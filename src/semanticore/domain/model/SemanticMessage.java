package semanticore.domain.model;

public class SemanticMessage extends Message {

    public static final String sendToAll = "*";

    public SemanticMessage(String from, String to, Object content) {
	init("", from, new String[] { to }, null, content, getTime(), null);
    }

    public SemanticMessage(String from, String[] to, Object content) {
	init("", from, to, null, content, getTime(), null);
    }

    public SemanticMessage(String subject, String from, String to,
	    Object content) {
	init(subject, from, new String[] { to }, null, content, getTime(), null);
    }

    public SemanticMessage(String subject, String from, String to,
	    String domain, Object content) {
	init(subject, from, new String[] { to }, domain, content, getTime(),
		null);
    }

    public SemanticMessage(String subject, String from, String[] to,
	    Object content) {
	init(subject, from, to, null, content, getTime(), null);
    }

    public SemanticMessage(String subject, String from, String[] to,
	    String domain, Object content) {
	init(subject, from, to, domain, content, getTime(), null);
    }

    public SemanticMessage(String subject, String from, String[] to,
	    Object content, String machine) {
	init(subject, from, to, null, content, getTime(), machine);
    }

    private void init(Object subject, Object from, Object to, Object domain,
	    Object content, Object timestamp, Object machine) {
	this.domainTo = (String) domain;
	this.subject = (String) subject;
	this.from = (String) from;
	this.to = (String[]) to;
	this.content = content;
	this.machine = (String) machine;
	try {
	    this.timestamp = Long.parseLong((String) timestamp);
	} catch (Exception e) {
	    this.timestamp = getTime();
	}
    }

    @Override
    public String toString() {
	return "SemanticMessage\nFrom:" + from + "\nTo:" + to[0] + "\nContent:"
		+ content;
    }

    public long getTimestamp() {
	return timestamp;
    }
}
