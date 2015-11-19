package my.server.resourcesystem;

import my.server.base.Resource;

public class DBResource implements Resource {

    private int unauthorizedId;
    private int sleepTime;
    private String dbName;
    private String login;
    private String password;
    private int limit;

    public int getUnauthorizedId() {
        return unauthorizedId;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public String getDBName() {
        return dbName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getLimit() {
        return limit;
    }
}
