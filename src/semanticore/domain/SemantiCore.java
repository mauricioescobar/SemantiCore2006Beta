package semanticore.domain;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import semanticore.domain.communicationManager.CommunicationManager;
import semanticore.domain.control.ControlBridge;
import semanticore.domain.control.DataBridge;
import semanticore.domain.domainController.DomainController;
import semanticore.domain.gui.GUISemantiCore;
import semanticore.domain.gui.Introduction;
import semanticore.domain.model.SemanticAgent;
import semanticore.domain.services.notification.Notification;
import semanticore.domain.services.notification.NotificationFactory;

public class SemantiCore {
    public static String NAMESPACE = "http://semanticore.pucrs.br#";

    protected Environment environment = null;

    private static Introduction intro = null;

    public static Notification notification;

    private static NotificationFactory notificationFactory;

    public SemantiCore(String[] args) {
	initialize();

	new SemantiCoreDefinitions();

	loadSemanticore(args);
    }

    private void incIntroProgress() {
	if (intro != null)
	    intro.inc();
    }

    private void loadSemantiCore(String localPort, String domainPartName,
	    String mainAddress, String mainPort, String domain) {

	try {

	    if (mainAddress.equalsIgnoreCase("localhost"))
		mainAddress = InetAddress.getLocalHost().getHostAddress();
	    else {
		InetAddress address = InetAddress.getByName(mainAddress);
		mainAddress = address.getHostAddress();
	    }

	    {
		environment = new Environment(domain, domainPartName);
		environment.gui = new GUISemantiCore(environment);

		String address = InetAddress.getLocalHost().getHostAddress();

		ControlBridge controlBridge = new ControlBridge(environment,
			address, localPort, mainAddress, mainPort);

		controlBridge.addObserver(environment.gui);

		DataBridge dataBridge = new DataBridge(environment, address,
			Integer.toString((Integer.parseInt(localPort) + 1)));

		environment.initialize(controlBridge, dataBridge);

		setupAgents(environment);

		environment.start();

		if (intro != null)
		    intro.dispose();

		Thread.sleep(200);

		if (SemantiCoreDefinitions.showSemanticoreGui)
		    environment.gui.showUp();
	    }
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] Error initializing SemantiCore");
	}
    }

    private void initialize() {
	try {
	    System.out.println("[V_22.05.07]");

	    notificationFactory = new NotificationFactory();
	    SemantiCore.notification = notificationFactory.getNotification();
	    SemantiCore.notification
		    .setEnabled(SemantiCoreDefinitions.notificationEnabled);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	new SemantiCore(args);
    }

    private void loadSemanticore(String[] args) {
	try {

	    if (SemantiCoreDefinitions.showIntro) {
		intro = new Introduction();
		intro.showUp();
	    }

	    if (args[0].equalsIgnoreCase("MAIN"))
		loadMainPlatform(args);
	    else if (args[0].equalsIgnoreCase("REMOTE"))
		loadRemotePlatform(args);
	    else {
		throw new Exception("SemantiCore parameters error.");
	    }

	    if (intro != null) {
		intro.dispose();
		intro = null;
	    }
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(null, "Incorrect parameteres!",
		    "Erro", JOptionPane.ERROR_MESSAGE);
	    SemantiCore.notification.print(e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}
    }

    private void loadMainPlatform(String args[]) {
	if (args.length == 3) {
	    try {
		String port = args[1];
		String domain = args[2];

		// Environment -----------
		{
		    incIntroProgress();// 1

		    environment = new Environment(domain,
			    SemantiCoreDefinitions.DOMAINS);
		    environment.gui = new GUISemantiCore(environment);

		    incIntroProgress();

		    String address = InetAddress.getLocalHost()
			    .getHostAddress();

		    DomainController domainController = new DomainController(
			    environment, "DomainController", "");
		    CommunicationManager communicationManager = new CommunicationManager(
			    environment, "CommunicationManager", "");

		    incIntroProgress();

		    LinkedList<SemanticAgent> platformAgents = new LinkedList<SemanticAgent>();

		    incIntroProgress();

		    platformAgents.add(domainController);
		    platformAgents.add(communicationManager);

		    incIntroProgress();

		    ControlBridge controlBridge = new ControlBridge(
			    environment, address, port, platformAgents);

		    controlBridge.addObserver(environment.gui);

		    incIntroProgress();

		    DataBridge dataBridge = new DataBridge(environment,
			    address,
			    Integer.toString((Integer.parseInt(port) + 1)));

		    incIntroProgress();

		    environment.initialize(controlBridge, dataBridge,
			    platformAgents);

		    incIntroProgress();

		    setupAgents(environment);

		    incIntroProgress();

		    environment.start();

		    incIntroProgress();

		    if (intro != null)
			intro.dispose();

		    Thread.sleep(200);

		    if (SemantiCoreDefinitions.showSemanticoreGui)
			environment.gui.showUp();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private void loadRemotePlatform(String args[]) {
	loadSemantiCore(args[1], args[2], args[3], args[4], args[5]);
    }

    private void setupAgents(Environment env) {
	Enumeration<Hashtable<String, String>> agents = SemantiCoreDefinitions.agentes
		.elements();

	while (agents.hasMoreElements()) {
	    try {
		Hashtable<String, String> agent = agents.nextElement();
		String name = agent.get("name");

		if (name != null) {
		    String agentClass = agent.get("class");
		    String parameter = agent.get("param");

		    if (agentClass != null) {
			if (parameter == null)
			    parameter = "";

			Object[] param = { name, agentClass, parameter };
			new EnvironmentServiceThread(environment,
				EnvironmentServiceThread.LOAD_AGENT, param)
				.start();
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
