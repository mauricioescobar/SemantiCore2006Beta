package semanticore.domain.model;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;

import semanticore.agent.decision.DecisionComponent;
import semanticore.agent.decision.DecisionEngine;
import semanticore.agent.decision.hotspots.GenericDecisionEngine;
import semanticore.agent.effector.Effector;
import semanticore.agent.effector.EffectorComponent;
import semanticore.agent.effector.hotspots.GenericEffector;
import semanticore.agent.execution.ExecutionComponent;
import semanticore.agent.execution.ExecutionEngine;
import semanticore.agent.execution.hotspots.SCWorkflowEngine;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.Rule;
import semanticore.agent.sensorial.Sensor;
import semanticore.agent.sensorial.SensorialComponent;
import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.SemantiCoreDefinitions;
import semanticore.domain.gui.GUIListGoals;
import semanticore.general.util.Metric;
import semanticore.general.util.OWLUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class SemanticAgent implements Serializable {

    protected String name;
    private String domain;
    protected String domainPartName;
    protected Environment environment;
    protected Model messageSchema = null;
    protected String namespace = SemantiCore.NAMESPACE;
    protected Hashtable<String, Component> components = new Hashtable<String, Component>();
    private Hashtable<String, Object> parts = new Hashtable<String, Object>();
    protected Hashtable<String, String> distributedComponents = new Hashtable<String, String>();
    private boolean distributed = false;
    protected boolean partOfAgent = false;
    protected Goal goal;
    protected Hashtable<String, Goal> goals = new Hashtable<String, Goal>();
    protected GUIListGoals guiGoals;
    protected String arg = null;
    public Metric metric;

    public SemanticAgent(Environment env, String agentName, String arg) {
	this.name = agentName;
	this.arg = arg;

	try {
	    if (env != null) {
		this.environment = env;

		this.domain = ((Environment) this.environment).getDomainName();
		this.domainPartName = ((Environment) this.environment)
			.getDomainPartName();

		internalSetup();
		setup();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void endGoal(String planName) {
	try {
	    Enumeration<Goal> iter = goals.elements();
	    while (iter.hasMoreElements()) {
		Goal g = iter.nextElement();
		ActionPlan p = g.getActionPlan();
		if (p != null) {
		    if (p.getName().equals(planName))
			g.restart();
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    protected void internalSetup() {
	SensorialComponent sComp = new SensorialComponent(this);
	addComponent(sComp.getName(), sComp);

	DecisionComponent dComp = new DecisionComponent(this);
	addComponent(dComp.getName(), dComp);

	if (!setDecisionEngine(SemantiCoreDefinitions.DECISION_ENGINE_HOTSPOT))
	    setDecisionEngine(new GenericDecisionEngine());

	ExecutionComponent eComp = new ExecutionComponent(this);
	addComponent(eComp.getName(), eComp);
	if (!setExecutionEngine(SemantiCoreDefinitions.EXECUTION_ENGINE_HOTSPOT))
	    setExecutionEngine(new SCWorkflowEngine());

	EffectorComponent efComp = new EffectorComponent(this);
	efComp.addEffector(new GenericEffector(efComp));
	addComponent(efComp.getName(), efComp);

	sComp.addTransmissionComponent(dComp.getName());
	dComp.addTransmissionComponent(eComp.getName());
	eComp.addTransmissionComponent(efComp.getName());

	eComp.start();
	dComp.start();
	sComp.start();
	efComp.start();
    }

    public boolean put(Object message) {
	if (!isPartOfAgent()) {
	    Component c = this.components.get("sensorial");
	    c.put(message);
	    return true;
	}

	return false;
    }

    public void setupGoals() {
	if (guiGoals == null || !guiGoals.isDisplayable()) {
	    guiGoals = new GUIListGoals(this.goals);
	}
    }

    protected abstract void setup();

    protected void addRule(Rule rule) {
	getDecisionComponent().addRule(rule);
    }

    public boolean addSensor(Sensor sensor) {
	SensorialComponent s = (SensorialComponent) this
		.getComponent("sensorial");
	return s.addSensor(sensor);
    }

    protected void setDecisionEngine(DecisionEngine dEngine) {
	DecisionComponent dComponent = (DecisionComponent) this
		.getComponent("decision");
	dComponent.setDecisionEngine(dEngine);
    }

    protected boolean setDecisionEngine(String decisionEngine) {
	Class c;

	try {
	    c = getClass().getClassLoader().loadClass(decisionEngine);
	    DecisionEngine de = (DecisionEngine) c.getConstructor(
		    DecisionEngine.class.getConstructors()[0]
			    .getParameterTypes()).newInstance(null);
	    this.setDecisionEngine(de);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    protected void setExecutionEngine(ExecutionEngine eEngine) {
	ExecutionComponent eComponent = (ExecutionComponent) this
		.getComponent("execution");
	eComponent.setExecutionEngine(eEngine);
    }

    protected boolean setExecutionEngine(String executionEngine) {

	Class c;

	try {
	    c = getClass().getClassLoader().loadClass(executionEngine);
	    ExecutionEngine ee = (ExecutionEngine) c.getConstructor(
		    ExecutionEngine.class.getConstructors()[0]
			    .getParameterTypes()).newInstance(null);
	    this.setExecutionEngine(ee);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    protected boolean addEffector(Effector eff) {
	try {
	    return getEffectorComponent().addEffector(eff);
	} catch (Exception e) {

	}
	return false;
    }

    protected DecisionComponent getDecisionComponent() {
	DecisionComponent d = (DecisionComponent) this.getComponent("decision");
	return d;
    }

    public ExecutionComponent getExecutionComponent() {
	ExecutionComponent e = (ExecutionComponent) this
		.getComponent("execution");
	return e;
    }

    public EffectorComponent getEffectorComponent() {
	EffectorComponent ef = null;

	try {
	    ef = (EffectorComponent) this.getComponent("effector");
	} catch (Exception e) {
	}

	return ef;
    }

    public SensorialComponent getSensorialComponent() {
	SensorialComponent s = null;

	try {
	    s = (SensorialComponent) this.getComponent("sensorial");
	} catch (Exception e) {
	    s = null;
	}

	return s;
    }

    public ActionPlan addActionPlan(String planName) {
	try {
	    ActionPlan plan = new ActionPlan(planName);

	    addActionPlan(plan);

	    return plan;
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    public boolean addActionPlan(ActionPlan plan) {
	boolean sts = true;
	try {
	    ExecutionComponent e = (ExecutionComponent) this
		    .getComponent("execution");

	    e.addActionPlan(plan);
	} catch (Exception e) {
	    sts = false;
	}
	return sts;
    }

    public void restartBasicComponents() {
	try {
	    SensorialComponent sc = (SensorialComponent) getComponent("sensorial");
	    sc.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    DecisionComponent dc = (DecisionComponent) getComponent("decision");
	    dc.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    ExecutionComponent ec = (ExecutionComponent) getComponent("execution");
	    ec.start();
	    ec.engine.resume();

	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    EffectorComponent efc = (EffectorComponent) getComponent("effector");
	    efc.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    protected void addComponent(String type, Component component) {
	try {
	    this.components.put(type, component);
	} catch (Exception e) {

	}
    }

    public void setDistributedComponent(String componentName,
	    String domainPartName) {
	if (!componentName.equalsIgnoreCase("sensorial")) {

	    Component c = this.components.remove(componentName);

	    if (c != null) {
		c.stop();
		addPartOfAgent(domainPartName, c);
	    }
	}
    }

    public void removeComponent(String type) {
	try {
	    this.components.remove(type);
	} catch (Exception e) {
	    SemantiCore.notification.print("[ E ] Error removing component!");
	}
    }

    public Component getComponent(String type) {
	return this.components.get(type);
    }

    public int getComponentsSize() {
	return this.components.size();
    }

    public OntModel createOntoMessage(String from, String to, OntModel content) {
	OntModel messageData = ModelFactory.createOntologyModel();

	messageData.createIndividual(namespace + "message1",
		messageSchema.getResource(namespace + "Message"));

	messageData.add((Statement) messageData.createStatement(
		(Resource) messageData.getResource(namespace + "message1"),
		messageSchema.getProperty(namespace + "from"), from));
	messageData.add((Statement) messageData.createStatement(
		(Resource) messageData.getResource(namespace + "message1"),
		messageSchema.getProperty(namespace + "to"), to));

	String ontoToXML = OWLUtil.parseModelToXML(content);
	messageData.add((Statement) messageData.createStatement(
		(Resource) messageData.getResource(namespace + "message1"),
		messageSchema.getProperty(namespace + "content"),
		messageData.createTypedLiteral(ontoToXML)));

	return messageData;
    }

    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    public Model getMessageSchema() {
	return messageSchema;
    }

    // FACT
    public void addFact(Fact fact) {
	getDecisionComponent().addFact(fact);
    }

    public Fact getFact(String factId) {
	return getDecisionComponent().getFact(factId);
    }

    public boolean removeFact(String factId) {
	return getDecisionComponent().removeFact(factId);
    }

    public String getName() {
	return name;
    }

    public String getDomain() {
	return domain;
    }

    public Enumeration<Component> getComponentsElements() {
	return components.elements();
    }

    public boolean sendMessage(SemanticMessage message) {
	try {
	    if (environment == null) {
		return false;
	    }

	    ((Environment) this.environment)
		    .sendSemanticMessage((SemanticMessage) message);
	} catch (Exception e) {
	    System.err.println("[ E ] SemanticAgent : SendMessage error : "
		    + e.getMessage());

	    return false;
	}

	return true;
    }

    public boolean isDistributed() {
	return distributed;
    }

    public String getDomainPartName() {
	return domainPartName;
    }

    public void removeAllComponents() {
	Enumeration<Component> e = components.elements();
	while (e.hasMoreElements())
	    e.nextElement().running = false;

	this.components.clear();
    }

    public void listComponents() {
	Enumeration<Component> list = components.elements();
	while (list.hasMoreElements())
	    SemantiCore.notification.print(list.nextElement().getName());
    }

    public Hashtable<String, Component> getComponents() {
	return components;
    }

    public boolean isPartOfAgent() {
	return partOfAgent;
    }

    public String isDistributedComponents(String componentName) {
	String dName = distributedComponents.get(componentName);

	if (dName == null)
	    return "";
	else
	    return dName;
    }

    private void addPartOfAgent(String domainName, Component c) {
	this.distributed = true;

	try {
	    if (!parts.containsKey(domainName)) {
		Class[] stringArgsClass = new Class[] { Environment.class,
			String.class, String.class };
		Object[] stringArgs = new Object[] { null, this.getName(), null };

		Constructor constructor;
		constructor = this.getClass().getConstructor(stringArgsClass);

		Object obj = constructor.newInstance(stringArgs);
		SemanticAgent agD = (SemanticAgent) obj;

		agD.domain = this.getDomain();
		agD.domainPartName = "";
		agD.partOfAgent = true;

		if (!((Environment) this.environment).getDomainName().equals(
			domainName))
		    agD.domainPartName = domainName;

		agD.distributedComponents = (Hashtable<String, String>) this.distributedComponents
			.clone();

		this.distributedComponents.put(c.getName(), domainName);

		c.setOwner(agD);
		agD.components.put(c.getName(), c);

		Enumeration<Component> ccc = this.components.elements();
		while (ccc.hasMoreElements()) {
		    if (this.domainPartName.trim().length() > 0)
			agD.distributedComponents.put(ccc.nextElement()
				.getName(), this.domainPartName);
		    else
			agD.distributedComponents.put(ccc.nextElement()
				.getName(), this.domain);
		}

		parts.put(domainName, agD);
	    } else {
		SemanticAgent agD = (SemanticAgent) parts.get(domainName);

		c.setOwner(agD);
		agD.components.put(c.getName(), c);

		this.distributedComponents.put(c.getName(), domainName);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Hashtable<String, Object> getParts() {
	return parts;
    }

    public void setEnvironment(Environment env) {
	this.environment = env;
    }

    public void moveTo(String domainPartName) {
	try {

	    try {
		beforeMove();
	    } catch (Exception e) {
	    }

	    ((Environment) environment).move(this, domainPartName);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] > SemanticAgent : (moveTo) : "
			    + e.getMessage());
	}
    }

    public void beforeMove() {
	SemantiCore.notification.print("> " + this.getName()
		+ " : before move!");
    }

    public void afterMove() {
	SemantiCore.notification
		.print("> " + this.getName() + " : after move!");
    }

    protected boolean addGoal(Goal g) {
	try {
	    goals.put(g.getID(), g);

	    return true;
	} catch (Exception e) {
	    // TODO: handle exception
	}

	return false;
    }

    protected Goal getGoal(String id) {
	return goals.get(id);
    }

    public Goal getGoal() {
	return this.goal;
    }
}
