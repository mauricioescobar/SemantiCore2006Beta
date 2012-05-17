package semanticore.domain.model;

import java.io.Serializable;
import java.util.GregorianCalendar;

public abstract class Message implements Serializable {
    protected long timestamp = 0;

    protected String machine = null;

    protected String from = null;

    protected String[] to = null;

    protected String domainTo = null;

    protected String domainFrom = null;

    protected Object content = null;

    protected String subject = null;

    public void setTime() {
	GregorianCalendar cal = new GregorianCalendar();
	timestamp = cal.getTimeInMillis();
    }

    public void setMachine(String m) {
	machine = m;
    }

    public String getMachine() {
	return machine;
    }

    public long getTime() {
	return timestamp;
    }

    public Object getContent() {
	return content;
    }

    public String[] getTo() {
	return to;
    }

    public String getFrom() {
	return from;
    }

    public String getSubject() {
	return subject;
    }

    public void setContent(Object content) {
	this.content = content;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    public String getDomainTo() {
	return domainTo;
    }

    public void setDomainTo(String domain) {
	this.domainTo = domain;
    }

    public void setFrom(String from) {
	this.from = from;
    }

    public synchronized String getDomainFrom() {
	return domainFrom;
    }

    public synchronized void setDomainFrom(String domainFrom) {
	this.domainFrom = domainFrom;
    }

}
