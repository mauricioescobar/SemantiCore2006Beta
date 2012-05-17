package semanticore.agent.execution.model;

import java.io.Serializable;
import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import semanticore.agent.kernel.action.model.ExecutionEvent;
import semanticore.agent.kernel.information.Fact;
import semanticore.general.util.SCThread;

public class ActionPlan extends SCThread implements Serializable {

    public Vector<Action> actions = new Vector<Action>();
    public Vector<Action> executing = new Vector<Action>();
    public Vector<Action> ready = new Vector<Action>();
    public Vector<Action> completed = new Vector<Action>();
    private Context context;
    private Action first = null;

    public ActionPlan(String n) {
	this.setName(n);
	context = new Context();
    }

    public void setFirst(Action ac) {
	first = ac;
    }

    private void initialize(Action a) {
	this.ready.add(a);

	if (first == null)
	    setFirst(a);
    }

    public void restartPlan() {
	this.state = Thread.State.NEW;
    }

    public void addAction(Action a) {
	try {
	    a.plan = this;
	    actions.add(a);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	initialize(a);
    }

    public void addAction(String className, String actionName,
	    Fact preCondition, Fact postCondition) {
	try {
	    Class[] argsClass = new Class[] { String.class, Fact.class,
		    Fact.class };
	    Object[] args = new Object[] { actionName, preCondition,
		    postCondition };

	    String actionsLibPath = "semanticore.domain.actions.lib";

	    Constructor stringArgsConstructor;
	    Class c = Class.forName(actionsLibPath + "." + className);
	    stringArgsConstructor = c.getConstructor(argsClass);
	    Object a = createObject(stringArgsConstructor, args);

	    Action action = (Action) a;

	    initialize(action);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static Object createObject(Constructor constructor,
	    Object[] arguments) {
	Object object = null;
	try {
	    object = constructor.newInstance(arguments);
	    return object;
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	}

	return object;
    }

    public Action getAction(String actionName) {
	try {
	    Iterator<Action> iter = actions.iterator();
	    while (iter.hasNext()) {
		Action a = (Action) iter.next().clone();
		if (a.getName().equals(actionName))
		    return a;
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	}

	return null;
    }

    @Override
    public void exec() {

	while ((ready.size() > 0 || executing.size() > 0)
		&& (state != State.TERMINATED && state != State.BLOCKED)) {
	    try {
		boolean finished = true;
		Enumeration<Action> actions = this.actions.elements();
		while (actions.hasMoreElements()) {
		    Action a = actions.nextElement();

		    if (a.getState() == State.TERMINATED) {
			try {
			    if (executing.remove(a)) {
				completed.add(a);

				Fact postCondition = a.getPostCondition();
				if (a.getPostCondition() != null)
				    context.getSharedContext()
					    .getExecutionEngine()
					    .signal(postCondition);
			    }
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    } else
			finished = false;
		}

		if (finished)
		    break;

		Thread.sleep(200);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    public void setContext(Context c) {
	context = c;
    }

    public Context getContext() {
	return context;
    }

    public boolean hasVariable(String variableName) {
	if (context.get(variableName) != null)
	    return true;
	else
	    return false;

    }

    public boolean defineVariable(String variableName) {
	try {
	    ContextObject co = new ContextObject(variableName, null);

	    return this.context.add(variableName, co);
	} catch (Exception e) {
	    return false;
	}
    }

    public boolean defineVariable(String variableName, Object initialValue) {
	try {
	    ContextObject co = new ContextObject(variableName, initialValue);

	    return this.context.add(variableName, co);
	} catch (Exception e) {
	    return false;
	}
    }

    public boolean removeVariable(String variableName) {
	try {
	    return this.context.remove(variableName);
	} catch (Exception e) {
	    return false;
	}
    }

    public Object getVariableValue(String variableName) {
	try {
	    return this.context.get(variableName);
	} catch (Exception e) {
	    return null;
	}
    }

    public void installContext(Hashtable param) {
	try {
	    Enumeration<String> keys = param.keys();
	    while (keys.hasMoreElements()) {
		String variable = keys.nextElement();
		if (!hasVariable(variable))
		    defineVariable(variable, param.get(variable));
		else
		    setVariableValue(variable, param.get(variable));
	    }
	} catch (Exception e) {
	    System.err.println("[ E ] ActionPlan (installContext)");
	}
    }

    public boolean setVariableValue(String variableName, Object value) {
	try {
	    return this.context.setValue(variableName, value);
	} catch (Exception e) {
	    return false;
	}
    }

    public Vector getActionsList() {
	return actions;
    }

    public void restartCompletedActions() {
	Iterator<Action> iter = completed.iterator();
	while (iter.hasNext()) {
	    try {
		initialize(iter.next());
		iter.remove();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void terminate(Object param) {
	try {
	    if (param instanceof ExecutionEvent) {
		context.getSharedContext().put(param);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
