package semanticore.agent.sensorial;

import semanticore.agent.kernel.action.model.SensorialEvent;
import semanticore.domain.model.ComponentInterpreter;

public class SensorialInterpreter extends
	ComponentInterpreter<SensorialEvent, SensorialComponent> {
    public SensorialInterpreter(SensorialComponent sensorial) {
	super(sensorial);
    }

    public Object evaluate(SensorialEvent a) {
	try {
	    return a.evaluate(component);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
