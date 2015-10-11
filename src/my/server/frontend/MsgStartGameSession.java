package my.server.frontend;

import my.server.base.Address;
import my.server.base.GameMechanics;
import my.server.base.GameReplica;
import my.server.messagesystem.MsgToGM;

public class MsgStartGameSession extends MsgToGM {

    private int sessionId;
    private int userId;

    public MsgStartGameSession(Address from, Address to, int sessionId, int userId) {
        super(from, to);
        this.sessionId = sessionId;
        this.userId = userId;
    }

    @Override
    public void exec(GameMechanics gameMechanics) {
        GameReplica gameReplica = gameMechanics.startNewGame(sessionId, userId);
        gameMechanics.getMessageSystem().sendMessage(new MsgUpdateGame(getTo(), getFrom(), sessionId, gameReplica));
    }
}
