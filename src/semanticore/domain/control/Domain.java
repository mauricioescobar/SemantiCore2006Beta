package semanticore.domain.control;

import java.io.Serializable;

public class Domain implements Serializable {
    protected String address;

    protected String port;

    protected String domainName;

    protected boolean subDomain;

    protected boolean connected;

    private int metric = ControlBridge.MAX_METRIC;

    public Domain(String address, String port, String domainName) {
	this.address = address;
	this.port = port;
	this.domainName = domainName;

	this.subDomain = false;
	this.connected = false;
    }

    public String getAddress() {
	return address;
    }

    public String getPort() {
	return port;
    }

    public synchronized int getMetric() {
	return metric;
    }

    public synchronized void setMetric(int metric) {
	this.metric = metric;
    }

    public synchronized void incMetric() {
	if (metric > ControlBridge.MAX_METRIC) {
	    metric = ControlBridge.MAX_METRIC;
	    return;
	}

	this.metric++;
    }

    public synchronized void decMetric() {
	this.metric--;
    }

    public String getDomainName() {
	return domainName;
    }

    public boolean isConnected() {
	return connected;
    }

    public void setConnected(boolean connected) {
	this.connected = connected;
    }

    public boolean isSubDomain() {
	return subDomain;
    }

    public void setSubDomain(boolean subDomain) {
	this.subDomain = subDomain;
    }
}
