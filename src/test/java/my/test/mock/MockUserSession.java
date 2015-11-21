package my.test.mock;

import my.server.base.GameReplica;
import my.server.frontend.UserSession;
import my.server.gamemechanics.GameReplicaImpl;

public class MockUserSession extends UserSession {

    @Override
    public GameReplica getGameReplica() {
        return new GameReplicaImpl(0, 0, 0, "", 0, true);
    }

    @Override
    public String getUserName() {
        return "";
    }
}
