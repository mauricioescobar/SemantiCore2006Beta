package semanticore.agent.execution;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import semanticore.agent.execution.model.Action;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.agent.execution.model.Context;
import semanticore.agent.kernel.action.model.ExecutionEvent;
import semanticore.agent.kernel.information.Fact;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.Component;
import semanticore.domain.model.SemanticAgent;

public class ExecutionComponent extends Component {

    private ExecutionInterpreter interpreter = new ExecutionInterpreter(this);
    private Hashtable executionContext = new Hashtable();
    private Vector<ActionPlan> plans = new Vector<ActionPlan>();
    private LinkedList<Fact> perceivedEvents = new LinkedList<Fact>();
    private LinkedList<Fact> generatedEvents = new LinkedList<Fact>();
    private LinkedList<ActionPlan> terminatedPlans = new LinkedList<ActionPlan>();

    public ExecutionComponent(SemanticAgent owner) {
	super(owner, "execution");
    }

    public void addActionPlan(ActionPlan ap) {
	Context context = ap.getContext();
	context.setSharedContext(this);

	plans.add(ap);

	((ExecutionEngine) engine).install(ap);
    }

    public void run() {
	Object o;

	while (running) {
	    try {
		o = msgBuffer.getFirst();

		Vector v;

		if (o instanceof Vector)
		    v = (Vector) o;
		else {
		    v = new Vector(1, 1);
		    v.add(o);
		}

		for (int i = 0; i < v.size(); i++) {
		    Object obj = v.get(i);

		    if (obj instanceof Action) {
			Action a = (Action) v.get(i);

			interpreter.evaluate(a);
		    } else if (obj instanceof ExecutionEvent) {
			Object ret;
			ret = interpreter.evaluate((ExecutionEvent) obj);
			if (ret != null) {
			    transmit(ret);
			}
		    } else
			signal(obj);
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public void put(Object information) {
	this.msgBuffer.put(information);
    }

    public Vector getActionPlans() {
	return plans;
    }

    public boolean restartActionPlan(String name) {
	SemantiCore.notification.print("\n> Restarting plan : " + name);
	try {
	    Iterator<ActionPlan> iter = terminatedPlans.iterator();
	    while (iter.hasNext()) {
		ActionPlan p = iter.next();
		if (p.getName().equals(name)) {
		    iter.remove();
		    plans.add(p);
		    addActionPlan(p);

		    return true;
		}
	    }
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] ExecutionComponent : Erro restarting plan > "
			    + e.getMessage());
	    return false;
	}

	return false;
    }

    public void setExecutionEngine(ExecutionEngine eEngine) {
	try {
	    this.engine.stop();
	} catch (Exception e) {
	    // TODO: handle exception
	}

	this.engine = eEngine;
	this.engine.setComponent(this);

	try {
	    engine.start();
	} catch (Exception e) {
	    // TODO: handle exception
	}
    }

    public ExecutionEngine getExecutionEngine() {
	return (ExecutionEngine) this.engine;
    }

    @Override
    public void addTransmissionComponent(String component) {
	this.transmissionComponent.add(component);
    }

    public synchronized void addTerminatedPlan(ActionPlan p) {
	this.terminatedPlans.add(p);
    }

    public synchronized LinkedList<ActionPlan> getTerminatedPlans() {
	return (LinkedList<ActionPlan>) terminatedPlans.clone();
    }

    @Override
    public void transmit(Object information) {
	// TODO Auto-generated method stub
	super.transmit(information);
    }

    public void removeActionPlan(ActionPlan plan) {
	try {
	    this.terminatedPlans.remove(plan);
	    this.plans.remove(plan);
	} catch (Exception e) {
	    // TODO: handle exception
	}
    }

    private void signal(Object fact) {
	try {
	    ((ExecutionEngine) engine).signal(fact);

	    addPerceivedFact((Fact) fact);
	} catch (Exception e) {
	    // TODO: handle exception
	}
    }

    protected void addPerceivedFact(Fact f) throws ClassCastException,
	    NullPointerException {
	this.perceivedEvents.add(f);
    }

    protected void addGeneratedFact(Fact f) throws ClassCastException,
	    NullPointerException {
	this.generatedEvents.add(f);
    }
}
