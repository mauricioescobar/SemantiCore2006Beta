package semanticore.domain.model;

public class GoalInput {
    public String owlClass;

    public String owlProperty;

    public GoalInput(String owlClass, String owlProperty) {
	this.owlClass = owlClass;
	this.owlProperty = owlProperty;
    }
}
