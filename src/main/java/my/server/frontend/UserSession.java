package my.server.frontend;

import my.server.base.GameReplica;
import my.server.resourcesystem.DBResource;
import my.server.resourcesystem.ResourceFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class UserSession {

    private static final DBResource DB_RESOURCE =
            (DBResource) ResourceFactory.instance().getResource("./data/DBResource.xml");
    private static final int UNAUTHORIZED_ID = DB_RESOURCE.getUnauthorizedId();

    private static AtomicInteger sessionIdCount = new AtomicInteger(0);

    private int sessionId;
    private String userName = null;
    private int userId = UNAUTHORIZED_ID;
    private GameReplica gameReplica = null;
    private GameReplica storedGameReplica = null;
    private long gameSessionDuration;
    private State state;

    public UserSession() {
        sessionId = sessionIdCount.incrementAndGet();
        state = State.INITIAL_STATE;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setGameReplica(GameReplica gameReplica) {
        storedGameReplica = this.gameReplica;
        this.gameReplica = gameReplica;
    }

    public GameReplica getGameReplica() {
        return gameReplica;
    }

    public void restoreGameReplica() {
        gameReplica = storedGameReplica;
    }

    public long getGameSessionDuration() {
        return gameSessionDuration;
    }

    public void setGameSessionDuration(long gameSessionDuration) {
        this.gameSessionDuration = gameSessionDuration;
    }

    public enum State {
        INITIAL_STATE,
        WAIT_FOR_NAME,
        WAIT_FOR_AUTHORIZATION,
        AUTHORIZATION_OK,
        GAME_STARTED,
        GAME_OVER
    }
}
