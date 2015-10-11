package my.server.frontend;

import my.server.base.Address;
import my.server.base.Frontend;
import my.server.base.GameReplica;
import my.server.messagesystem.MsgToFrontend;

public class MsgUpdateGame extends MsgToFrontend {

    private int sessionId;
    private GameReplica gameReplica;

    public MsgUpdateGame(Address from, Address to, int sessionId, GameReplica gameReplica) {
        super(from, to);
        this.sessionId = sessionId;
        this.gameReplica = gameReplica;
    }

    @Override
    public void exec(Frontend frontend) {
        frontend.setReplica(sessionId, gameReplica);
    }
}
