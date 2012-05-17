package semanticore.domain;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import semanticore.domain.control.ControlBridge;
import semanticore.domain.control.ControlMessage;
import semanticore.domain.control.DataBridge;
import semanticore.domain.control.Domain;
import semanticore.domain.control.DomainPart;
import semanticore.domain.gui.GUISemantiCore;
import semanticore.domain.model.AgentRoutingTable;
import semanticore.domain.model.Component;
import semanticore.domain.model.SemanticAgent;
import semanticore.domain.model.SemanticMessage;

final public class Environment implements Runnable {

    private Hashtable<String, SemanticAgent> localAgents = new Hashtable<String, SemanticAgent>(
	    2000);

    private LinkedList<SemanticAgent> platformAgents = new LinkedList<SemanticAgent>();

    private ControlBridge controlBridge;

    private DataBridge dataBridge;

    private String domainName;

    private String domainPartName = new String();

    private boolean partOfDomain;

    protected GUISemantiCore gui;

    protected Vector<Domain> DOMAINS = new Vector<Domain>(1, 1) {
	@Override
	public boolean contains(Object d) {
	    Domain domain = (Domain) d;
	    Iterator<Domain> iter = this.iterator();
	    while (iter.hasNext()) {
		if (iter.next().getDomainName().equals(domain.getDomainName()))
		    return true;
	    }

	    return false;
	}
    };

    public Environment(String domainName, Vector<Domain> domains) {
	this.domainName = domainName;

	if (domains.size() > 0)
	    this.DOMAINS = domains;

	this.partOfDomain = false;
    }

    public Environment(String domainName, String domainPartName) {
	this.partOfDomain = true;

	this.domainName = domainName;
	this.domainPartName = domainPartName;
    }

    public void run() {
	int tx = 0;

	while (true) {
	    sendAlive();
	    controlBridge.decMetric();

	    if (tx % 3 == 0) {
		refresh();
		interDomainConnections();
	    }

	    if (tx % 1000 == 0)
		tx = 0;

	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	    tx++;
	}
    }

    public void addDomain(Domain d) {
	if (!DOMAINS.contains(d))
	    this.DOMAINS.add(d);
    }

    public void initialize(ControlBridge controlBridge, DataBridge dataBridge,
	    LinkedList<SemanticAgent> platformAgent) {
	this.controlBridge = controlBridge;
	this.dataBridge = dataBridge;
	this.platformAgents = platformAgent;

	new Thread(controlBridge).start();
	new Thread(dataBridge).start();
    }

    public void initialize(ControlBridge controlBridge, DataBridge dataBridge) {
	this.controlBridge = controlBridge;
	this.dataBridge = dataBridge;

	new Thread(controlBridge).start();
	new Thread(dataBridge).start();
    }

    public String getDomainName() {
	return domainName;
    }

    public String getDomainPartName() {
	return domainPartName;
    }

    public boolean isPartOfDomain() {
	return partOfDomain;
    }

    public ControlBridge getControlBridge() {
	return controlBridge;
    }

    public DataBridge getDataBridge() {
	return dataBridge;
    }

