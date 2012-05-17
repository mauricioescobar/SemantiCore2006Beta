package semanticore.domain.control;

import java.util.Observable;

import javax.swing.JOptionPane;

import semanticore.domain.SemantiCore;
import semanticore.domain.model.AgentRoutingTable;
import semanticore.domain.model.Component;
import semanticore.domain.model.SemanticAgent;

final public class ControlBridgeServiceThread extends Observable implements
	Runnable {
    private ControlMessage controlMessage = null;

    private ControlBridge controlBridge = null;

    public ControlBridgeServiceThread(ControlMessage cm, ControlBridge bridge) {
	this.controlMessage = cm;
	this.controlBridge = bridge;
    }

    public void run() {
	if (controlMessage != null)
	    evaluate(controlMessage);
    }

    public void evaluate(ControlMessage sm) {
	try {
	    evaluateControlMessage(sm);
	} catch (Exception e) {
	    System.err
		    .println("[ E ] BridgeServiceThread > Erro ( testMessage ) ");
	}
    }

    private void evaluateControlMessage(ControlMessage controlMessage) {
	try {
	    String job[] = new String[] {};

	    if (controlMessage.getContent() instanceof String)
		job = controlMessage.getContent().toString().split(" ");

	    switch (controlMessage.getType()) {
	    case ControlMessage.REMOVE_REMOTE_AGENT:
		controlBridge.removeRemoteAgent((String) controlMessage
			.getContent());
		break;

	    case ControlMessage.MOBILE_AGENT:
		SemanticAgent agent = (SemanticAgent) controlMessage
			.getContent();

		agent.setEnvironment(controlBridge.getEnvironment());

		agent.restartBasicComponents();
		agent.afterMove();

		controlBridge.getEnvironment().addLocalAgent(agent);

		AgentRoutingTable ag;

		if (controlBridge.getEnvironment().isPartOfDomain())
		    ag = new AgentRoutingTable(agent, controlBridge
			    .getEnvironment().getDomainPartName());
		else
		    ag = new AgentRoutingTable(agent);

		controlBridge.addAgentToRoutingTable(ag);

		break;

	    case ControlMessage.DOMAIN_ALIVE: {
		try {
		    controlBridge.getInterDomainRoutingTable()
			    .get(controlMessage.getFrom())
			    .setMetric(ControlBridge.MAX_METRIC);
		} catch (Exception e) {

		}
	    }
		break;

	    case ControlMessage.DOMAIN_REGISTERED: {
		try {
		    Domain d = this.controlBridge.getInterDomainRoutingTable()
			    .get(controlMessage.getFrom());
		    d.setConnected(true);
		} catch (Exception e) {
		    System.err.println("[ E ] ControlBridge (ST):");
		    System.err.println("                         "
			    + e.getMessage());
		}
	    }
		break;

	    case ControlMessage.AGENT_INTERNAL_TRANSMISSION: {
		try {
		    String from = controlMessage.getFrom();
		    String to = controlMessage.getTo()[0];

		    from = from.substring(0, from.indexOf("."));
		    to = to.substring(to.indexOf(".") + 1, to.length());

		    SemanticAgent a = controlBridge.getEnvironment()
			    .getLocalAgentByName(from);

		    Component c = a.getComponent(to);
		    if (c != null) {
			c.put(controlMessage.getContent());
		    }
		} catch (Exception e) {
		    System.err.println("[ E ] ControlBridge (ST):");
		    System.err.println("                         "
			    + e.getMessage());
		}
	    }
		break;

	    case ControlMessage.PART_OF_AGENT: {
		try {
		    SemanticAgent distributedAgent = (SemanticAgent) controlMessage
			    .getContent();
		    distributedAgent.setEnvironment(this.controlBridge
			    .getEnvironment());

		    distributedAgent.restartBasicComponents();

		    controlBridge.getEnvironment().addLocalAgent(
			    distributedAgent);

		    SemantiCore.notification
			    .print("[ I ] AgenteDistributed Agent:");
		    SemantiCore.notification.print("\tName: "
			    + distributedAgent.getName());
		} catch (Exception e) {
		    SemantiCore.notification.print("[ E ] ControlBridge (ST):");
		    SemantiCore.notification.print("\t" + e.getMessage());
		}
	    }
		break;

	    case ControlMessage.AGENT_UPDATE: {
		ag = (AgentRoutingTable) controlMessage.getContent();

		if (controlBridge.getAgentRoutingTable().get(ag.getKey()) == null)
		    controlBridge.addAgentToRoutingTable(ag);
	    }
		break;

	    case ControlMessage.REGISTER_DOMAIN_PART: {
		controlBridge.sendBroadcastToPlatformAgents(controlMessage);
	    }
		break;

	    case ControlMessage.REGISTER_DOMAIN: {
		controlBridge.sendBroadcastToPlatformAgents(controlMessage);
	    }
		break;

	    case ControlMessage.REGISTER_DOMAIN_PART_REFUSED: {
		JOptionPane.showMessageDialog(null, "Registro Recusado: "
			+ controlMessage.getContent()
			+ " \nFinalizando Plataforma! ", "",
			JOptionPane.ERROR_MESSAGE);

		System.exit(0);
	    }
		break;

	    case ControlMessage.NEW_DOMAIN_PART: {
		DomainPart part = (DomainPart) controlMessage.getContent();

		this.controlBridge.addPartToDomainRoutingTable(part);
	    }
		break;

	    case ControlMessage.NEW_DOMAIN: {

	    }
		break;

	    case ControlMessage.ALIVE: {
		String domainPart = job[0];

		controlBridge.updateMetric(domainPart);
	    }
		break;

	    default:
		controlBridge.sendBroadcastToPlatformAgents(controlMessage);
		break;

	    }

	    Thread.sleep(50);
	}

	catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void start() {
	new Thread(this).start();
    }
}
