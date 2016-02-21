package my.server.resourcesystem;

import my.server.base.Resource;

public class FrontendResource implements Resource {

    private String sessionId;
    private String userName;
    private String startGame;
    private String turn;
    private int sleepTime;
    private String templateLoader;
    private String corePage;
    private String dmSessionId;
    private String dmName;
    private String dmUserId;
    private String dmDuration;
    private String dmRequestName;
    private String dmRefreshable;
    private String dmWaitAuthorization;
    private String dmAuthorizationOK;
    private String dmGameReplica;
    private String dmLower;
    private String dmUpper;
    private String dmMessage;
    private String dmAttempts;
    private String dmGameOver;
    private String dmGoal;
    private String dmHighScores;
    private String dmTimeFormat;
    private String dmTimeFormatMs;
    private long webSocketIdleTimeout;

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getStartGame() {
        return startGame;
    }

    public String getTurn() {
        return turn;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public String getTemplateLoader() {
        return templateLoader;
    }

    public String getCorePage() {
        return corePage;
    }

    public String getDmHighScores() {
        return dmHighScores;
    }

    public String getDmSessionId() {
        return dmSessionId;
    }

    public String getDmName() {
        return dmName;
    }

    public String getDmUserId() {
        return dmUserId;
    }

    public String getDmDuration() {
        return dmDuration;
    }

    public String getDmRequestName() {
        return dmRequestName;
    }

    public String getDmRefreshable() {
        return dmRefreshable;
    }

    public String getDmWaitAuthorization() {
        return dmWaitAuthorization;
    }

    public String getDmAuthorizationOK() {
        return dmAuthorizationOK;
    }

    public String getDmGameReplica() {
        return dmGameReplica;
    }

    public String getDmLower() {
        return dmLower;
    }

    public String getDmUpper() {
        return dmUpper;
    }

    public String getDmMessage() {
        return dmMessage;
    }

    public String getDmAttempts() {
        return dmAttempts;
    }

    public String getDmGameOver() {
        return dmGameOver;
    }

    public String getDmGoal() {
        return dmGoal;
    }

    public String getDmTimeFormat() {
        return dmTimeFormat;
    }

    public String getDmTimeFormatMs() {
        return dmTimeFormatMs;
    }

    public long getWebSocketIdleTimeout() {
        return webSocketIdleTimeout;
    }
}
