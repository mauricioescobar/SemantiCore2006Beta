package semanticore.agent.effector;

import java.util.Iterator;
import java.util.LinkedList;

import semanticore.domain.model.Component;
import semanticore.domain.model.SemanticAgent;

public class EffectorComponent extends Component {
    private LinkedList<Effector> effectors;

    public EffectorComponent(SemanticAgent owner) {
	super(owner, "effector");

	effectors = new LinkedList<Effector>();
    }

    public void put(Object information) {
	this.msgBuffer.put(information);
    }

    public boolean addEffector(Effector eff) {
	try {
	    return this.effectors.add(eff);
	} catch (Exception e) {
	    return false;
	}
    }

    public boolean removeEffector(String name) {
	try {
	    Iterator<Effector> iter = effectors.iterator();
	    while (iter.hasNext()) {
		try {
		    if (iter.next().name.equals(name)) {
			iter.remove();
			return true;
		    }
		} catch (Exception e) {
		    // TODO: handle exception
		}
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	}

	return false;
    }

    public void run() {
	Object o;

	while (running) {
	    try {
		o = msgBuffer.getFirst();
		Object assertion = o;
		Iterator<Effector> iter = effectors.iterator();
		
		while (iter.hasNext()) {
		    try {
			if (assertion instanceof Object[])
			    iter.next().publish((Object[]) assertion);
			else
			    iter.next().publish(assertion);
		    } catch (Exception e) {
			System.err.println("[ E ] EffectorComponent (run) : "
				+ e.getMessage());
		    }
		}
	    } catch (Exception e) {
		System.err.println("[ E ] EffectorComponent : Erro (run) - "
			+ e.getMessage());
		e.printStackTrace();
	    }
	}
    }

    public void addTransmissionComponent(String component) {
	this.transmissionComponent.add(component);
    }

    public String[] listEffectors() {
	String[] ef = null;

	try {
	    ef = new String[effectors.size()];
	    Iterator<Effector> iter = effectors.iterator();
	    int i = 0;
	    while (iter.hasNext()) {
		ef[i] = iter.next().name;
		i++;
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	}

	return ef;
    }
}
