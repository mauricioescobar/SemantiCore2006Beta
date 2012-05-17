package semanticore.agent.execution.model;

import semanticore.domain.model.*;
import semanticore.agent.execution.*;

import java.io.Serializable;
import java.util.*;

public class Context implements Serializable {

    private SemanticAgent agent;

    private ExecutionComponent execution;

    private Hashtable<String, ContextObject> planContext = new Hashtable<String, ContextObject>();

    public Context() {
	super();
    }

    public void setLocalContext(SemanticAgent ag) {
	agent = ag;
    }

    public SemanticAgent getLocalContext() {
	return agent;
    }

    public void setSharedContext(ExecutionComponent ec) {
	execution = ec;
    }

    public ExecutionComponent getSharedContext() {
	return execution;
    }

    public Hashtable getRestrictContext() {
	return planContext;
    }

    public Object getRestrict(String name) {
	return planContext.get(name);
    }

    public boolean add(String key, ContextObject o) {
	if (planContext.put(key, o) != null)
	    return true;
	else
	    return false;
    }

    public Object get(String key) {
	ContextObject co = planContext.get(key);

	if (co != null)
	    return co.getValue();
	else
	    return null;
    }

    public boolean setValue(String key, Object value) {
	try {
	    ContextObject co = planContext.get(key);
	    co.setValue(value);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    public boolean remove(String key) {
	if (planContext.remove(key) != null)
	    return true;
	else
	    return false;
    }
}
