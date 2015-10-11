package my.server.messagesystem;

import my.server.base.Address;
import my.server.base.AddressService;

public class AddressServiceImpl implements AddressService {

	private Address addressFrontend;
	private Address addressDBS;
    private Address addressGM;

    @Override
	public Address getAddressDBS() {
		return addressDBS;
	}

	public void setAddressDBS(Address addressDBS) {
		this.addressDBS = addressDBS;
	}

    @Override
	public Address getAddressFrontend() {
		return addressFrontend;
	}

	public void setAddressFrontend(Address addressFrontend) {
		this.addressFrontend = addressFrontend;
	}

	@Override
	public Address getAddressGM() {
		return addressGM;
	}

	public void setAddressGM(Address addressGM) {
        this.addressGM = addressGM;
	}
}
