package my.server.resourcesystem;

import my.server.base.Resource;

public class GameResource implements Resource {

    private int lowerBound;
    private int upperBound;
    private String less;
    private String more;
    private int sleepTime;

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public String getLess() {
        return less;
    }

    public String getMore() {
        return more;
    }

    public int getSleepTime() {
        return sleepTime;
    }
}
