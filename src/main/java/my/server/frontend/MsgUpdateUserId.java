package my.server.frontend;

import my.server.base.Address;
import my.server.base.Frontend;
import my.server.messagesystem.MsgToFrontend;

public class MsgUpdateUserId extends MsgToFrontend {

	private int sessionId;
	private Integer id;
	
	public MsgUpdateUserId(Address from, Address to, int sessionId, Integer id) {
		super(from, to);
		this.sessionId = sessionId;
		this.id = id;
	}

	@Override
	public void exec(Frontend frontend) {
		frontend.setId(sessionId, id);
	}
}
