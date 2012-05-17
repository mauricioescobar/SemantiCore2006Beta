package semanticore.agent.sensorial.hotspots;

import semanticore.agent.sensorial.Sensor;
import semanticore.domain.model.SemanticMessage;

public class UnicastSensor extends Sensor {
    public UnicastSensor() {
	super("UnicastSensor");
    }

    @Override
    public Object evaluate(Object facts) {
	if (facts instanceof SemanticMessage) {
	    SemanticMessage message = (SemanticMessage) facts;

	    if (message.getTo().length > 0)
		for (int i = 0; i < message.getTo().length; i++)
		    if (message.getTo()[i].equals(getOwner().getName()))
			return message;
	}

	return null;
    }
}
