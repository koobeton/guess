package my.server.messagesystem;

import my.server.base.Abonent;
import my.server.base.Address;
import my.server.base.GameMechanics;
import my.server.base.Msg;

public abstract class MsgToGM extends Msg {

    public MsgToGM(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if(abonent instanceof GameMechanics) {
            exec((GameMechanics)abonent);
        }
    }

    public abstract void exec(GameMechanics gameMechanics);
}
