package my.server.gamemechanics;

import my.server.base.Address;
import my.server.base.Frontend;
import my.server.messagesystem.MsgToFrontend;

public class MsgGameSessionDuration extends MsgToFrontend {

    private int sessionId;
    private long gameSessionDuration;

    public MsgGameSessionDuration(Address from, Address to, int sessionId, long duration) {
        super(from, to);
        this.sessionId = sessionId;
        this.gameSessionDuration = duration;
    }

    @Override
    public void exec(Frontend frontend) {
        frontend.setGameSessionDuration(sessionId, gameSessionDuration);
    }
}
