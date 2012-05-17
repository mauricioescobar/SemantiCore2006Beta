package semanticore.agent.sensorial.hotspots;

import semanticore.agent.sensorial.Sensor;
import semanticore.domain.model.SemanticMessage;

public class StringSensor extends Sensor {

    public StringSensor(String sName) {
	super(sName);
    }

    public Object evaluate(Object facts) {
	if (facts instanceof SemanticMessage) {
	    SemanticMessage m = (SemanticMessage) facts;
	    return m;
	}
	return null;
    }
}
