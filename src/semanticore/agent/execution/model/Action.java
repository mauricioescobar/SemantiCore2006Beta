package semanticore.agent.execution.model;

import java.util.GregorianCalendar;
import java.util.Vector;

import semanticore.agent.kernel.information.Fact;
import semanticore.domain.model.SemanticAgent;
import semanticore.general.util.Metric;
import semanticore.general.util.SCThread;

public abstract class Action extends SCThread implements java.io.Serializable,
	Cloneable {

    public ActionPlan plan = null;
    private Vector previousActions = new Vector();
    protected Fact postCondition = null;
    protected Fact preCondition = null;
    protected String actionDescriptor;
    protected Object arguments;
    protected Object parameter;
    private boolean completed = false;
    private GregorianCalendar cal = new GregorianCalendar();
    private long timestamp = cal.getTimeInMillis();
    public Metric metric;

    public Action(String actionName, Fact preCondition, Fact postCondition) {
	this.setName(actionName);
	this.postCondition = postCondition;
	this.preCondition = preCondition;
    }

    public Action(String actionName, String descriptor) {
	this.setName(actionName);
	this.actionDescriptor = descriptor;
    }

    public void addPreviousAction(Action ac) {
	previousActions.add(ac);
    }

    public Vector getPreviousActions() {
	return previousActions;
    }

    public Fact getPostCondition() {
	return postCondition;
    }

    public Fact getPreCondition() {
	return preCondition;
    }

    public String getActionDescriptor() {
	return actionDescriptor;
    }

    public Object getArguments() {
	return arguments;
    }

    public Object getParameter() {
	return parameter;
    }

    public Object clone() throws CloneNotSupportedException {
	return (Action) super.clone();
    }

    protected void transmit(Object information) {
	try {
	    if (plan != null)
		plan.getContext().getSharedContext().transmit(information);
	    else
		System.err
			.println("[E] Action: the message could not be sent! Action plan is null.");
	} catch (Exception e) {
	    System.err
		    .println("[ E ] Action: the message could not be sent! \n"
			    + e.getMessage());
	}
    }

    public void setActionDescriptor(String descriptor) {
	this.actionDescriptor = descriptor;
    }

    public synchronized boolean isCompleted() {
	return completed;
    }

    public synchronized void setCompleted(boolean completed) {
	this.completed = completed;
    }

    @Override
    public void terminate(Object param) {
    }

    public void exec(Object input, Object result) {
    }

    public synchronized void setParameter(Object parameter) {
	this.parameter = parameter;
    }

    protected SemanticAgent getOwner() {
	try {
	    return plan.getContext().getSharedContext().getOwner();
	} catch (Exception e) {
	    return null;
	}
    }

    public long getTimestamp() {
	return timestamp;
    }
}
