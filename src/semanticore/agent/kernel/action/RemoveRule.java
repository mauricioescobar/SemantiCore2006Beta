package semanticore.agent.kernel.action;

import semanticore.agent.decision.DecisionComponent;
import semanticore.agent.kernel.action.model.DecisionEvent;
import semanticore.domain.model.SCOnt;

public class RemoveRule extends DecisionEvent<String, DecisionComponent> {
    public RemoveRule(String ruleName) {
	super(SCOnt.REMOVE_RULE, ruleName);
    }

    @Override
    public Object evaluate(DecisionComponent param) {
	// TODO Auto-generated method stub
	return null;
    }
}
