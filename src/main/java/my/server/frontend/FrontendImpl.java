package my.server.frontend;

import my.server.base.*;
import my.server.frontend.html.PageGenerator;
import my.server.frontend.ws.UserWebSocketCreator;
import my.server.frontend.ws.WebSocketService;
import my.server.resourcesystem.DBResource;
import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static my.server.frontend.UserSession.State.*;

public class FrontendImpl extends WebSocketServlet implements Frontend {

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
    private static final long WS_IDLE_TIMEOUT = FRONTEND_RESOURCE.getWebSocketIdleTimeout();

    private Map<Integer, UserSession> sessionIdToUserSession = new ConcurrentHashMap<>();
    private Address address;
    private MessageSystem ms;
    private List<Results> highScores;
    private WebSocketService webSocketService;

    public FrontendImpl(MessageSystem ms) {
        this.ms = ms;
        this.address = new Address();
        this.highScores = null;
        this.webSocketService = new WebSocketService();
        new Thread(webSocketService).start();
    }

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.getPolicy().setIdleTimeout(WS_IDLE_TIMEOUT);
        webSocketServletFactory.setCreator(new UserWebSocketCreator(webSocketService));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String sessionIdParameter = req.getParameter(SESSION_ID);
        UserSession userSession = getUserSession(sessionIdParameter);
        String answer = null;

        switch (userSession.getState()) {
            case INITIAL_STATE:
                answer = handleInitialState(userSession,
                        PageGenerator::getRequestNamePage);
                break;
            case WAIT_FOR_NAME:
                String userNameParameter = req.getParameter(USER_NAME);
                answer = handleWaitForName(userSession,
                        PageGenerator::getWaitAuthorizationPage,
                        userNameParameter);
                break;
            case WAIT_FOR_AUTHORIZATION:
                answer = handleWaitForAuthorization(userSession,
                        PageGenerator::getWaitAuthorizationPage);
                break;
            case AUTHORIZATION_OK:
                String startGameParameter = req.getParameter(START_GAME);
                answer = handleAuthorizationOK(userSession,
                        PageGenerator::getAuthorizationOKPage,
                        PageGenerator::getGamePage,
                        PageGenerator::getGameOverPage,
                        startGameParameter);
                break;
            case GAME_STARTED:
                String turnParameter = req.getParameter(TURN);
                answer = handleGameStarted(userSession,
                        PageGenerator::getGamePage,
                        PageGenerator::getGameOverPage,
                        turnParameter);
                break;
            case GAME_OVER:
                startGameParameter = req.getParameter(START_GAME);
                answer = handleGameOver(userSession,
                        PageGenerator::getGamePage,
                        PageGenerator::getGameOverPage,
                        startGameParameter);
                break;
        }

        resp.getWriter().println(answer);
    }

    private UserSession getUserSession(String sessionId) {

        UserSession userSession;

        if (sessionId == null) {
            userSession = new UserSession();
            sessionIdToUserSession.put(userSession.getSessionId(), userSession);
        } else {
            userSession = sessionIdToUserSession.get(Integer.parseInt(sessionId));
        }

        return userSession;
    }

    private String handleInitialState(UserSession userSession,
                                      Function<UserSession, String> replyToSender) {

        userSession.setState(WAIT_FOR_NAME);
        return replyToSender.apply(userSession);
    }

    private String handleWaitForName(UserSession userSession,
                                     Function<UserSession, String> replyToSender,
                                     String providedUserName) {

        if (providedUserName != null) {
            userSession.setUserName(providedUserName);
            Address addressDBS = ms.getAddressService().getAddressDBS();
            ms.sendMessage(new MsgGetUserId(getAddress(), addressDBS, userSession.getSessionId(), providedUserName));
            userSession.setState(WAIT_FOR_AUTHORIZATION);
        }
        return replyToSender.apply(userSession);
    }

    private String handleWaitForAuthorization(UserSession userSession,
                                              Function<UserSession, String> replyToSender) {

        if (userSession.getUserId() != UNAUTHORIZED_ID) {
            userSession.setState(AUTHORIZATION_OK);
        }
        return replyToSender.apply(userSession);
    }

    private String handleAuthorizationOK(UserSession userSession,
                                         Function<UserSession, String> replyToSenderAuthorizationOK,
                                         Function<UserSession, String> replyToSenderGameStarted,
                                         BiFunction<UserSession, List<Results>, String> replyToSenderGameOver,
                                         String startGame) {

        String answer;

        if (startGame == null) {
            answer = replyToSenderAuthorizationOK.apply(userSession);
        } else {
            Address addressGM = ms.getAddressService().getAddressGM();
            ms.sendMessage(new MsgStartGameSession(
                    getAddress(),
                    addressGM,
                    userSession.getSessionId(),
                    userSession.getUserId())
            );
            answer = handleReplica(userSession, replyToSenderGameStarted, replyToSenderGameOver);
        }

        return answer;
    }

    private String handleGameStarted(UserSession userSession,
                                     Function<UserSession, String> replyToSenderGameStarted,
                                     BiFunction<UserSession, List<Results>, String> replyToSenderGameOver,
                                     String turn) {

        if (turn == null) {
            userSession.restoreGameReplica();
        } else {
            Address addressGM = ms.getAddressService().getAddressGM();
            ms.sendMessage(new MsgHandleTurn(
                    getAddress(),
                    addressGM,
                    userSession.getSessionId(),
                    Integer.parseInt(turn))
            );
        }
        return handleReplica(userSession, replyToSenderGameStarted, replyToSenderGameOver);
    }

    private String handleGameOver(UserSession userSession,
                                  Function<UserSession, String> replyToSenderGameStarted,
                                  BiFunction<UserSession, List<Results>, String> replyToSenderGameOver,
                                  String startGame) {

        if (startGame == null) {
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
        return handleReplica(userSession, replyToSenderGameStarted, replyToSenderGameOver);
    }

    private String handleReplica(UserSession userSession,
                                 Function<UserSession, String> replyToSenderGameStarted,
                                 BiFunction<UserSession, List<Results>, String> replyToSenderGameOver) {

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
            answer = replyToSenderGameOver.apply(userSession, highScores);
            highScores = null;
        } else {
            userSession.setState(GAME_STARTED);
            answer = replyToSenderGameStarted.apply(userSession);
        }
        userSession.setGameReplica(null);
        return answer;
    }

    @Override
    public void run() {
        while(true) {
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
        UserSession userSession = sessionIdToUserSession.get(sessionId);
        userSession.setGameSessionDuration(duration);
        webSocketService.sendMessage(sessionId, PageGenerator.getDurationJSON(userSession));
    }

    @Override
    public void setHighScores(List<Results> highScores) {
        this.highScores = highScores;
    }
}
