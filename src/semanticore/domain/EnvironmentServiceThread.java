package semanticore.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import semanticore.domain.control.ControlBridge;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.DataBridge;
import semanticore.domain.control.Domain;
import semanticore.domain.control.DomainPart;
import semanticore.domain.model.AgentRoutingTable;
import semanticore.domain.model.SemanticAgent;
import semanticore.domain.model.SemanticMessage;

final public class EnvironmentServiceThread extends Thread {
    public static final int CREATE_AGENT = 1;

    public static final int LOAD_AGENT = 2;

    public static final int SEND_SEMANTIC_MESSAGE = 3;

    public static final int SEND_CONTROL_MESSAGE = 4;

    public static final int REFRESH = 5;

    public static final int DOMAIN_CONNECTIONS = 6;

    public static final int MOVE_AGENT = 7;

    private int task = -1;

    private Object[] paramToTask;

    private Environment environment;

    public EnvironmentServiceThread(Environment env, int task,
	    Object[] paramToTask) {
	this.environment = env;
	this.task = task;
	this.paramToTask = paramToTask;
    }

    public void run() {
	try {
	    switch (task) {
	    case CREATE_AGENT:
		Thread.sleep(200);
		createAgent((SemanticAgent) paramToTask[0]);
		break;
	    case LOAD_AGENT:
		loadAgent();
		break;
	    case SEND_SEMANTIC_MESSAGE:
		sendSemanticMessage();
		break;
	    case SEND_CONTROL_MESSAGE:
		// sendControlMessage ( ( ControlMessage ) paramToTask [ 0 ] );
		break;
	    case REFRESH:
		refresh();
		break;
	    case DOMAIN_CONNECTIONS:
		interDomainConnections();
		break;
	    case MOVE_AGENT:
		move();
		break;
	    }
	} catch (Exception e) {
	    SemantiCore.notification.print("[ E ] SCService : Erro ( run ) > "
		    + e.getMessage());
	}
    }

