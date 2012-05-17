package semanticore.domain.communicationManager.actions;

import semanticore.agent.execution.model.Action;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.Domain;
import semanticore.domain.model.SemanticMessage;

final public class SendMessage extends Action {
    private Environment environment;

    public SendMessage(Environment env, String responsable, String descriptor,
	    Object parameter, Object arguments) {
	super("SendMessage", descriptor);

	this.parameter = parameter;
	this.arguments = arguments;

	this.environment = env;
    }

    public void exec() {
	SemantiCore.notification.print("[ I ] CM : ");
	SemantiCore.notification.print("          SendMessage Action");

	try {
	    ControlMessage cMessage = (ControlMessage) this.getArguments();
	    SemanticMessage message = (SemanticMessage) cMessage.getContent();

	    String destination = message.getDomainTo();

	    message.setDomainTo(null);

	    Domain d = environment.getControlBridge()
		    .getInterDomainRoutingTable().get(destination);

	    if (d != null) {
		ControlMessage uMessage = new ControlMessage(-1,
			environment.getDomainName(), destination, message);
		environment.getControlBridge().sendMessage(uMessage,
			d.getAddress(), d.getPort());
	    } else {
		SemantiCore.notification
			.print("[ I ] Communication Manager : ");
		SemantiCore.notification.print("\tMessage not sent,");
		SemantiCore.notification.print("\tDomain : " + destination
			+ " unreachable!");
	    }
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] CommunicationManager : Erro (SendMessage) > "
			    + e.getMessage());
	}
    }
}
