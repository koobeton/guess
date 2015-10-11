package my.server.messagesystem;

import my.server.base.Abonent;
import my.server.base.Address;
import my.server.base.Frontend;
import my.server.base.Msg;

public abstract class MsgToFrontend extends Msg {

	public MsgToFrontend(Address from, Address to) {
		super(from, to);
	}

	@Override
	public void exec(Abonent abonent) {
		if(abonent instanceof Frontend) {
			exec((Frontend)abonent);
		}
	}
	
	public abstract void exec(Frontend frontend);
}