    private synchronized void createAgent(SemanticAgent sa) {
	try {
	    if (!this.environment.addLocalAgent(sa)) {
		SemantiCore.notification.print("[ E ] Environment > Agent "
			+ sa.getName() + " is already registered!");
		return;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private synchronized void loadAgent() {
	String agentName = paramToTask[0].toString();
	String className = paramToTask[1].toString();
	String arg = paramToTask[2].toString();

	try {
	    Object agent;

	    Class c;

	    Class[] stringArgsClass = new Class[] { Environment.class,
		    String.class, String.class };

	    Object[] stringArgs = new Object[] { environment, agentName, arg };

	    Constructor stringArgsConstructor;

	    c = Class.forName(className);

	    stringArgsConstructor = c.getConstructor(stringArgsClass);

	    agent = createObject(stringArgsConstructor, stringArgs);

	    SemanticAgent a = (SemanticAgent) agent;

	    createAgent(a);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static Object createObject(Constructor constructor,
	    Object[] arguments) {
	Object object = null;

	try {
	    object = constructor.newInstance(arguments);
	    return object;
	} catch (InstantiationException e) {
	    SemantiCore.notification.print(e.getMessage());
	} catch (IllegalAccessException e) {
	    SemantiCore.notification.print(e.getMessage());
	} catch (IllegalArgumentException e) {
	    SemantiCore.notification.print(e.getMessage());
	} catch (InvocationTargetException e) {
	    SemantiCore.notification.print(e.getMessage());
	}

	return object;
    }

    private synchronized void sendSemanticMessage() {

	LinkedList<String> mensagensEnviadas = new LinkedList();

	SemanticMessage message = (SemanticMessage) paramToTask[0];

	if (message.getDomainTo() != null) {
	    ControlMessage cm = new ControlMessage(-1,
		    environment.getDomainName(), message.getDomainTo(), message);

	    cm.setDomain(environment.getDomainName());
	    message.setDomainFrom(environment.getDomainName());

	    if (environment.isPartOfDomain()) {
		cm.setFrom(environment.getDomainName());
		environment.getControlBridge().sendMessageToMainDomain(cm);
	    } else
		environment.getControlBridge()
			.sendBroadcastToPlatformAgents(cm);

	    return;
	}

	String[] names;

	try {
	    if (message.getTo()[0].equals(SemanticMessage.sendToAll))
		names = environment.getAgentsName();
	    else
		names = message.getTo();
	} catch (Exception e) {
	    SemantiCore.notification.print("[ E ] Environment : Error "
		    + e.getMessage());
	    return;
	}

	for (int i = 0; i < names.length; i++) {
	    String agentName = names[i];

	    SemanticAgent sa = null;

	    boolean aFound = false;

	    try {

		sa = environment.getLocalAgentByName(agentName);

		if (sa != null) {
		    if (sa.isPartOfAgent())
			aFound = false;
		    else
			aFound = true;
		} else
		    aFound = false;
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    if (aFound) {

		sa.put(message);
	    } else

	    {
		try {
		    AgentRoutingTable agenteRemoto = environment
			    .getControlBridge().getRemoteAgentByName(agentName);

		    if (agenteRemoto != null) {

			DataBridge db = environment.getDataBridge();

			String address;
			String port;

			if (agenteRemoto.getDomainPartName().trim().length() > 0) {
			    Domain domainPart = environment.getControlBridge()
				    .getDomainPart(
					    agenteRemoto.getDomainPartName());
			    address = domainPart.getAddress();
			    port = domainPart.getPort();
			} else {
			    address = environment.getControlBridge()
				    .getMainPlatformAddress();
			    port = environment.getControlBridge()
				    .getMainPlatformPort();
			}

			port = Integer.toString(Integer.parseInt(port) + 1);

			if (mensagensEnviadas.contains(address + port))
			    continue;

			if (db != null) {
			    try {
				db.getRequester().request(message, address,
					port);
				mensagensEnviadas.add(address + port);
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}

		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private void refresh() {
	String from;

	if (environment.isPartOfDomain())
	    from = environment.getDomainPartName();
	else
	    from = environment.getDomainName();

	ControlMessage updateMessage;

	Enumeration<SemanticAgent> agentesLocais = environment
		.getLocalAgentsElements();

	boolean partOfDomain = environment.isPartOfDomain();

	while (agentesLocais.hasMoreElements()) {
	    Enumeration<DomainPart> dominios = environment.getControlBridge()
		    .getDomainRoutingTableElements();

	    SemanticAgent agent = agentesLocais.nextElement();

	    if (!agent.isPartOfAgent()) {
		AgentRoutingTable ag;

		if (partOfDomain)
		    ag = AgentRoutingTable.parser.parse(agent,
			    environment.getDomainPartName());
		else
		    ag = AgentRoutingTable.parser.parse(agent);

		updateMessage = new ControlMessage(ControlMessage.AGENT_UPDATE,
			from, "", ag);

		while (dominios.hasMoreElements()) {
		    Domain part = dominios.nextElement();
		    environment.getControlBridge().sendMessage(updateMessage,
			    part.getAddress(), part.getPort());
		}

		if (partOfDomain)
		    environment.getControlBridge().sendMessageToMainDomain(
			    updateMessage);
	    }
	}

	if (!partOfDomain) {
	    Iterator<SemanticAgent> agentesDaPlataforma = environment
		    .getPlatformAgents().iterator();

	    while (agentesDaPlataforma.hasNext()) {
		SemanticAgent agent = agentesDaPlataforma.next();

		AgentRoutingTable ag = new AgentRoutingTable(agent);

		environment.getControlBridge().addAgentToRoutingTable(ag);
	    }
	}
    }

    private void interDomainConnections() {

	ControlBridge cb = this.environment.getControlBridge();

	Iterator<Domain> iter = environment.DOMAINS.iterator();

	while (iter.hasNext()) {
	    Domain d = iter.next();

	    try {
		if (!d.isConnected()) {
		    d.setMetric(ControlBridge.MAX_METRIC);

		    cb.solicitaRegistroNoDominio(d);

		    cb.addDomainRoutingTable(d);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	Enumeration<Domain> elements = environment.getControlBridge()
		.getInterDomainRoutingTable().elements();

	while (elements.hasMoreElements()) {
	    Domain d = elements.nextElement();

	    try {
		if (d.isConnected()) {
		    d.decMetric();

		    ControlMessage m = new ControlMessage(
			    ControlMessage.DOMAIN_ALIVE,
			    environment.getDomainName(), d.getDomainName(), "");
		    cb.sendMessage(m, d.getAddress(), d.getPort());

		    if (d.getMetric() < 0) {
			SemantiCore.notification
				.print("[ I ] Environment (ST) : Metric 0");
			SemantiCore.notification.print("\tRemoving domain : "
				+ d.getDomainName());

			cb.removeDomain(d.getDomainName());

			d.setConnected(false);
		    }
		}

	    } catch (Exception e) {
		SemantiCore.notification.print("[ E ] Environemnt (ST) : "
			+ e.getMessage());
	    }
	}
    }

    // ====================================================================================================
    public void move() {
	try {
	    SemanticAgent agent = (SemanticAgent) paramToTask[0];
	    String domainPartName = (String) paramToTask[1];

	    SemantiCore.notification
		    .print("> Environment | (move) migrating agent to "
			    + domainPartName);

	    if (!agent.isDistributed()) {

		environment.getLocalAgents().remove(agent.getName());

		agent.setEnvironment(null);

		environment.getControlBridge().removeRemoteAgent(
			agent.getName());

		ControlMessage m = new ControlMessage(
			ControlMessage.MOBILE_AGENT, "from", domainPartName,
			agent);

		ControlMessage notify = new ControlMessage(
			ControlMessage.REMOVE_REMOTE_AGENT, "", "",
			agent.getName());

		{
		    Enumeration<DomainPart> elements = environment
			    .getControlBridge().getDomainRoutingTableElements();

		    while (elements.hasMoreElements()) {
			DomainPart p = elements.nextElement();

			environment.getControlBridge().sendMessage(notify,
				p.getAddress(), p.getPort());
		    }
		}

		try {

		    if (!domainPartName.equals(environment.getDomainName())) {
			Domain part = environment.getControlBridge()
				.getDomainPart(domainPartName);
			environment.getControlBridge().sendMessage(m,
				part.getAddress(), part.getPort());
			environment.getControlBridge().sendMessage(notify,
				part.getAddress(), part.getPort());
		    } else {
			environment.getControlBridge().sendMessageToMainDomain(
				m);
			environment.getControlBridge().sendMessageToMainDomain(
				notify);
		    }
		} catch (Exception e) {
		    System.err.println("[ E ] Environment (move) : Erro "
			    + e.getMessage());
		    e.printStackTrace();
		}

		agent = null;
		System.gc();
	    } else
		SemantiCore.notification
			.print("[ E ] Environment : a distributed agent cannot be moved.!");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    // ====================================================================================================
}
