package my.server.gamemechanics;

import my.server.base.Address;
import my.server.base.DBService;
import my.server.messagesystem.MsgToDBS;

public class MsgSaveResult extends MsgToDBS {

    private int userId;
    private int attempts;
    private long time;

    public MsgSaveResult(Address from, Address to, int userId, int attempts, long time) {
        super(from, to);
        this.userId = userId;
        this.attempts = attempts;
        this.time = time;
    }

    @Override
    public void exec(DBService dbService) {
        dbService.addResult(userId, attempts, time);
    }
}
