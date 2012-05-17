package semanticore.domain.model;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;

import semanticore.agent.execution.model.Action;
import semanticore.agent.execution.model.ActionPlan;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.Rule;
import semanticore.domain.actions.lib.ExecuteAction;
import semanticore.domain.actions.lib.ExecuteGoalAction;
import semanticore.domain.actions.lib.ExecuteProcessAction;
import semanticore.domain.gui.GUIGoal;
import semanticore.general.util.OWLUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Goal {
    private static int uniqueId = 0;

    private String id = "";

    protected transient OntModel ontology;

    protected transient OntModel base = ModelFactory.createOntologyModel();

    protected ActionPlan plan = null;

    protected SemanticAgent owner;

    protected String description = "";

    protected String name = "";

    protected Fact preCondition;

    protected Fact postCondition;

    protected Object startData;

    protected GoalStatus status = GoalStatus.Stopped;

    protected LinkedList<Rule> rules = new LinkedList<Rule>();

    public static enum GoalStatus {
	Started, Stopped, SearchingKnowledge
    };

    public LinkedList<GoalInput> concepts;

    protected GUIGoal gui = null;

    protected GregorianCalendar cal = new GregorianCalendar();

    protected long timestamp = cal.getTimeInMillis();

    public Goal(SemanticAgent owner, OntModel ontology) {
	this.owner = owner;
	this.ontology = ontology;
	this.id = Integer.toString(uniqueId++);
	this.name = "Goal_" + id;
	this.description = name;

	if (ontology != null)
	    OWLUtil.copyOntModelContent(ontology, base);

	start();
    }

    public Goal(SemanticAgent owner, OntModel ontology, ActionPlan plan,
	    Fact preCondition) {
	this.owner = owner;
	this.ontology = ontology;
	this.id = Integer.toString(uniqueId++);
	this.plan = plan;
	this.preCondition = preCondition;
	this.name = "Goal_" + id;
	this.description = name;

	if (ontology != null)
	    OWLUtil.copyOntModelContent(ontology, base);

	if (preCondition != null)
	    owner.addRule(new Rule("Goal_" + this.id + "_preCondition",
		    preCondition, new ExecuteGoalAction(this)));
	else
	    start();
    }

    public Goal(SemanticAgent owner, OntModel ontology, ActionPlan plan,
	    GoalInput[] concepts) {
	this.owner = owner;
	this.ontology = ontology;
	this.id = Integer.toString(uniqueId++);
	this.plan = plan;
	this.name = "Goal_" + id;
	this.description = name;

	if (ontology != null)
	    OWLUtil.copyOntModelContent(ontology, base);

	try {
	    this.concepts = new LinkedList<GoalInput>();
	    for (int i = 0; i < concepts.length; i++)
		this.concepts.add(concepts[i]);
	} catch (Exception e) {
	    // TODO: handle exception
	}
    }

    public void setPlan(ActionPlan plan) {
	this.plan = plan;
    }

    public void setupActions(ActionPlan plan) {
	try {
	    // cadastra as acoes no decisorio
	    Iterator<Action> iter = plan.getActionsList().iterator();
	    while (iter.hasNext()) {
		Action a = iter.next();

		if (a.getPreCondition() != null) {

		    ExecuteAction eAction = new ExecuteAction(a.getName(),
			    Long.toString(a.getTimestamp()),
			    getQuestions(a.getPreCondition()));

		    Rule r = new Rule("Action_" + a.getName() + "_"
			    + a.getTimestamp(), a.getPreCondition(), eAction);
		    owner.addRule(r);

		    rules.add(r);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void setupGoal(JFrame parrentGui) {
	if (concepts != null)
	    if (concepts.size() > 0) {
		if (gui == null)
		    gui = new GUIGoal(this, owner.getNamespace(), parrentGui);

		gui.showUp();
	    }
    }

    public void start() {
	start(null);
    }

    public void start(Object input) {
	try {
	    if (status == GoalStatus.Started)
		return;

	    if (status == GoalStatus.SearchingKnowledge && this.plan == null)
		return;

	    if (this.plan != null) {
		if (status == GoalStatus.SearchingKnowledge)
		    plan.defineVariable("startData", startData);
		else
		    plan.defineVariable("startData", input);

		owner.addActionPlan(plan);
		status = GoalStatus.Started;

		plan.defineVariable("goalSchema", this.ontology);
		plan.defineVariable("owner", this.owner.getName());

		Vector action = new Vector();
		action.add(new ExecuteProcessAction(plan.getName()));
		owner.getExecutionComponent().put(action);

		setupActions(plan);

		if (input != null) {
		    owner.getDecisionComponent().put(input);
		}
	    }
	} catch (Exception e) {
	    System.err.println("[ E ] Erro iniciando Objetivo : "
		    + e.getMessage());
	}
    }

    public OntModel getOntology() {
	return this.ontology;
    }

    public void restart() {
	OWLUtil.copyOntModelContent(base, ontology);

	Iterator<Rule> iter = rules.iterator();
	while (iter.hasNext())
	    owner.getDecisionComponent().removeRule(iter.next().getName());

	rules.clear();

	if (plan != null)
	    owner.getExecutionComponent().restartActionPlan(plan.getName());

	status = GoalStatus.Stopped;
    }

    public synchronized String getID() {
	return id;
    }

    public synchronized String getDescription() {
	return description;
    }

    public synchronized String getName() {
	return name;
    }

    public synchronized void setDescription(String description) {
	this.description = description;
    }

    public synchronized void setName(String name) {
	this.name = name;
    }

    public ActionPlan getActionPlan() {
	return plan;
    }

    @Override
    public String toString() {
	String toString = "";
	try {
	    toString = id + " Name: " + name + " Description: " + description
		    + " Status: " + status;
	} catch (Exception e) {
	    toString = "";
	}

	return toString;
    }

    public long getTimestamp() {
	return timestamp;
    }

    private String getQuestions(Fact f) {
	LinkedList<String> questions = new LinkedList<String>();

	if (f != null) {
	    try {
		String fact = f.canonicalForm();

		int cont = 0;
		int off = 0;

		while (off < fact.length()) {
		    int idL = off = fact.indexOf("?", off);
		    int idH = fact.indexOf(" ", idL);

		    if (idL > 0 && idH > 0) {
			String q = fact.substring(idL, idH);
			questions.add(q);
		    }

		    if (off == -1)
			break;

		    off = idH + 1;
		    cont++;
		}

		String q = questions.toString().replace("[", "");
		q = q.toString().replace("]", "");

		return q;
	    } catch (Exception e) {

	    }
	}

	return null;
    }
}
