package my.server.frontend;

import my.server.base.Address;
import my.server.base.DBService;
import my.server.messagesystem.MsgToDBS;

public class MsgGetUserId extends MsgToDBS {

	private int sessionId;
	private String name;

	public MsgGetUserId(Address from, Address to, int sessionId, String name) {
		super(from, to);
		this.sessionId = sessionId;
		this.name = name;
	}

    @Override
	public void exec(DBService dbService) {
		Integer id = dbService.getUserId(name);
		dbService.getMessageSystem().sendMessage(new MsgUpdateUserId(getTo(), getFrom(), sessionId, id));
	}
}
