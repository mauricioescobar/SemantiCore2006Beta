package semanticore.domain.model;

import java.util.Observable;

import semanticore.domain.services.platform.ServiceListener;
import semanticore.domain.services.platform.ServiceRequester;

public abstract class Service extends Observable implements Runnable {
    protected ServiceListener listener;

    protected ServiceRequester requester;

    protected String address;

    protected String port;

    protected Service(String address, String port) {
	this.listener = null;
	this.requester = null;
	this.address = address;
	this.port = port;
    }

    protected void setServiceRequester(ServiceRequester sReq) {
	this.requester = sReq;
    }

    protected void setServiceListener(ServiceListener sList) {
	this.listener = sList;
    }

    public void setPort(int p) {
	this.port = Integer.toString(p);
    }

    public String getAddress() {
	return this.address;
    }

    public String getPort() {
	return this.port;
    }

    public ServiceRequester getRequester() {
	return this.requester;
    }
}
