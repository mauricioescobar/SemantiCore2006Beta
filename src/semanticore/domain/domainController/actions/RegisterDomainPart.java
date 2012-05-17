package semanticore.domain.domainController.actions;

import semanticore.agent.execution.model.Action;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.DomainPart;
import semanticore.domain.model.SCOnt;
import semanticore.domain.services.notification.TerminalNotification;

final public class RegisterDomainPart extends Action {
    Environment environment;

    public RegisterDomainPart(Environment env, String descriptor,
	    Object parameter, Object arguments) {
	super("RegisterPlatform", descriptor);

	this.parameter = parameter;
	this.arguments = arguments;

	this.environment = env;
    }

    public void exec() {
	SemantiCore.notification.print("[ I ] DomainController :");
	SemantiCore.notification
		.print("                        domain part requesting registration");
	registerDomain();
    }

    public void registerDomain() {
	String[] parameters = ((ControlMessage) getArguments()).getContent()
		.toString().split(" ");

	String address = parameters[0];
	String port = parameters[1];
	String domainPartName = parameters[2];
	String domainName = parameters[3];

	if (domainName.equals(environment.getDomainName())) {
	    Object[] param = { address, port, domainPartName };

	    try {
		if (!environment.getControlBridge().getDomainRoutingTable()
			.containsKey(domainPartName)) {
		    DomainPart part = new DomainPart(address, port,
			    environment.getDomainName(), domainPartName);

		    environment.getControlBridge().addPartToDomainRoutingTable(
			    part);

		    SemantiCore.notification
			    .print("[ I ] DomainController > remote platform registered");

		    new UpdateNewDomainPart(environment, "execution",
			    SCOnt.EXE_ACTION, "", part).start();
		} else {

		    ControlMessage m = new ControlMessage(
			    ControlMessage.REGISTER_DOMAIN_PART_REFUSED, "",
			    "", " domain part registered!");

		    environment.getControlBridge()
			    .sendMessage(m, address, port);

		    m = null;

		    SemantiCore.notification
			    .print("[ I ] DomainController > Registration refused!");
		}
	    } catch (Exception e) {
		SemantiCore.notification
			.print("[ E ] DomainController > Error ( RegisterPlatform) : "
				+ e.getMessage());
		e.printStackTrace();
	    }
	} else {
	    ControlMessage m = new ControlMessage(
		    ControlMessage.REGISTER_DOMAIN_PART_REFUSED, "", "",
		    "Wrong domain name!");

	    this.environment.getControlBridge().sendMessage(m, address, port);

	    SemantiCore.notification
		    .print("[ I ] DomainController > registration refused!");
	}
    }
}
