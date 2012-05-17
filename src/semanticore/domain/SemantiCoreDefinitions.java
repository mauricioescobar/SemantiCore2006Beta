package semanticore.domain;

import java.util.Hashtable;
import java.util.Vector;

import semanticore.domain.control.Domain;

public class SemantiCoreDefinitions {
    public static final String INSTANCIATION_FILE_NAME = "semanticoreinstantiation.xml";

    public static final String CONFIG_FILE_NAME = "semanticoreconfig.xml";

    public static final String DOMAIN_MODEL_SEMANTICACTION = "semanticore.domain.model.SemanticAction";

    public static final String DECISION_COMPONENT_NAME = "semanticore.agent.decision.DecisionComponent";

    public static final String EXECUTION_COMPONENT_NAME = "semanticore.agent.execution.ExecutionComponent";

    public static final String EFFECTOR_COMPONENT_NAME = "semanticore.agent.effector.EffectorComponent";

    public static Vector DATA_BRIDGE_HOTSPOT;

    public static Vector DATA_BRIDGE_HOTSPOT_TYPES;

    public static Hashtable<String, Hashtable<String, String>> agentes = new Hashtable<String, Hashtable<String, String>>();

    public static String EXECUTION_ENGINE_HOTSPOT = "";

    public static String DECISION_ENGINE_HOTSPOT = "";

    public static String EFFECTOR_ENGINE_HOTSPOT = "";

    public static Vector DOMAINS;

    public static boolean showSemanticoreGui = true;

    public static boolean showIntro = true;

    public static boolean notificationEnabled = false;

    public static String title = "SemantiCore 2006";

    public static String plataformName = "SemantiCore";

    public static String NOTIFICATION_CLASS = "semanticore.domain.services.notification.TerminalNotification";

    public SemantiCoreDefinitions() {
	super();

	DATA_BRIDGE_HOTSPOT = new Vector(1, 1);
	DATA_BRIDGE_HOTSPOT_TYPES = new Vector(1, 1);

	DOMAINS = new Vector<Domain>(1, 1);

	try {
	    ConfigFileLoader loader = new ConfigFileLoader(CONFIG_FILE_NAME);
	    loader = new ConfigFileLoader(INSTANCIATION_FILE_NAME);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print(">>>> SemantiCoreDefinitions : ERROr : "
			    + e.getMessage());
	}
    }
}
