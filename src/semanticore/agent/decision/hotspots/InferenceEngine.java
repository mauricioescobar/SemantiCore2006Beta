package semanticore.agent.decision.hotspots;

import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import semanticore.agent.decision.DecisionEngine;
import semanticore.agent.kernel.information.ComposedFact;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.FunctionBasedFact;
import semanticore.agent.kernel.information.Rule;
import semanticore.agent.kernel.information.SimpleFact;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.OntologyModel;
import semanticore.domain.model.SemanticMessage;
import semanticore.general.util.OWLUtil;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;

public class InferenceEngine extends DecisionEngine {

    private transient BuiltinRegistry theRegistry = new BuiltinRegistry().theRegistry;
    private OntologyModel schema = null;
    private OntologyModel dataModel = null;
    private InfModel infModel;

    private String canonicalRules = "";

    private Vector result = new Vector(1, 1);

    private Vector input = new Vector(1, 1);

    private OntologyModel domainModel;

    public InferenceEngine(URI schemaURI) {
	try {
	    initialize(schemaURI, null);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public InferenceEngine(OntModel schema) {
	try {
	    initialize();
	    domainModel.model.add(schema);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public InferenceEngine() {
	try {
	    initialize(null, null);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public InferenceEngine(URI schemaURI, URI dataURI) {
	try {
	    initialize(schemaURI, dataURI);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void initialize(URI schemaURI, URI dataURI) {
	try {
	    initialize();

	    if (schemaURI != null) {
		schema = new OntologyModel(ModelFactory.createOntologyModel());
		schema.model = OWLUtil.ontModelFromFile(schemaURI);
		domainModel.model.add(schema.model);
	    }

	    if (dataURI != null) {
		dataModel = new OntologyModel(OWLUtil.ontModelFromFile(dataURI));
		domainModel.model.add(dataModel.model);
	    } else {
		if (dataModel == null)
		    dataModel = new OntologyModel(
			    ModelFactory.createOntologyModel());
		else
		    dataModel.model = ModelFactory.createOntologyModel();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void registerFunction(FunctionBasedFact function) {
	try {
	    theRegistry.register(new BaseBuiltAction(
		    (FunctionBasedFact) function, input, result));
	    SemantiCore.notification.print("[I] Inference Engine");
	    SemantiCore.notification.print("\tFunction registered : "
		    + function.getFunctionName());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void initialize() {
	try {
	    OntModel mTemp = ModelFactory.createOntologyModel();
	    String file = getClass().getClassLoader()
		    .getResource("ontologies/semanticAgent.owl").toString();
	    mTemp.read(file);

	    domainModel = new OntologyModel(mTemp);
	    OntModel eventsModel = ModelFactory.createOntologyModel();

	    file = getClass().getClassLoader()
		    .getResource("ontologies/Events.owl").toString();
	    eventsModel.read(file);
	    domainModel.model.add(eventsModel);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void updateRules(Rule rule) {
	canonicalRules = "";

	if (rule.getConsequence() instanceof ComposedFact) {
	    registerFunctions(rule.getConsequence());
	} else if (rule.getConsequence() instanceof FunctionBasedFact)
	    registerFunction((FunctionBasedFact) rule.getConsequence());

	Enumeration<Rule> iter = getRules().elements();
	while (iter.hasMoreElements()) {
	    canonicalRules += OWLUtil.parseJenaRules(iter.nextElement()) + "\n";
	}

	SemantiCore.notification.print("==============\n" + canonicalRules);
    }

    private void registerFunctions(Fact f) {
	if (f instanceof ComposedFact) {
	    registerFunctions(((ComposedFact) f).getTerm1());
	    registerFunctions(((ComposedFact) f).getTerm2());
	} else {
	    if (f instanceof FunctionBasedFact)
		registerFunction((FunctionBasedFact) f);
	}
    }

    private void updateRules() {
	canonicalRules = "";

	Enumeration<Rule> iter = getRules().elements();
	while (iter.hasMoreElements()) {
	    canonicalRules += OWLUtil.parseJenaRules(iter.nextElement()) + "\n";
	}
    }

    private void inference() {
	try {
	    List rules1 = com.hp.hpl.jena.reasoner.rulesys.Rule
		    .parseRules(canonicalRules);
	    Reasoner reasoner = new GenericRuleReasoner(rules1);
	    // reasoner = reasoner.bindSchema ( schema );
	    reasoner = reasoner.bindSchema(domainModel.getModel());

	    // if ( infModel == null )
	    // infModel = ModelFactory.createInfModel ( reasoner, dataModel );
	    infModel = ModelFactory.createInfModel(reasoner,
		    domainModel.getModel());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    protected void addRule(Rule rule) {
	super.addRule(rule);

	updateRules(rule);
    }

    @Override
    protected Rule removeRule(String name) {
	Rule r = super.removeRule(name);

	updateRules();

	return r;
    }

    public void printInfmodel() {
	try {
	    infModel.write(System.out);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public Vector decide(Object facts) {
	try {
	    result.removeAllElements();
	    input.removeAllElements();

	    input.add(facts);

	    if (facts instanceof SemanticMessage) {
		Object content = ((SemanticMessage) facts).getContent();
		evaluateFacts(content);
	    } else
		evaluateFacts(facts);

	    inference();

	    return result;
	} catch (Exception e) {
	    e.printStackTrace();

	    return new Vector();
	}
    }

    protected void createIndividual(SimpleFact fact) {
	try {
	    Individual iFact = domainModel.getModel().createIndividual(
		    fact.getSubject(),
		    domainModel.getModel().getResource(fact.getObject()));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    protected void evaluateFacts(Object info) {
	if (info instanceof SimpleFact) {
	    SimpleFact simpleFact = (SimpleFact) info;
	    createIndividual(simpleFact);
	} else if (info instanceof String) {
	    try {
		OntModel modelFacts = OWLUtil.parseXMLToOntModel((String) info);
		domainModel.model.add(modelFacts);
	    } catch (Exception e) {
		System.err
			.println("[ E ] InferenceEngine - Erro ao converter conteudo em OntModel");
	    }
	} else if (info instanceof OntModel) {
	    domainModel.model.add((OntModel) info);
	}
    }
}
