package my.server.base;

import java.util.List;

public interface DBService extends Abonent, Runnable {

    Integer getUserId(String name);
    void addResult(int userId, int attempts, long time);
    List<Results> getResults(int limit);
    MessageSystem getMessageSystem();
}
