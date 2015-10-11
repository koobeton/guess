package my.server.base;

import java.util.List;

public interface Frontend extends Abonent, Runnable {

    void setId(int sessionId, Integer id);
    void setReplica(int sessionId, GameReplica gameReplica);
    void setGameSessionDuration(int sessionId, long duration);
    void setHighScores(List<Results> highScores);
}
