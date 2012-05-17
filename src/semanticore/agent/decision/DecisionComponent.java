package semanticore.agent.decision;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import semanticore.agent.kernel.action.model.DecisionEvent;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.Rule;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.Component;
import semanticore.domain.model.ComponentMessage;
import semanticore.domain.model.SemanticAgent;

public class DecisionComponent extends Component {

    private DecisionEngine decisionEngine;
    protected Hashtable facts = new Hashtable();
    protected Hashtable<String, Rule> rules = new Hashtable<String, Rule>();
    protected LinkedList<Fact> domainFacts = new LinkedList<Fact>();

    protected LinkedList<Fact> agentFacts = new LinkedList<Fact>();

    public DecisionComponent(SemanticAgent owner) {
	super(owner, "decision");
	factoriesSetup();
	interpreter = new DecisionInterpreter(this);
    }

    public void put(Object information) {
	this.msgBuffer.put(information);
    }

    private void factoriesSetup() {
	DecisionFactory dFact = new DecisionFactory();
	decisionEngine = dFact.getDecisionEngine(this);
    }

    public DecisionEngine getEngine() {
	return this.decisionEngine;
    }

    public void run() {
	Object o;
	while (running) {
	    try {
		o = msgBuffer.getFirst();

		Vector resutl = null;

		if (o instanceof ComponentMessage) {
		    ComponentMessage cm = (ComponentMessage) o;
		    Fact f = null;
		    if (cm.getContent() instanceof Fact)
			f = (Fact) cm.getContent();

		    if (f != null)
			this.agentFacts.add(f);

		    o = cm.getContent();
		} else if (o instanceof Fact)
		    this.domainFacts.add((Fact) o);

		if (o instanceof DecisionEvent)
		    interpreter.evaluate((DecisionEvent) o);
		else
		    resutl = decisionEngine.decide(o);

		if (resutl != null)
		    transmit(resutl);
	    } catch (Exception e) {
		System.err.println("[ E ] DecisionComponent : error (run) - "
			+ e.getMessage());
	    }
	}
    }

    public void addRule(Rule rule) {
	decisionEngine.addRule(rule);
    }

    public Rule removeRule(String name) {
	return decisionEngine.removeRule(name);
    }

    public boolean addFact(Fact fact) {
	try {
	    this.facts.put(Integer.toString(fact.getId()), fact);
	    return true;
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] DecisionComponent : (getFact) : "
			    + e.getMessage());
	    return false;
	}
    }

    public Fact getFact(String factId) {
	try {
	    return (Fact) this.facts.get(factId);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] DecisionComponent : (getFact) : "
			    + e.getMessage());
	    return null;
	}
    }

    public boolean removeFact(String accessKey) {
	try {
	    this.facts.remove(accessKey);
	    return true;
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] DecisionComponent : (removeFact) : "
			    + e.getMessage());
	    return false;
	}
    }

    public void addTransmissionComponent(String component) {
	this.transmissionComponent.add(component);
    }

    public void setDecisionEngine(DecisionEngine decisionEngine) {
	this.decisionEngine = decisionEngine;
	this.decisionEngine.setComponent(this);
    }

    protected synchronized LinkedList<Fact> getAgentFacts() {
	return agentFacts;
    }

    protected synchronized LinkedList<Fact> getDomainFacts() {
	return domainFacts;
    }

    protected synchronized Hashtable getFacts() {
	return facts;
    }
}
