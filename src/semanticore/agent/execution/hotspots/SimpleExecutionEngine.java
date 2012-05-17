package semanticore.agent.execution.hotspots;

import java.lang.Thread.State;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import semanticore.agent.execution.ExecutionEngine;
import semanticore.agent.execution.model.Action;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.domain.SemantiCore;
import semanticore.domain.actions.lib.ExecuteAction;

public class SimpleExecutionEngine extends ExecutionEngine {
    protected Hashtable<String, ActionPlan> plans = new Hashtable<String, ActionPlan>();
    protected Hashtable<String, Action> actions = new Hashtable<String, Action>();

    @Override
    public Vector getReturnedActions() {
	return null;
    }

    @Override
    public void install(ActionPlan plan) {
	plans.put(plan.getName(), plan);
	installActions(plan);
    }

    private void installActions(ActionPlan plan) {
	Iterator<Action> iter = plan.actions.iterator();
	while (iter.hasNext()) {
	    Action a = iter.next();
	    actions.put(a.getName(), a);

	    if (a.getPreCondition() == null)
		a.start();
	}
    }

    @Override
    public void signal(Object fact) {
	// if (fact instanceof ExecuteAction) {
	// }
    }

    @Override
    public void startActionPlan(ActionPlan ap, Hashtable params) {
	ActionPlan p = plans.get(ap.getName());
	if (p != null) {
	    p.installContext(params);
	    p.start();
	}
    }

    @Override
    public void uninstallPlan(ActionPlan plan) {
	synchronized (plans) {
	    plan.restartPlan();
	    plans.remove(plan.getName());
	    unistallActions(plan);
	}
    }

    private void unistallActions(ActionPlan plan) {
	Enumeration<Action> a = plan.actions.elements();
	while (a.hasMoreElements()) {
	    try {
		actions.remove(a.nextElement().getName());
	    } catch (Exception e) {
		System.err
			.println("[ E ] ExecutionEngine : Erro removendo acao");
	    }
	}
    }

    @Override
    public void startAction(String name, Object params) {
	Action a = actions.get(name);
	if (a != null) {
	    try {
		a.setParameter(params);
		a.start();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void exec() {
	while (getState() == State.RUNNABLE) {
	    ActionPlan ap;

	    Enumeration<ActionPlan> p = plans.elements();
	    while (p.hasMoreElements()) {
		ap = p.nextElement();

		if (ap.getState() == State.TERMINATED) {
		    try {
			component.addTerminatedPlan(ap);

			SemantiCore.notification.print("\n[ I ] <"
				+ ap.getName()
				+ "> plan added to finalized processes ");

			uninstallPlan(ap);
			getOwner().endGoal(ap.getName());
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }

	    try {
		Thread.sleep(300);
	    } catch (InterruptedException ex) {
	    }
	}
    }

    @Override
    public void terminate(Object param) {
	// TODO Auto-generated method stub
    }
}
