package semanticore.agent.decision.hotspots;

import java.util.Vector;

import semanticore.agent.decision.DecisionEngine;
import semanticore.agent.execution.model.Action;

public class GenericDecisionEngine extends DecisionEngine {
    @Override
    public Vector decide(Object facts) {
	Vector actions = null;

	try {
	    Object f = compare(facts);

	    if (f != null) {
		actions = new Vector<Object>();

		if (f instanceof Vector)
		    actions.addAll((Vector) f);
		else
		    actions.add(f);
	    }
	} catch (Exception e) {
	    System.err.println("[ E ] GenericDecisionEngine | Erro : "
		    + e.getMessage());
	}

	return actions;
    }

    protected Object compare(Object s) {
	Action action = null;
	return action;
    }
}
