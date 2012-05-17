package semanticore.domain.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.ControlBridge;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.Domain;
import semanticore.general.util.SynchronizedQueue;

public abstract class Component<ENGINE> implements Serializable, Runnable {
    protected ComponentInterpreter interpreter;

    protected SemanticAgent owner;

    protected String componentName;

    protected boolean running;

    protected SynchronizedQueue msgBuffer;

    protected LinkedList<String> transmissionComponent;

    protected Engine engine;

    public Component(SemanticAgent owner, String componentName) {
	this.componentName = componentName;
	this.owner = owner;

	msgBuffer = new SynchronizedQueue();

	transmissionComponent = new LinkedList<String>();
    }

    public SemanticAgent getOwner() {
	return owner;
    }

    protected void transmit(Object information) {
	boolean componentMessage = false;

	if (information instanceof ComponentMessage) {
	    ComponentMessage m = (ComponentMessage) information;

	    componentMessage = true;
	}

	{
	    Iterator<String> iter = transmissionComponent.iterator();

	    while (iter.hasNext()) {
		String name = iter.next();

		if (componentMessage) {
		    try {
			name = ((ComponentMessage) information).getTo()[0];
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

		try {
		    sendToComponent(name, information);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		if (componentMessage)
		    break;
	    }
	}
    }

    protected void sendToComponent(String name, Object information) {
	Component component = owner.components.get(name);

	if (component != null) {
	    component.put(information);
	} else {

	    String domainPartName = owner.isDistributedComponents(name);

	    if (domainPartName.trim().length() > 0) {
		String from = owner.getName() + "." + componentName;
		String to = owner.getName() + "." + name;

		SemantiCore.notification.print("[ I ] Component [ "
			+ this.getName() + " ] - Transmit:");
		SemantiCore.notification.print("\tTo  : " + to);

		ControlMessage updateMessage = new ControlMessage(
			ControlMessage.AGENT_INTERNAL_TRANSMISSION, from, to,
			information);

		ControlBridge cBridge = ((Environment) owner.environment)
			.getControlBridge();

		if (domainPartName.equals(owner.getDomain())) {
		    cBridge.sendMessageToMainDomain(updateMessage);
		} else {
		    Domain dPart = cBridge.getDomainPart(domainPartName);

		    if (dPart != null)
			cBridge.sendMessage(updateMessage, dPart.getAddress(),
				dPart.getPort());
		    else
			SemantiCore.notification
				.print("[ E ] SemanticAgent [Component] : Component <"
					+ name + "> not found");
		}
	    } else
		SemantiCore.notification
			.print("[ E ] SemanticAgent [Component] : Component <"
				+ name + "> not found");
	}
    }

    public abstract void put(Object information);

    public abstract void addTransmissionComponent(String component);

    public String getName() {
	return componentName;
    }

    public void start() {
	running = true;
	new Thread(this).start();
    }

    public void setOwner(SemanticAgent owner) {
	this.owner = owner;
    }

    public void stop() {
	this.running = false;
    }

    public ENGINE getEngine() {
	return (ENGINE) engine;
    }
}
