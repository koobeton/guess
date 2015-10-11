package my.server.frontend;

import my.server.base.Address;
import my.server.base.GameMechanics;
import my.server.base.GameReplica;
import my.server.messagesystem.MsgToGM;

public class MsgHandleTurn extends MsgToGM {

    private int sessionId;
    private int turn;

    public MsgHandleTurn(Address from, Address to, int sessionId, int turn) {
        super(from, to);
        this.sessionId = sessionId;
        this.turn = turn;
    }

    @Override
    public void exec(GameMechanics gameMechanics) {
        GameReplica gameReplica = gameMechanics.handleTurn(sessionId, turn);
        gameMechanics.getMessageSystem().sendMessage(new MsgUpdateGame(getTo(), getFrom(), sessionId, gameReplica));
    }
}
