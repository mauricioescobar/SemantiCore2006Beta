package semanticore.agent.sensorial;

import java.io.Serializable;

import semanticore.agent.kernel.information.Fact;
import semanticore.domain.model.SemanticAgent;
import semanticore.general.util.Metric;

public abstract class Sensor implements Serializable {
    public Metric metric;
    protected String name;
    protected Fact headerPattern;
    protected Fact contentPattern;

    protected SensorialComponent sensorialComponent = null;

    public Sensor(String sName) {
	name = sName;
    }

    public String getName() {
	return name;
    }

    protected SemanticAgent getOwner() {
	return sensorialComponent.getOwner();
    }

    public abstract Object evaluate(Object facts);

    protected SensorialComponent getSensorialComponent() {
	return sensorialComponent;
    }

    public Fact getHeaderPattern() {
	return headerPattern;
    }

    public Fact getContentPattern() {
	return contentPattern;
    }
}
