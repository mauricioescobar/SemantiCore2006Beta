package semanticore.domain.model;

public class ComponentMessage extends Message {

    public ComponentMessage(String from, String to, Object content) {
	this.from = from;
	this.to = new String[] { to };
	this.content = content;
	this.timestamp = getTime();
    }
}
