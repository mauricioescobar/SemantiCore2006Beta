package semanticore.agent.kernel.information;

public class SimpleFact extends Fact {
    private String subject = null;

    private String predicate = null;

    private String object = null;

    public SimpleFact(String subject, String predicate, String object) {
	super();
	this.subject = subject;
	this.predicate = predicate;
	this.object = object;
    }

    public String getObject() {
	return object;
    }

    public String getPredicate() {
	return predicate;
    }

    public String getSubject() {
	return subject;
    }

}
