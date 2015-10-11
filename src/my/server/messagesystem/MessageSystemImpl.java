package my.server.messagesystem;

import my.server.base.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSystemImpl implements MessageSystem {

	private Map<Address, ConcurrentLinkedQueue<Msg>> messages = new HashMap<>();
	private AddressServiceImpl addressService = new AddressServiceImpl();

    public void addFrontend(Abonent abonent) {
        addressService.setAddressFrontend(abonent.getAddress());
        messages.put(abonent.getAddress(), new ConcurrentLinkedQueue<Msg>());
    }

    public void addDBService(Abonent abonent) {
        addressService.setAddressDBS(abonent.getAddress());
        messages.put(abonent.getAddress(), new ConcurrentLinkedQueue<Msg>());
    }

    public void addGameMechanics(Abonent abonent) {
        addressService.setAddressGM(abonent.getAddress());
        messages.put(abonent.getAddress(), new ConcurrentLinkedQueue<Msg>());
    }

    @Override
	public void sendMessage(Msg message) {
		Queue<Msg> messageQueue = messages.get(message.getTo());
		messageQueue.add(message);		
	}

    @Override
	public void execForAbonent(Abonent abonent) {
		Queue<Msg> messageQueue = messages.get(abonent.getAddress());
		if(messageQueue == null) {
			return;
		}
		while(!messageQueue.isEmpty()) {
			Msg message = messageQueue.poll();
			message.exec(abonent);
		}
	}

    @Override
	public AddressService getAddressService() {
		return addressService;
	}
}
