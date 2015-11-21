package my.server.frontend.html;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import my.server.base.Results;
import my.server.frontend.UserSession;
import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageGenerator {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
    private static final String DM_SESSION_ID = FRONTEND_RESOURCE.getDmSessionId();
    private static final String DM_NAME = FRONTEND_RESOURCE.getDmName();
    private static final String DM_USER_ID = FRONTEND_RESOURCE.getDmUserId();
    private static final String DM_DURATION = FRONTEND_RESOURCE.getDmDuration();
    private static final String DM_REQUEST_NAME = FRONTEND_RESOURCE.getDmRequestName();
    private static final String DM_REFRESHABLE = FRONTEND_RESOURCE.getDmRefreshable();
    private static final String DM_WAIT_AUTHORIZATION = FRONTEND_RESOURCE.getDmWaitAuthorization();
    private static final String DM_AUTHORIZATION_OK = FRONTEND_RESOURCE.getDmAuthorizationOK();
    private static final String DM_GAME_REPLICA = FRONTEND_RESOURCE.getDmGameReplica();
    private static final String DM_LOWER = FRONTEND_RESOURCE.getDmLower();
    private static final String DM_UPPER = FRONTEND_RESOURCE.getDmUpper();
    private static final String DM_MESSAGE = FRONTEND_RESOURCE.getDmMessage();
    private static final String DM_ATTEMPTS = FRONTEND_RESOURCE.getDmAttempts();
    private static final String DM_GAME_OVER = FRONTEND_RESOURCE.getDmGameOver();
    private static final String DM_GOAL = FRONTEND_RESOURCE.getDmGoal();
    private static final String DM_HIGH_SCORES = FRONTEND_RESOURCE.getDmHighScores();

    private static final String TEMPLATE_LOADER = FRONTEND_RESOURCE.getTemplateLoader();
    private static final String CORE_PAGE = FRONTEND_RESOURCE.getCorePage();
    private static final Configuration CFG;

    static {
        CFG = new Configuration(Configuration.VERSION_2_3_23);
        try {
            CFG.setDirectoryForTemplateLoading(new File(TEMPLATE_LOADER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPage(Map<String, Object> dataModel) {
        Writer stream = new StringWriter();
        try {
            Template template = CFG.getTemplate(CORE_PAGE);
            template.process(dataModel, stream);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return stream.toString();
    }

    private static Map<String, Object> getDataModel(int sessionId, String name, Integer userId, Long duration) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(DM_SESSION_ID, String.format("%04d", sessionId));
        dataModel.put(DM_NAME, name);
        dataModel.put(DM_USER_ID, userId);
        dataModel.put(DM_DURATION,
                duration != null ? TimeHelper.formatTime(duration) : null);
        return dataModel;
    }

    private static Map<String, Object> getDataModel(int sessionId, String name, Integer userId) {
        return getDataModel(sessionId, name, userId, null);
    }

    private static Map<String, Object> getDataModel(int sessionId, String name) {
        return getDataModel(sessionId, name, null, null);
    }

    private static Map<String, Object> getDataModel(int sessionId) {
        return getDataModel(sessionId, null, null, null);
    }

    public static String getRequestNamePage(UserSession userSession) {

        int sessionId = userSession.getSessionId();

        Map<String, Object> dataModel = getDataModel(sessionId);
        dataModel.put(DM_REQUEST_NAME, true);
        return getPage(dataModel);
    }

    public static String getWaitAuthorizationPage(UserSession userSession) {

        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();

        Map<String, Object> dataModel = getDataModel(sessionId, name);
        dataModel.put(DM_REFRESHABLE, true);
        dataModel.put(DM_WAIT_AUTHORIZATION, true);
        return getPage(dataModel);
    }

    public static String getAuthorizationOKPage(UserSession userSession) {

        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        int userId = userSession.getUserId();

        Map<String, Object> dataModel = getDataModel(sessionId, name, userId);
        dataModel.put(DM_AUTHORIZATION_OK, true);
        return getPage(dataModel);
    }

    public static String getGamePage(UserSession userSession) {

        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        int userId = userSession.getUserId();
        long duration = userSession.getGameSessionDuration();
        int lower = userSession.getGameReplica().getLowerBound();
        int upper = userSession.getGameReplica().getUpperBound();
        String message = userSession.getGameReplica().getMessage();
        int attempts = userSession.getGameReplica().getAttempts();

        Map<String, Object> dataModel = getDataModel(sessionId, name, userId, duration);
        Map<String, Object> gameReplicaHash = new HashMap<>();
        gameReplicaHash.put(DM_LOWER, lower);
        gameReplicaHash.put(DM_UPPER, upper);
        gameReplicaHash.put(DM_MESSAGE, message);
        gameReplicaHash.put(DM_ATTEMPTS, attempts);
        dataModel.put(DM_GAME_REPLICA, gameReplicaHash);
        return getPage(dataModel);
    }

    public static String getGameOverPage(UserSession userSession, List<Results> highScores) {
        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        int userId = userSession.getUserId();
        long duration = userSession.getGameSessionDuration();
        int goal = userSession.getGameReplica().getLastTurn();
        int attempts = userSession.getGameReplica().getAttempts();

        Map<String, Object> dataModel = getDataModel(sessionId, name, userId, duration);
        Map<String, Object> gameOverHash = new HashMap<>();
        gameOverHash.put(DM_GOAL, goal);
        gameOverHash.put(DM_ATTEMPTS, attempts);
        List<Map<String, Object>> highScoresSequence = new ArrayList<>();
        for (Results score : highScores) {
            Map<String, Object> scoreHash = new HashMap<>();
            scoreHash.put(DM_NAME, score.getName());
            scoreHash.put(DM_ATTEMPTS, score.getAttempts());
            scoreHash.put(DM_DURATION, TimeHelper.formatTime(score.getTime()));
            highScoresSequence.add(scoreHash);
        }
        gameOverHash.put(DM_HIGH_SCORES, highScoresSequence);
        dataModel.put(DM_GAME_OVER, gameOverHash);
        return getPage(dataModel);
    }
}
