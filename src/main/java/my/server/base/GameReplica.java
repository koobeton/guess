package my.server.base;

public interface GameReplica {

    int getLowerBound();
    int getUpperBound();
    int getLastTurn();
    String getMessage();
    int getAttempts();
    boolean isGameOver();
}
