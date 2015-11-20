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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageGenerator {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
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
        dataModel.put("sessionId", String.format("%04d", sessionId));
        dataModel.put("name", name);
        dataModel.put("userId", userId);
        dataModel.put("duration",
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

    //TODO
    private static String highScoresTable(UserSession userSession, List<Results> highScores) {
        String thisSessionName = userSession.getUserName();
        int thisSessionAttempts = userSession.getGameReplica().getAttempts();
        long thisSessionTime = userSession.getGameSessionDuration();
        StringBuilder table = new StringBuilder();
        int place = 0;
        table
                .append("<table border cellpadding=\"3\">")
                .append("<caption><b>High Scores:<b><caption>")
                .append("<tr><th>#</th><th>Name</th><th>Attempts</th><th>Time</th></tr>");
                for (Results score : highScores) {
                    String currentName = score.getName();
                    int currentAttempts = score.getAttempts();
                    long currentTime = score.getTime();
                    String bgcolor = currentName.equals(thisSessionName)
                            && currentAttempts == thisSessionAttempts
                            && currentTime == thisSessionTime
                            ? "bgcolor=\"yellow\""
                            : "";
                    table
                            .append(String.format("<tr %s>", bgcolor))
                            .append(String.format("<td align=\"center\">%d</td>",
                                    ++place))
                            .append(String.format("<td>%s</td>",
                                    currentName))
                            .append(String.format("<td align=\"center\">%d</td>",
                                    currentAttempts))
                            .append(String.format("<td align=\"center\">%s</td>",
                                    TimeHelper.formatTime(currentTime)))
                            .append("</tr>");
                }
        table.append("</table>");
        return table.toString();
    }

    //TODO
    public static String getRequestNamePage(UserSession userSession) {
        int sessionId = userSession.getSessionId();
        Map<String, Object> dataModel = getDataModel(sessionId);
        dataModel.put("requestName", true);

        dataModel.put("body", "");
        return getPage(dataModel);
    }

    public static String getWaitAuthorizationPage(UserSession userSession) {
        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();

        Map<String, Object> dataModel = getDataModel(sessionId, name);
        dataModel.put("refreshable", true);

        String body = "<p><b>Wait for authorization!</b></p>";

        dataModel.put("body", body);
        return getPage(dataModel);

    }

    public static String getAuthorizationOKPage(UserSession userSession) {
        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        int userId = userSession.getUserId();

        Map<String, Object> dataModel = getDataModel(sessionId, name, userId);

        String body = "<p><b>Authorization OK!</b></p>" +
                "<hr><input type=\"submit\" name=\"startGame\" value=\"Start game!\">";

        dataModel.put("body", body);
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

        String body = "<p>Enter the number between <b>" + lower +"</b> and <b>" + upper + "</b>:</p>" +
                "<p>" +
                String.format("<input type=\"number\" name=\"turn\" min=\"%d\" max=\"%d\" step=\"1\" required autofocus placeholder=\"%d ... %d\">",
                        lower, upper, lower, upper) +
                "<input type=\"submit\" value=\"Play!\">" +
                "</p>" +
                String.format("<hr>[Attempts: <b>%d</b>] %s",
                        attempts,
                        message != null ? "[Goal: <b>" + message + "</b>]" : ""
                );

        dataModel.put("body", body);
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

        String body = "<p><b>Game over!</b></p>" +
                String.format("<p>You found <b>%d</b> with <b>%d</b> attempts.</p>", goal, attempts) +
                "<hr>" + highScoresTable(userSession, highScores) +
                "<hr><input type=\"submit\" name=\"startGame\" value=\"Play again!\">";

        dataModel.put("body", body);
        return getPage(dataModel);

    }
}
