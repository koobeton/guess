package my.server.frontend;

import my.server.base.*;
import my.server.frontend.html.PageGenerator;
import my.server.resourcesystem.DBResource;
import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static my.server.frontend.UserSession.State.*;

public class FrontendImpl extends AbstractHandler implements Frontend {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
    private static final DBResource DB_RESOURCE =
            (DBResource) ResourceFactory.instance().getResource("./data/DBResource.xml");
    private static final int UNAUTHORIZED_ID = DB_RESOURCE.getUnauthorizedId();
    private static final int LIMIT = DB_RESOURCE.getLimit();
    private static final String SESSION_ID = FRONTEND_RESOURCE.getSessionId();
    private static final String USER_NAME = FRONTEND_RESOURCE.getUserName();
    private static final String START_GAME = FRONTEND_RESOURCE.getStartGame();
    private static final String TURN = FRONTEND_RESOURCE.getTurn();
    private static final int SLEEP_TIME = FRONTEND_RESOURCE.getSleepTime();

    private Map<Integer, UserSession> sessionIdToUserSession = new ConcurrentHashMap<>();
    private Address address;
    private MessageSystem ms;
    private List<Results> highScores;

    public FrontendImpl(MessageSystem ms) {
        this.ms = ms;
        this.address = new Address();
        this.highScores = null;
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        baseRequest.setHandled(true);

        UserSession userSession;
        int sessionId;
        String answer = null;

        if (request.getParameter(SESSION_ID) == null) {
            userSession = new UserSession();
            sessionIdToUserSession.put(userSession.getSessionId(), userSession);
        } else {
            sessionId = Integer.parseInt(request.getParameter(SESSION_ID));
            userSession = sessionIdToUserSession.get(sessionId);
        }

        switch (userSession.getState()) {
            case INITIAL_STATE:
                userSession.setState(WAIT_FOR_NAME);
                answer = PageGenerator.getRequestNamePage(userSession);
                break;
            case WAIT_FOR_NAME:
                String providedUserName = request.getParameter(USER_NAME);
                if (providedUserName != null) {
                    userSession.setUserName(providedUserName);
                    Address addressDBS = ms.getAddressService().getAddressDBS();
                    ms.sendMessage(new MsgGetUserId(getAddress(), addressDBS, userSession.getSessionId(), providedUserName));
                    userSession.setState(WAIT_FOR_AUTHORIZATION);
                    answer = PageGenerator.getWaitAuthorizationPage(userSession);
                }
                break;
            case WAIT_FOR_AUTHORIZATION:
                if (userSession.getUserId() != UNAUTHORIZED_ID) {
                    userSession.setState(AUTHORIZATION_OK);
                }
                answer = PageGenerator.getWaitAuthorizationPage(userSession);
                break;
            case AUTHORIZATION_OK:
                if (request.getParameter(START_GAME) == null) {
                    answer = PageGenerator.getAuthorizationOKPage(userSession);
                } else {
                    Address addressGM = ms.getAddressService().getAddressGM();
                    ms.sendMessage(new MsgStartGameSession(
                            getAddress(),
                            addressGM,
                            userSession.getSessionId(),
                            userSession.getUserId())
                    );
                    answer = handleReplica(userSession);
                }
                break;
            case GAME_STARTED:
                if (request.getParameter(TURN) == null) {
                    userSession.restoreGameReplica();
                } else {
                    int turn = Integer.parseInt(request.getParameter(TURN));
                    Address addressGM = ms.getAddressService().getAddressGM();
                    ms.sendMessage(new MsgHandleTurn(getAddress(), addressGM, userSession.getSessionId(), turn));
                }
                answer = handleReplica(userSession);
                break;
            case GAME_OVER:
                if (request.getParameter(START_GAME) == null) {
                    userSession.restoreGameReplica();
                } else {
                    Address addressGM = ms.getAddressService().getAddressGM();
                    ms.sendMessage(new MsgStartGameSession(
                            getAddress(),
                            addressGM,
                            userSession.getSessionId(),
                            userSession.getUserId())
                    );
                }
                answer = handleReplica(userSession);
                break;
        }

        response.getWriter().println(answer);
    }

    private String handleReplica(UserSession userSession) {

        String answer;

        while (userSession.getGameReplica() == null) {
            TimeHelper.sleep(SLEEP_TIME);
        }

        if (userSession.getGameReplica().isGameOver()) {
            userSession.setState(GAME_OVER);
            Address addressDBS = ms.getAddressService().getAddressDBS();
            ms.sendMessage(new MsgGetHighScores(getAddress(), addressDBS, LIMIT));
            while (highScores == null) {
                TimeHelper.sleep(SLEEP_TIME);
            }
            answer = PageGenerator.getGameOverPage(userSession, highScores);
            highScores = null;
        } else {
            userSession.setState(GAME_STARTED);
            answer = PageGenerator.getGamePage(userSession);
        }
        userSession.setGameReplica(null);
        return answer;
    }

    @Override
    public void run() {
        while(true){
            ms.execForAbonent(this);
            TimeHelper.sleep(SLEEP_TIME);
        }
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setId(int sessionId, Integer id) {
        sessionIdToUserSession.get(sessionId).setUserId(id);
    }

    @Override
    public void setReplica(int sessionId, GameReplica gameReplica) {
        sessionIdToUserSession.get(sessionId).setGameReplica(gameReplica);
    }

    @Override
    public void setGameSessionDuration(int sessionId, long duration) {
        sessionIdToUserSession.get(sessionId).setGameSessionDuration(duration);
    }

    @Override
    public void setHighScores(List<Results> highScores) {
        this.highScores = highScores;
    }
}
