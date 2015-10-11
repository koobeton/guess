package my.server.base;

public interface GameMechanics extends Abonent, Runnable {

    GameReplica startNewGame(int sessionId, int userId);
    GameReplica handleTurn(int sessionId, int turn);
    MessageSystem getMessageSystem();
}
