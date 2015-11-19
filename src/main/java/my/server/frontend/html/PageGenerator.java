package my.server.frontend.html;

import my.server.base.Results;
import my.server.frontend.UserSession;
import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;

import java.util.List;

public class PageGenerator {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
    private static final int REFRESH_TIME = FRONTEND_RESOURCE.getRefreshTime();

    private static String staticPage(String head, String body, int sessionId) {
        return "<html>" +
                    "<head>" +
                        "<title>Guess the number</title>" +
                        head +
                    "</head>" +
                    "<body>" +
                        formPost(body, sessionId) +
                    "</body>" +
                "</html>";
    }

    private static String refreshablePage(String head, String body, int sessionId) {
        return "<html>" +
                    "<head>" +
                        "<title>Guess the number</title>" +
                        head +
                        "<script type='text/JavaScript'>" +
                            "function refresh(){" +
                                "document.forms[0].submit();" +
                            "}" +
                        "</script>" +
                    "</head>" +
                    "<body onload='setInterval(function(){refresh()}," + REFRESH_TIME + ");'>" +
                        formPost(body, sessionId) +
                    "</body>" +
                "</html>";
    }

    private static String formPost(String text, int sessionId) {
        return "<form method=\"post\"> " +
                    text +
                    "<input type=\"hidden\" name=\"sessionId\" value=\"" + sessionId +"\">" +
                "</form>";
    }

    private static String header(int sessionId, String name, Integer userId, Long duration) {
        return String.format("<hr>[Session ID: <b>%04d</b>] %s %s %s<hr>",
                        sessionId,
                        name != null ? "[User name: <b>" + name + "</b>]" : "",
                        userId != null ? "[User ID: <b>" + userId + "</b>]" : "",
                        duration != null
                                ? "[Duration: " +
                                    "<b>" +
                                        TimeHelper.formatTime(duration) +
                                    "</b>]"
                                : ""
                );
    }

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

    public static String getRequestNamePage(UserSession userSession) {
        int sessionId = userSession.getSessionId();
        return staticPage("",
                header(sessionId, null, null, null) +
                    "<p>Enter your name: " +
                        "<input name=\"userName\" required autofocus placeholder=\"Your name\">" +
                        "<input type=\"submit\" value=\"Send\">" +
                    "</p>",
                sessionId
        );
    }

    public static String getWaitAuthorizationPage(UserSession userSession) {
        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        return refreshablePage("",
                header(sessionId, name, null, null) +
                        "<p><b>Wait for authorization!</b></p>",
                sessionId
        );
    }

    public static String getAuthorizationOKPage(UserSession userSession) {
        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        int userId = userSession.getUserId();
        return staticPage("",
                header(sessionId, name, userId, null) +
                    "<p><b>Authorization OK!</b></p>" +
                    "<hr><input type=\"submit\" name=\"startGame\" value=\"Start game!\">",
                sessionId
        );
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
        return staticPage("",
                header(sessionId, name, userId, duration) +
                    "<p>Enter the number between <b>" + lower +"</b> and <b>" + upper + "</b>:</p>" +
                    "<p>" +
                        String.format("<input type=\"number\" name=\"turn\" min=\"%d\" max=\"%d\" step=\"1\" required autofocus placeholder=\"%d ... %d\">",
                                lower, upper, lower, upper) +
                        "<input type=\"submit\" value=\"Play!\">" +
                    "</p>" +
                    String.format("<hr>[Attempts: <b>%d</b>] %s",
                            attempts,
                            message != null ? "[Goal: <b>" + message + "</b>]" : ""
                    ),
                sessionId
        );
    }

    public static String getGameOverPage(UserSession userSession, List<Results> highScores) {
        int sessionId = userSession.getSessionId();
        String name = userSession.getUserName();
        int userId = userSession.getUserId();
        long duration = userSession.getGameSessionDuration();
        int goal = userSession.getGameReplica().getLastTurn();
        int attempts = userSession.getGameReplica().getAttempts();
        return staticPage("",
                header(sessionId, name, userId, duration) +
                        "<p><b>Game over!</b></p>" +
                        String.format("<p>You found <b>%d</b> with <b>%d</b> attempts.</p>", goal, attempts) +
                        "<hr>" + highScoresTable(userSession, highScores) +
                        "<hr><input type=\"submit\" name=\"startGame\" value=\"Play again!\">",
                sessionId
        );
    }
}
