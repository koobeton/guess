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
}
