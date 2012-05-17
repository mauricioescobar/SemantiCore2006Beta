package semanticore.agent.decision;

import semanticore.agent.kernel.action.model.DecisionEvent;
import semanticore.domain.model.Component;
import semanticore.domain.model.ComponentInterpreter;

public class DecisionInterpreter extends
	ComponentInterpreter<DecisionEvent, DecisionComponent> {
    public DecisionInterpreter(Component c) {
	super(c);
    }

    @Override
    public Object evaluate(DecisionEvent a) {
	try {
	    return a.evaluate(component);
	} catch (Exception e) {
	    return null;
	}
    }
}
