package semanticore.agent.kernel.action.model;

import semanticore.agent.decision.DecisionComponent;
import semanticore.domain.model.Event;

public abstract class DecisionEvent<T, DecisionComponent> extends
	Event<T, DecisionComponent> {
    public DecisionEvent(String descriptor, T parameter) {
	super(descriptor, parameter);
    }
}
