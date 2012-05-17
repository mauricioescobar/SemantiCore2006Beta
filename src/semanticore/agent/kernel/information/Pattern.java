package semanticore.agent.kernel.information;

public class Pattern {
    private Fact fact = null;

    private String description;

    public Pattern() {
	description = "";
    }

    public Pattern(Fact fact) {
	this.fact = fact;
	this.description = "";
    }

    public Pattern(Fact fact, String description) {
	this.fact = fact;
	this.description = description;
    }

    public String getDescription() {
	return description;
    }

    public Fact getFact() {
	return fact;
    }

}
