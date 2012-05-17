package semanticore.agent.kernel.information;

import java.io.Serializable;

public class Rule implements Serializable {
    private String name;

    private String description;

    private Fact fact;

    private Fact consequence;

    public Rule(String name, Fact fact, Fact consequence) {
	this.name = name;
	this.fact = fact;
	this.consequence = consequence;
    }

    public Rule(String name, Fact fact, Fact consequence, String description) {
	this.name = name;
	this.fact = fact;
	this.consequence = consequence;
    }

    public Fact getConsequence() {
	return consequence;
    }

    public Fact getFact() {
	return fact;
    }

    public String getName() {
	return name;
    }

}
