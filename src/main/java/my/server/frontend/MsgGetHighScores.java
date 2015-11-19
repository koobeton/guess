package my.server.frontend;

import my.server.base.Address;
import my.server.base.DBService;
import my.server.base.Results;
import my.server.messagesystem.MsgToDBS;

import java.util.List;

public class MsgGetHighScores extends MsgToDBS {

    private int limit;

    public MsgGetHighScores(Address from, Address to, int limit) {
        super(from, to);
        this.limit = limit;
    }

    @Override
    public void exec(DBService dbService) {
        List<Results> highScores = dbService.getResults(limit);
        dbService.getMessageSystem().sendMessage(new MsgUpdateHighScores(getTo(), getFrom(), highScores));
    }
}
