package semanticore.domain.control;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.gui.GUISemantiCore;
import semanticore.domain.model.AgentRoutingTable;
import semanticore.domain.model.Message;
import semanticore.domain.model.SemanticAgent;
import semanticore.domain.model.Service;
import semanticore.domain.services.platform.ServiceFactory;

final public class ControlBridge extends Service {

    public static final int MAX_METRIC = 5;
    private Environment environment = null;
    private Hashtable<String, DomainPart> domainRoutingTable = new Hashtable<String, DomainPart>();
    private Hashtable<String, Domain> interDomainRoutingTable = new Hashtable<String, Domain>();
    private Hashtable<String, AgentRoutingTable> agentRoutingTable = new Hashtable<String, AgentRoutingTable>();
    protected LinkedList<SemanticAgent> platformAgents = new LinkedList<SemanticAgent>();

    private String mainPlatformAddress;
    private String mainPlatformPort;

    public ControlBridge(Environment env, String address, String port,
	    LinkedList<SemanticAgent> platformAgents) {
	super(address, port);

	this.platformAgents = platformAgents;
	this.environment = env;

	factoriesSetup();

	listener.start();
    }

    public ControlBridge(Environment env, String address, String port,
	    String mainAddress, String mainPort) {
	super(address, port);

	this.environment = env;

	this.mainPlatformAddress = mainAddress;
	this.mainPlatformPort = mainPort;

	factoriesSetup();

	listener.start();
    }

    private void factoriesSetup() {
	ServiceFactory sFact = new ServiceFactory(this);

	super.setServiceRequester(sFact.getServiceRequester());
	super.setServiceListener(sFact.getServiceListener());
    }

    public void run() {
	if (environment.isPartOfDomain()) {
	    SemantiCore.notification
		    .print("> Bridge : Enviando mensagem de registro");

	    this.solicitaRegistroDeParteDeDominio();
	}

	Vector messages;
	ControlMessage cm;

	while (true) {
	    messages = listener.getMessages();

	    for (int i = 0; i < messages.size(); i++) {
		cm = (ControlMessage) messages.get(i);

		(new ControlBridgeServiceThread(cm, this)).start();
	    }

	    try {
		Thread.sleep(80);
	    } catch (InterruptedException ex) {
		SemantiCore.notification
			.print(">>>> ControlBridge | Error : (run) : "
				+ ex.getMessage());
	    }
	}
    }

    public synchronized void solicitaRegistroDeParteDeDominio() {
	try {
	    String content = this.address + " " + this.port + " "
		    + environment.getDomainPartName() + " "
		    + environment.getDomainName();

	    ControlMessage m = new ControlMessage(
		    ControlMessage.REGISTER_DOMAIN_PART, this.address,
		    this.mainPlatformAddress, content);

	    sendMessageToMainDomain(m);

	    m = null;

	    SemantiCore.notification
		    .print("[ I ] ControlBridge > Mensagem de registro enviada");
	} catch (Exception e) {
	    System.err
		    .println("[ E ] ControlBridge > Erro ao enviar mensagem de registro");
	    e.printStackTrace();
	}
    }

