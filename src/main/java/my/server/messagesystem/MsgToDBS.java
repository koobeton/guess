package my.server.messagesystem;

import my.server.base.Abonent;
import my.server.base.Address;
import my.server.base.DBService;
import my.server.base.Msg;

public abstract class MsgToDBS extends Msg {

	public MsgToDBS(Address from, Address to) {
		super(from, to);
	}

	@Override
	public void exec(Abonent abonent) {
		if(abonent instanceof DBService) {
			exec((DBService) abonent);
		}
	}

	public abstract void exec(DBService dbService);
}
