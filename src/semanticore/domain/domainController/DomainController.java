package semanticore.domain.domainController;

import java.util.Vector;

import semanticore.agent.decision.DecisionEngine;
import semanticore.agent.sensorial.Sensor;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.domainController.actions.RegisterDomain;
import semanticore.domain.domainController.actions.RegisterDomainPart;
import semanticore.domain.model.SCOnt;
import semanticore.domain.model.SemanticAgent;

final public class DomainController extends SemanticAgent {

    public DomainController(Environment env, String agentName, String arg) {
	super(env, agentName, arg);
    }

    @Override
    protected void setup() {
	DomainControllerSensor sensor = new DomainControllerSensor(
		"DomainControllerSensor");
	addSensor(sensor);
	DomainControllerDecide engine = new DomainControllerDecide();
	setDecisionEngine(engine);
    }

    private class DomainControllerDecide extends DecisionEngine {
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
		SemantiCore.notification
			.print("[ E ] > DomcinController Decision | (think) : "
				+ e.getMessage());
	    }

	    return actions;
	}

	protected Object compare(Object s) {
	    if (s instanceof ControlMessage) {
		SemantiCore.notification
			.print("[ I ] DomainController : ControlMessage");

		switch (((ControlMessage) s).getType()) {
		case ControlMessage.REGISTER_DOMAIN_PART:
		    return new RegisterDomainPart(((Environment) environment),
			    SCOnt.EXE_ACTION, "ControlMessage", s);

		case ControlMessage.REGISTER_DOMAIN:
		    return new RegisterDomain(((Environment) environment),
			    SCOnt.EXE_ACTION, "ControlMessage", s);
		}
	    }

	    return null;
	}
    }

    private class DomainControllerSensor extends Sensor {
	public DomainControllerSensor(String sName) {
	    super(sName);
	}

	@Override
	public Object evaluate(Object facts) {
	    return facts;
	}
    }
}