    public synchronized void solicitaRegistroNoDominio(Domain remoteDomain) {
	try {
	    Domain domain = new Domain(this.address, this.port,
		    this.environment.getDomainName());

	    ControlMessage m = new ControlMessage(
		    ControlMessage.REGISTER_DOMAIN, this.address,
		    this.mainPlatformAddress, domain);

	    sendMessage(m, remoteDomain.address, remoteDomain.port);

	    m = null;

	    SemantiCore.notification
		    .print("[I] ControlBridge : Domain register message was sent.");
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] ControlBridge > Erro sendin register message.");
	    e.printStackTrace();
	}
    }

    public synchronized void sendMessage(ControlMessage message,
	    String address, String port) {
	this.requester.request(message, address, port);
    }

    public synchronized void sendMessageToMainDomain(ControlMessage message) {
	this.requester.request(message, this.mainPlatformAddress,
		this.mainPlatformPort);
    }

    public Hashtable<String, AgentRoutingTable> getAgentRoutingTable() {
	return agentRoutingTable;
    }

    public Hashtable<String, DomainPart> getDomainRoutingTable() {
	return domainRoutingTable;
    }

    public Enumeration<DomainPart> getDomainRoutingTableElements() {
	return domainRoutingTable.elements();
    }

    public Environment getEnvironment() {
	return environment;
    }

    public void addPartToDomainRoutingTable(DomainPart part) {
	this.domainRoutingTable.put(part.getDomainPartName(), part);

	setChanged();
	Object[] o = new Object[] { GUISemantiCore.ADD_DOMAIN_PART, part };
	notifyObservers(o);

	SemantiCore.notification
		.print("[ I ] ControlBridge > Parte de dominio adicionada na tabela de roteamento");
    }

    public void addDomainRoutingTable(Domain domain) {
	if (this.interDomainRoutingTable.put(domain.getDomainName(), domain) == null) {
	    setChanged();
	    Object[] o = new Object[] { GUISemantiCore.ADD_DOMAIN, domain };
	    notifyObservers(o);

	    SemantiCore.notification.print("[ I ] ControlBridge : Domain "
		    + domain.getDomainName() + " added");
	}
    }

    public void addAgentToRoutingTable(AgentRoutingTable agent) {
	setChanged();
	Object[] o = new Object[] { GUISemantiCore.ADD_AGENT, agent };
	notifyObservers(o);

	this.agentRoutingTable.put(agent.getKey(), agent);
    }

    public Enumeration<AgentRoutingTable> getAgentRoutingTableElements() {
	return agentRoutingTable.elements();
    }

    public void updateMetric(String domainPart) {
	DomainPart p = domainRoutingTable.get(domainPart);
	if (p != null)
	    p.setMetric(ControlBridge.MAX_METRIC);
    }

    public void decMetric() {
	Enumeration<DomainPart> parts = domainRoutingTable.elements();

	while (parts.hasMoreElements()) {
	    DomainPart part = (DomainPart) parts.nextElement();

	    part.decMetric();

	    if (part.getMetric() <= 0) {
		SemantiCore.notification
			.print("> ControlBridge : Metric = 0 - removing domain < "
				+ part.getDomainPartName() + " > ...");

		domainPartDown(part.getDomainPartName());

		return;
	    }
	}
    }

    protected synchronized void domainPartDown(String domainPartName) {
	try {
	    DomainPart p = domainRoutingTable.remove(domainPartName);

	    if (p != null) {
		setChanged();
		Object[] o = new Object[] { GUISemantiCore.REMOVE_DOMAIN_PART,
			p };
		notifyObservers(o);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	removeAgentFromDomain(domainPartName);
    }

    protected void removeAgentFromDomain(String domainName) {
	boolean b = false;
	do {
	    try {
		b = tRemove(domainName);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} while (b);
    }

    public void removeDomain(String domainName) {
	Domain d = interDomainRoutingTable.remove(domainName);

	if (d == null)
	    SemantiCore.notification
		    .print("[ I ] ControlBridge : domain not removed!");
	else {
	    SemantiCore.notification
		    .print("[ I ] ControlBridge : domain removed! ");

	    setChanged();
	    Object[] o = new Object[] { GUISemantiCore.REMOVE_DOMAIN, d };
	    notifyObservers(o);
	}
    }

    private boolean tRemove(String domainName) {
	Enumeration<AgentRoutingTable> agentes = agentRoutingTable.elements();

	while (agentes.hasMoreElements()) {
	    AgentRoutingTable ag = agentes.nextElement();

	    if (ag.getDomainName().equals(domainName)
		    || ag.getDomainPartName().equals(domainName)) {
		setChanged();
		Object[] o = new Object[] { GUISemantiCore.REMOVE_AGENT, ag };
		notifyObservers(o);

		agentRoutingTable.remove(ag.getKey());

		return true;
	    }
	}

	return false;
    }

    public boolean removeRemoteAgent(String agentName) {
	Enumeration<AgentRoutingTable> agentes = agentRoutingTable.elements();

	while (agentes.hasMoreElements()) {
	    AgentRoutingTable ag = agentes.nextElement();

	    if (ag.getName().equals(agentName)) {
		setChanged();
		Object[] o = new Object[] { GUISemantiCore.REMOVE_AGENT, ag };
		notifyObservers(o);

		agentRoutingTable.remove(ag.getKey());

		return true;
	    }
	}

	return false;
    }

    public void sendBroadcastToPlatformAgents(Message message) {
	Iterator<SemanticAgent> iter = platformAgents.iterator();

	while (iter.hasNext())
	    iter.next().put(message);
    }

    public AgentRoutingTable getRemoteAgentByName(String agentName) {
	Enumeration<String> keys = agentRoutingTable.keys();

	String agent = "";
	String key = null;

	while (keys.hasMoreElements()) {
	    key = keys.nextElement();

	    if (key.substring(0, key.indexOf(".")).equals(agentName)) {
		agent = key;
		break;
	    }
	}

	return agentRoutingTable.get(agent);
    }

    public Domain getDomainPart(String domainPartName) {
	return domainRoutingTable.get(domainPartName);
    }

    public Hashtable<String, Domain> getInterDomainRoutingTable() {
	return interDomainRoutingTable;
    }

    public void printInterDomainRoutingTable() {
	Enumeration<Domain> e = interDomainRoutingTable.elements();

	SemantiCore.notification
		.print("--------------------------------------");
	while (e.hasMoreElements()) {
	    Domain d = e.nextElement();
	    if (d.isSubDomain())
		SemantiCore.notification.print(environment.getDomainName()
			+ "." + d.getDomainName());
	    else
		SemantiCore.notification.print(d.getDomainName() + "."
			+ environment.getDomainName());

	    SemantiCore.notification.print("		Address : " + d.getAddress());
	    SemantiCore.notification.print("		Port 	: " + d.getPort());
	}
	SemantiCore.notification
		.print("--------------------------------------");
    }

    public String getMainPlatformAddress() {
	return mainPlatformAddress;
    }

    public String getMainPlatformPort() {
	return mainPlatformPort;
    }
}
