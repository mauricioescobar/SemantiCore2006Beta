package semanticore.domain.control;

import java.util.Enumeration;

import semanticore.domain.model.SemanticAgent;
import semanticore.domain.model.SemanticMessage;

final public class DataBridgeServiceThread extends Thread {
    private SemanticMessage message = null;

    private DataBridge bridge = null;

    public DataBridgeServiceThread(SemanticMessage sm, DataBridge bridge) {
	this.message = sm;
	this.bridge = bridge;
    }

    public void run() {
	if (message != null)
	    evaluate();
    }

    public void evaluate() {
	broadcastToAgents(message);
    }

    private void broadcastToAgents(SemanticMessage m) {
	Enumeration<SemanticAgent> elements = bridge.environment
		.getLocalAgentsElements();

	while (elements.hasMoreElements()) {
	    SemanticAgent agent = null;

	    try {
		agent = elements.nextElement();
		agent.put(m);
	    } catch (Exception e) {
		System.err
			.println("[ E ] DataBridge : Mensagem nao pode ser entregue para agente < "
				+ agent.getName() + " >");
	    }
	}
    }
}
