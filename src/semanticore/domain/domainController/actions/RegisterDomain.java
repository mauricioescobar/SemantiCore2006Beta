package semanticore.domain.domainController.actions;

import semanticore.agent.execution.model.Action;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlBridge;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.Domain;

final public class RegisterDomain extends Action {
    Environment environment;

    public RegisterDomain(Environment env, String descriptor, Object parameter,
	    Object arguments) {
	super("RegisterPlatform", descriptor);

	this.parameter = parameter;
	this.arguments = arguments;

	this.environment = env;
    }

    public void exec() {
	SemantiCore.notification
		.print("[ I ] DomainController : Register Platform action");
	registerPlatform();
    }

    public void registerPlatform() {
	Domain d = (Domain) ((ControlMessage) getArguments()).getContent();

	ControlBridge cb = environment.getControlBridge();

	d.setSubDomain(true);
	d.setConnected(true);

	cb.addDomainRoutingTable(d);

	ControlMessage m = new ControlMessage(ControlMessage.DOMAIN_REGISTERED,
		environment.getDomainName(), d.getDomainName(), "");

	environment.getControlBridge().sendMessage(m, d.getAddress(),
		d.getPort());
    }
}
