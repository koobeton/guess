package my.server.gamemechanics;

import my.server.base.GameReplica;

public class GameReplicaImpl implements GameReplica {

    private int lowerBound;
    private int upperBound;
    private int lastTurn;
    private String message;
    private int attempts;
    private boolean gameOver;

    public GameReplicaImpl(int lowerBound, int upperBound, int lastTurn, String message, int attempts, boolean gameOver) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lastTurn = lastTurn;
        this.message = message;
        this.attempts = attempts;
        this.gameOver = gameOver;
    }

    @Override
    public int getLowerBound() {
        return lowerBound;
    }

    @Override
    public int getUpperBound() {
        return upperBound;
    }

    @Override
    public int getLastTurn() {
        return lastTurn;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getAttempts() {
        return attempts;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }
}
