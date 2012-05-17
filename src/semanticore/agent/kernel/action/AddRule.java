package semanticore.agent.kernel.action;

import semanticore.agent.decision.DecisionComponent;
import semanticore.agent.kernel.action.model.DecisionEvent;
import semanticore.agent.kernel.information.Rule;
import semanticore.domain.model.SCOnt;

public class AddRule extends DecisionEvent<Rule, DecisionComponent> {
    public AddRule(Rule rule) {
	super(SCOnt.ADD_RULE, rule);
    }

    @Override
    public Object evaluate(DecisionComponent param) {
	// TODO Auto-generated method stub
	return null;
    }
}
