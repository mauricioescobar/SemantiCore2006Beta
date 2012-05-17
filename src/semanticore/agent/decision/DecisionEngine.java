package semanticore.agent.decision;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.Rule;
import semanticore.domain.model.Engine;

public abstract class DecisionEngine extends Engine<DecisionComponent> {
    public abstract Vector decide(Object facts);

    protected void addRule(Rule rule) {
	component.rules.put(rule.getName(), rule);
    }

    protected Rule removeRule(String name) {
	return component.rules.remove(name);
    }

    protected synchronized LinkedList<Fact> getAgentFacts() {
	return component.agentFacts;
    }

    protected synchronized LinkedList<Fact> getDomainFacts() {
	return component.domainFacts;
    }

    protected synchronized Hashtable getFacts() {
	return component.facts;
    }

    protected Hashtable<String, Rule> getRules() {
	return component.rules;
    }

    @Override
    public void terminate(Object param) {
	// TODO Auto-generated method stub
    }

    @Override
    public void exec() {
	// TODO Auto-generated method stub
    }
}
