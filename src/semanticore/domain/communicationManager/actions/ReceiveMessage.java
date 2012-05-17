package semanticore.domain.communicationManager.actions;

import semanticore.agent.execution.model.Action;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.model.SemanticMessage;

final public class ReceiveMessage extends Action {
    private Environment environment;

    public ReceiveMessage(Environment env, String responsable,
	    String descriptor, Object parameter, Object arguments) {
	super("ReceiveMessage", descriptor);

	this.parameter = parameter;
	this.arguments = arguments;

	this.environment = env;
    }

    @Override
    public void exec() {
	try {
	    ControlMessage cMessage = (ControlMessage) getArguments();

	    SemanticMessage sMessage = (SemanticMessage) cMessage.getContent();

	    sMessage.setDomainTo(null);

	    environment.sendSemanticMessage(sMessage);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] CommunicationManager : Error (ReceiveMessage) > "
			    + e.getMessage());
	}
    }
}
