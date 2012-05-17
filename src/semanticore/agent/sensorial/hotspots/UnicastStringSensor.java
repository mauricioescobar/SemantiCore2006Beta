package semanticore.agent.sensorial.hotspots;

import semanticore.domain.model.SemanticMessage;

public class UnicastStringSensor extends UnicastSensor {
    public UnicastStringSensor() {
	name = "UnicastStringSensor";
    }

    public Object evaluate(Object facts) {
	SemanticMessage message = (SemanticMessage) super.evaluate(facts);
	if (message != null)
	    if (message.getContent() instanceof String)
		return message;

	return null;
    }

}
