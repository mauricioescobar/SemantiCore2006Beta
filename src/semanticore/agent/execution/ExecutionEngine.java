package semanticore.agent.execution;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import semanticore.agent.execution.model.Action;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.agent.kernel.information.Fact;
import semanticore.domain.model.Engine;

public abstract class ExecutionEngine extends Engine<ExecutionComponent> {
    protected Vector<Fact> events = new Vector<Fact>();

    public abstract void install(ActionPlan plan);

    public abstract void signal(Object fact);

    public abstract void uninstallPlan(ActionPlan plan);

    public abstract Vector getReturnedActions();

    public abstract void startActionPlan(ActionPlan ap, Hashtable params);

    public abstract void startAction(String name, Object parameter);

    protected void transmit(Object information) {
	try {
	    component.transmit(information);
	} catch (Exception e) {

	}
    }

    protected void evaluateEvent(ActionPlan ap, Fact event) {
	Iterator<Action> iter = ap.ready.iterator();

	while (iter.hasNext()) {
	    Action action = iter.next();

	    try {
		if ((action.getPreCondition() == null)
			|| (action.getPreCondition().evaluate(event))) {
		    iter.remove();

		    ap.executing.add(action);

		    action.start();
		}
	    } catch (Exception e) {
		System.err.println("[ E ] SCWorkflowEngine (event) : "
			+ e.getMessage());
	    }
	}

	switchReadyActionToExecuting(ap);
    }

    protected void switchReadyActionToExecuting(ActionPlan ap) {
	Iterator<Action> iter = ap.completed.iterator();
	while (iter.hasNext()) {
	    ap.ready.remove(iter.next());
	}
    }
}
