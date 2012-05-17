package semanticore.domain.domainController.actions;

import java.util.Enumeration;

import javax.management.Notification;

import semanticore.agent.execution.model.Action;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.DomainPart;
import semanticore.domain.services.notification.TerminalNotification;

final public class UpdateNewDomainPart extends Action {
    private Environment environment;

    public UpdateNewDomainPart(Environment env, String responsable,
	    String descriptor, Object parameter, Object arguments) {
	super("UpdateNewDomainPart", descriptor);

	this.parameter = parameter;
	this.arguments = arguments;

	this.environment = env;
    }

    public void exec() {
	SemantiCore.notification
		.print("[ I ] DomainController : UpdateNewDomainPart Action");

	DomainPart newDomainPart = (DomainPart) getArguments();

	Enumeration<DomainPart> e = environment.getControlBridge()
		.getDomainRoutingTableElements();

	while (e.hasMoreElements()) {
	    DomainPart part = e.nextElement();

	    if (!part.getDomainPartName().equals(
		    newDomainPart.getDomainPartName())) {

		ControlMessage m = null;

		try {
		    m = new ControlMessage(ControlMessage.NEW_DOMAIN_PART, "",
			    "", newDomainPart);

		    environment.getControlBridge().sendMessage(m,
			    part.getAddress(), part.getPort());

		    m = new ControlMessage(ControlMessage.NEW_DOMAIN_PART, "",
			    "", part);

		    environment.getControlBridge()
			    .sendMessage(m, newDomainPart.getAddress(),
				    newDomainPart.getPort());
		} catch (Exception ex) {
		    // TODO Handle exception
		}
	    }
	}
    }
}
