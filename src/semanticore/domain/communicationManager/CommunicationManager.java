package semanticore.domain.communicationManager;

import java.util.Vector;

import semanticore.agent.decision.DecisionComponent;
import semanticore.agent.decision.DecisionEngine;
import semanticore.agent.sensorial.Sensor;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.communicationManager.actions.ReceiveMessage;
import semanticore.domain.communicationManager.actions.SendMessage;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.model.SCOnt;
import semanticore.domain.model.SemanticAgent;
import semanticore.domain.model.SemanticMessage;

final public class CommunicationManager extends SemanticAgent {

    public CommunicationManager(Environment env, String agentName, String arg) {
	super(env, agentName, arg);
    }

    @Override
    protected void setup() {
	CommunicationManagerSensor sensor = new CommunicationManagerSensor(
		"CMSensor");
	addSensor(sensor);
	CommunicationManagerDecide engine = new CommunicationManagerDecide();
	setDecisionEngine(engine);
    }

    private class CommunicationManagerDecide extends DecisionEngine {
	public Vector decide(Object facts) {
	    Vector actions = null;

	    try {
		Object action = null;

		action = compare(facts);

		if (action != null) {
		    if (actions == null)
			actions = new Vector();

		    actions.add(action);
		}
	    } catch (Exception e) {
		System.err
			.println("[ E ] > CommunicationManager Decision | (think) : "
				+ e.getMessage());
	    }

	    return actions;
	}

	protected Object compare(Object s) {
	    if (s instanceof ControlMessage) {
		ControlMessage cMessage = (ControlMessage) s;

		if (cMessage.getContent() instanceof SemanticMessage) {
		    if (cMessage.getTo()[0].equals(((Environment) environment)
			    .getDomainName()))
			return new ReceiveMessage(((Environment) environment),
				"execution", SCOnt.EXE_ACTION,
				"ControlMessage", s);
		    else
			return new SendMessage(((Environment) environment),
				"execution", SCOnt.EXE_ACTION,
				"ControlMessage", s);
		}
	    } else if (s instanceof SemanticMessage) {
		SemantiCore.notification
			.print("SemanticMessage : Nothing to do");
	    }

	    return null;
	}
    }

    private class CommunicationManagerSensor extends Sensor {
	public CommunicationManagerSensor(String sName) {
	    super(sName);
	}

	@Override
	public Object evaluate(Object facts) {
	    return facts;
	}
    }
}
