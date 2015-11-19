package my.server.frontend;

import my.server.base.Address;
import my.server.base.Frontend;
import my.server.base.Results;
import my.server.messagesystem.MsgToFrontend;

import java.util.List;

public class MsgUpdateHighScores extends MsgToFrontend {

    private List<Results> highScores;

    public MsgUpdateHighScores(Address from, Address to, List<Results> highScores) {
        super(from, to);
        this.highScores = highScores;
    }

    @Override
    public void exec(Frontend frontend) {
        frontend.setHighScores(highScores);
    }
}
