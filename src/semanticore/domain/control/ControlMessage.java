package semanticore.domain.control;

import semanticore.domain.model.Message;

final public class ControlMessage extends Message {
    public static final int REGISTER_DOMAIN_PART = 1;

    public static final int REGISTER_DOMAIN_PART_REFUSED = 2;

    public static final int NEW_DOMAIN_PART = 3;

    public static final int ALIVE = 4;

    public static final int AGENT_UPDATE = 6;

    public static final int PART_OF_AGENT = 7;

    public static final int AGENT_INTERNAL_TRANSMISSION = 8;

    public static final int NEW_DOMAIN = 9;

    public static final int REGISTER_DOMAIN = 10;

    public static final int DOMAIN_REGISTERED = 11;

    public static final int DOMAIN_ALIVE = 12;

    public static final int MOBILE_AGENT = 13;

    public static final int REMOVE_REMOTE_AGENT = 14;

    private int type;

    private String domain;

    public ControlMessage(int type, String from, String[] to, Object content) {
	this.type = type;
	this.from = from;
	this.to = to;
	this.content = content;
    }

    public ControlMessage(int type, String from, String to, Object content) {
	this.type = type;
	this.from = from;
	this.to = new String[] { to };
	this.content = content;
    }

    public ControlMessage(int type, String from, String to, Object content,
	    String timestamp, String machine) {
	this.type = type;
	this.from = from;
	this.to = new String[] { to };
	this.content = content;
	this.machine = machine;
	this.timestamp = Long.parseLong(timestamp);
    }

    public int getType() {
	return type;
    }

    public String getDomain() {
	return domain;
    }

    public void setDomain(String domain) {
	this.domain = domain;
    }
}
