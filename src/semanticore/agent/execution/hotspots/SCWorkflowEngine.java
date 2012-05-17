package semanticore.agent.execution.hotspots;

import java.lang.Thread.State;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import semanticore.agent.execution.ExecutionEngine;
import semanticore.agent.execution.model.Action;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.SimpleFact;
import semanticore.domain.SemantiCore;

public class SCWorkflowEngine extends ExecutionEngine {

    protected Vector<ActionPlan> initiatedProcess = new Vector<ActionPlan>();
    protected Vector<ActionPlan> runningProcess = new Vector<ActionPlan>();
    protected Vector<ActionPlan> completeProcess = new Vector<ActionPlan>();
    protected Vector<ActionPlan> suspendedProcess = new Vector<ActionPlan>();

    @Override
    public Vector getReturnedActions() {
	return null;
    }

    @Override
    public void install(ActionPlan plan) {
	SemantiCore.notification
		.print("[ I ] SCWorkflowEngine > install plan : "
			+ plan.getName());

	Initiate(plan);
    }

    @Override
    public void signal(Object fact) {
	SemantiCore.notification.print("[ I ] SCWorkflowEngine > signal");

	if (fact instanceof Fact)
	    events.add((Fact) fact);
    }

    @Override
    public void startActionPlan(ActionPlan ap, Hashtable params) {
	SemantiCore.notification
		.print("[ I ] SCWorkflowEngine > startActionPlan");
	Start(ap, params);
    }

    public void addRunningPlan(ActionPlan ap) {
	try {
	    if (initiatedProcess.remove(ap)) {
		this.runningProcess.add(ap);

		SemantiCore.notification
			.print("[ I ] SCWorkflowEngine > Action plan adiciondo em runningPlans");

		events.add(new SimpleFact("", "", ""));
	    } else
		SemantiCore.notification
			.print("[ I ] SCWorkflowEngine > Action plan nao foi adiciondo em runningPlans");
	} catch (Exception e) {
	    System.err
		    .println("[ E ] SCWorkflowEngine : Erro adicionando running plan");
	}
    }

    protected void Start(ActionPlan plan, Hashtable param) {
	try {
	    if (plan.getState() == State.TERMINATED) {
		SemantiCore.notification
			.print("\n[ I ] SCWorkflowEngine : Plan is already finalized!\n");

		if (completeProcess.remove(plan)) {
		    component.removeActionPlan(plan);

		    ActionPlan tmp = new ActionPlan(plan.getName());

		    Iterator<Action> iter = plan.actions.iterator();
		    while (iter.hasNext()) {
			try {
			    tmp.addAction((Action) iter.next().clone());
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }

		    component.addActionPlan(tmp);

		    plan = null;

		    tmp.installContext(param);
		    addRunningPlan(tmp);
		    tmp.start();
		}
	    } else if (plan.getState() == State.NEW) {
		plan.installContext(param);
		plan.start();

		addRunningPlan(plan);
	    }
	} catch (Exception e) {
	    System.err.println("[ E ] WorkflowEngine (Start) : "
		    + e.getMessage());
	    e.printStackTrace();
	}
    }

    protected void Initiate(ActionPlan plan) {
	if (initiatedProcess.add(plan))
	    SemantiCore.notification
		    .print("[ I ] WorkflowEngine : Plan adicionado em processos Iniciados!");
	else
	    SemantiCore.notification
		    .print("[ I ] WorkflowEngine : Plano NAO adicionado em processos Iniciados!");
    }

    @Override
    public void uninstallPlan(ActionPlan plan) {
	String planName = plan.getName();
	for (Iterator i = initiatedProcess.iterator(); i.hasNext();) {
	    if (((ActionPlan) i.next()).getName().equals(planName))
		i.remove();
	}
    }

    @Override
    public void suspend() {
	try {
	    Iterator<ActionPlan> iter = runningProcess.iterator();
	    while (iter.hasNext()) {
		suspendedProcess.add(iter.next());
		iter.remove();
	    }

	    SemantiCore.notification
		    .print("[ I ] WorkflowEngine : Suspend finished");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public boolean resume() {
	try {
	    Iterator<ActionPlan> iter = suspendedProcess.iterator();
	    while (iter.hasNext()) {
		runningProcess.add(iter.next());
		iter.remove();
	    }

	    SemantiCore.notification
		    .print("[ I ] WorkflowEngine : Resume finished");

	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return false;
    }

    @Override
    public void startAction(String name, Object parameter) {
	// TODO Auto-generated method stub

    }

    @Override
    public void exec() {
	Fact event;
	while (getState() == State.RUNNABLE) {
	    for (int i = 0; i < events.size(); i++) {
		event = events.remove(i);

		ActionPlan ap;

		for (int j = 0; j < runningProcess.size(); j++) {
		    ap = (ActionPlan) runningProcess.get(j);
		    evaluateEvent(ap, event);
		}
	    }

	    ActionPlan ap;
	    for (int j = 0; j < runningProcess.size(); j++) {
		ap = (ActionPlan) runningProcess.get(j);

		if (ap.getState() == State.TERMINATED)
		    try {
			if (runningProcess.remove(ap)) {
			    completeProcess.add(ap);

			    component.addTerminatedPlan(ap);

			    SemantiCore.notification.print("\n[ I ] <"
				    + ap.getName()
				    + "> plan added to finalized processes");
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
	    }
	    try {
		Thread.sleep(200);
	    } catch (InterruptedException ex) {
	    }
	}
    }

    @Override
    public void terminate(Object param) {
	// TODO Auto-generated method stub
    }
}