    public synchronized boolean removeAgentByName(final String agentName) {
	if (this.localAgents.remove(agentName) != null) {
	    new Thread() {
		@Override
		public void run() {
		    try {
			Thread.sleep(4000);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		    getControlBridge().removeRemoteAgent(agentName);
		}
	    }.start();

	    return true;
	}

	return false;
    }

    public synchronized boolean addLocalAgent(SemanticAgent agent) {
	try {
	    Object r = this.localAgents.put(agent.getName(), agent);

	    if (r == null) {

		if (agent.isDistributed())
		    setupDistributedAgent(agent);
		else {
		    AgentRoutingTable art;

		    if (isPartOfDomain())
			art = new AgentRoutingTable(agent, getDomainPartName());
		    else
			art = new AgentRoutingTable(agent);

		    getControlBridge().addAgentToRoutingTable(art);
		}

		return true;
	    } else {
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();

	    return false;
	}
    }

    private void setupDistributedAgent(SemanticAgent sa) {
	AgentRoutingTable agent;

	try {
	    Thread.sleep(6000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	// ============

	Hashtable<String, Object> partes = sa.getParts();
	Enumeration<Object> iter = partes.elements();

	while (iter.hasMoreElements()) {
	    SemanticAgent a = (SemanticAgent) iter.nextElement();

	    Enumeration<Component> enumeration = a.getComponentsElements();

	    String destino = "";

	    while (enumeration.hasMoreElements()) {
		Component c = enumeration.nextElement();

		if (a.getDomainPartName().trim().length() > 0) {
		    agent = new AgentRoutingTable(a, a.getDomainPartName());
		    destino = a.getDomainPartName();
		} else {
		    agent = new AgentRoutingTable(a);
		    destino = a.getDomain();
		}

		getControlBridge().addAgentToRoutingTable(agent);
	    }

	    String from;

	    if (isPartOfDomain())
		from = getDomainPartName();
	    else
		from = getDomainName();

	    ControlMessage updateMessage = new ControlMessage(
		    ControlMessage.PART_OF_AGENT, from, "", a);

	    try {

		if (!destino.equals(getDomainName())) {
		    Domain part = getControlBridge().getDomainPart(destino);
		    getControlBridge().sendMessage(updateMessage,
			    part.getAddress(), part.getPort());
		} else
		    getControlBridge().sendMessageToMainDomain(updateMessage);
	    } catch (Exception e) {
		System.err
			.println("[ E ] Environment : Erro " + e.getMessage());
	    }
	}

	// ============

	Enumeration<Component> enumeration = sa.getComponentsElements();

	while (enumeration.hasMoreElements()) {
	    Component c = enumeration.nextElement();

	    if (isPartOfDomain())
		agent = new AgentRoutingTable(sa, getDomainPartName());
	    else
		agent = new AgentRoutingTable(sa);

	    getControlBridge().addAgentToRoutingTable(agent);
	}
    }

    public void sendSemanticMessage(SemanticMessage message) {
	try {
	    (new EnvironmentServiceThread(this,
		    EnvironmentServiceThread.SEND_SEMANTIC_MESSAGE,
		    new Object[] { message })).start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void sendControlMessage(ControlMessage message) {
	try {
	    (new EnvironmentServiceThread(this,
		    EnvironmentServiceThread.SEND_CONTROL_MESSAGE,
		    new Object[] { message })).start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Enumeration<SemanticAgent> getLocalAgentsElements() {
	return localAgents.elements();
    }

    public Hashtable<String, SemanticAgent> getLocalAgents() {
	return localAgents;
    }

    private void sendAlive() {
	if (isPartOfDomain()) {
	    try {
		ControlMessage m = null;

		m = new ControlMessage(ControlMessage.ALIVE, "", "",
			domainPartName);

		Enumeration<DomainPart> dominios = getControlBridge()
			.getDomainRoutingTableElements();
		while (dominios.hasMoreElements()) {
		    Domain part = dominios.nextElement();

		    getControlBridge().sendMessage(m, part.getAddress(),
			    part.getPort());
		}

		getControlBridge().sendMessageToMainDomain(m);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public String[] getAgentsName() {
	String[] result = null;

	int size = this.getControlBridge().getAgentRoutingTable().size();

	result = new String[size];

	Enumeration<AgentRoutingTable> e = this.getControlBridge()
		.getAgentRoutingTable().elements();

	int idx = 0;

	while (e.hasMoreElements()) {
	    try {
		AgentRoutingTable agent = e.nextElement();
		result[idx] = agent.getName();
		idx++;
	    } catch (Exception ex) {
		System.err.println("[ E ] > SemanticAgent : (getAgentsName) : "
			+ ex.getMessage());
	    }
	}

	return result;
    }

    public SemanticAgent getLocalAgentByName(String agentName) {
	SemanticAgent agent = localAgents.get(agentName);

	if (agent != null)
	    return agent;

	return null;
    }

    private void refresh() {
	try {
	    (new EnvironmentServiceThread(this,
		    EnvironmentServiceThread.REFRESH, null)).start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void interDomainConnections() {
	try {
	    (new EnvironmentServiceThread(this,
		    EnvironmentServiceThread.DOMAIN_CONNECTIONS, null)).start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void start() {
	new Thread(this).start();
    }

    protected LinkedList<SemanticAgent> getPlatformAgents() {
	return platformAgents;
    }

    // ====================================================================================================
    // ====================================================================================================
    public void move(SemanticAgent agent, String domainPartName) {
	Object[] param = { agent, domainPartName };

	(new EnvironmentServiceThread(this,
		EnvironmentServiceThread.MOVE_AGENT, param)).start();
    }
}
