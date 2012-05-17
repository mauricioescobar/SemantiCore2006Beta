package semanticore.agent.execution;

import java.util.Hashtable;
import java.util.Vector;

import semanticore.agent.execution.model.Action;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.agent.kernel.action.model.ExecutionEvent;
import semanticore.domain.model.ComponentInterpreter;
import semanticore.domain.model.SCOnt;

public class ExecutionInterpreter extends
	ComponentInterpreter<Action, ExecutionComponent> {
    public ExecutionInterpreter(ExecutionComponent exe) {
	super(exe);
    }

    public Object evaluate(Action a) {
	if (a.getActionDescriptor().equals(SCOnt.EXE_PLAN)) {
	    try {
		if (a.getArguments() instanceof Hashtable)
		    executePlan((String) a.getParameter(),
			    (Hashtable) a.getArguments());
	    } catch (Exception e) {
		System.err.println("[ E ] Execution interpreter : "
			+ e.getMessage());
		e.printStackTrace();
	    }
	} else if (a.getActionDescriptor().equals(SCOnt.EXE_ACTION)) {
	    a.start();
	}

	return null;
    }

    public Object evaluate(ExecutionEvent event) {
	try {
	    return event.evaluate(component);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public void executePlan(String name, Hashtable params) {
	Vector plans = component.getActionPlans();
	ActionPlan ap = null;

	for (int i = 0; i < plans.size(); i++) {
	    ap = (ActionPlan) plans.get(i);
	    if (ap.getName().equals(name)) {
		try {
		    component.getExecutionEngine().startActionPlan(ap, params);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		break;
	    }
	}
    }
}
