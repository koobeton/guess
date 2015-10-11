package my.server.gamemechanics;

import my.server.base.GameReplica;
import my.server.resourcesystem.GameResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GameSession {

    private static final GameResource GAME_RESOURCE =
            (GameResource) ResourceFactory.instance().getResource("./data/GameResource.xml");
    private static final String LESS = GAME_RESOURCE.getLess();
    private static final String MORE = GAME_RESOURCE.getMore();

    private int userId;
    private int lowerBound;
    private int upperBound;
    private int goal;
    private int lastTurn;
    private String message;
    private AtomicInteger attemptsCount;
    private boolean gameOver;
    private long startTime;
    private long duration;

    public GameSession(int userId, int lowerBound, int upperBound) {
        this.userId = userId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        startTime = TimeHelper.getTime();
        goal = new Random(startTime).nextInt(upperBound - lowerBound + 1) + lowerBound;
        lastTurn = 0;
        message = null;
        attemptsCount = new AtomicInteger(0);
        gameOver = false;
        duration = 0;
    }

    public GameReplica handleTurn(int turn) {
        attemptsCount.incrementAndGet();
        int result = Integer.compare(goal, lastTurn = turn);
        message = (gameOver = result == 0) ? null : result < 0 ? LESS : MORE;
        return getReplica();
    }

    public GameReplica getReplica() {
        return new GameReplicaImpl(
                lowerBound,
                upperBound,
                lastTurn,
                message,
                attemptsCount.get(),
                gameOver
        );
    }

    public long getDuration() {
        if (!gameOver) {
            duration = TimeHelper.getTime() - startTime;
        }
        return duration;
    }

    public int getUserId() {
        return userId;
    }
}
