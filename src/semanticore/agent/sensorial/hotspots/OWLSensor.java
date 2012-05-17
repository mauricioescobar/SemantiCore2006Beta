package semanticore.agent.sensorial.hotspots;

import java.io.StringReader;

import semanticore.agent.kernel.information.ComposedFact;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.SimpleFact;
import semanticore.agent.sensorial.Sensor;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.SemanticMessage;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class OWLSensor extends Sensor {

    public OWLSensor(String sName, Fact headerPattern, Fact contentPattern) {
	super(sName);

	this.headerPattern = headerPattern;
	this.contentPattern = contentPattern;
    }

    public Object evaluate(Object source) {
	if (source instanceof SemanticMessage) {
	    SemanticMessage message = (SemanticMessage) source;

	    if (checkHeaderPattern(message, headerPattern)) {
		SemantiCore.notification
			.print("> OWLSensor : Header's pattern found");

		Object content = message.getContent();

		if (content.equals(null)) {
		    SemantiCore.notification
			    .print("> OWLSensor : Message content is empty");
		} else {
		    OntModel messageContent = ModelFactory
			    .createOntologyModel();

		    StringReader contentReader;

		    if (content instanceof String) {
			contentReader = new StringReader((String) content);

			messageContent.read(contentReader, null);

			if (checkContentPattern(messageContent, contentPattern)) {
			    SemantiCore.notification
				    .print("> OWLSensor : Content's pattern found");
			    return message; // envia para o decisorio mensagem
			} else {
			    SemantiCore.notification
				    .print("> OWLSensor : Content's pattern not found");
			}
		    }
		}
	    } else {
		SemantiCore.notification
			.print("> OWLSensor : Header's pattern not found");
	    }
	}

	return null;
    }

    private boolean checkHeaderPattern(SemanticMessage message, Fact fact) {
	try {
	    if (fact == null) {
		return true;
	    } else {
		return factHeaderHelper(fact, message);
	    }
	} catch (Exception e) {
	    System.err.println("\n[ E ] checkHeaderPattern Exception : "
		    + e.getMessage());

	    return false;
	}
    }

    private boolean checkContentPattern(OntModel messageReceived, Fact fact) {
	try {
	    if (fact == null) {
		return true;
	    } else {
		return factHelper(fact, messageReceived);
	    }
	} catch (Exception e) {
	    System.err.println("\n[ E ] checkContentPattern Exception : "
		    + e.getMessage());

	    return false;
	}
    }

    private String searchLiteral(Model m, Resource s, Property p, Resource o) {
	String object = "null";

	for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
	    Statement stmt = i.nextStatement();
	    object = stmt.getLiteral().getString();
	}

	return object;
    }

    private boolean factHelper(Fact fact, OntModel model) {
	if (fact instanceof ComposedFact) {
	    boolean term1 = factHelper(((ComposedFact) fact).getTerm1(), model);

	    if (!term1)
		return false;

	    boolean term2 = factHelper(((ComposedFact) fact).getTerm2(), model);

	    if (!term2)
		return false;

	    switch (((ComposedFact) fact).getOperator()) {
	    case AND:
		break;
	    case OR:
		break;
	    }
	} else {
	    SimpleFact sf = (SimpleFact) fact;

	    Resource subject = null;
	    Property predicate = null;
	    Object object = null;

	    boolean o = false;

	    if (sf.getSubject() == null) {
		subject = null;
	    } else if (!sf.getSubject().contains("RDF.")
		    && !sf.getSubject().contains("OWL.")) {
		subject = model.getResource(sf.getSubject());
	    } else {
		if (sf.getSubject().contains("RDF."))
		    subject = RDF.type;
	    }

	    if (!sf.getPredicate().contains("RDF.")
		    && !sf.getPredicate().contains("OWL.")) {
		predicate = model.getProperty(sf.getPredicate());
	    } else {
		if (sf.getSubject().contains("RDF."))
		    predicate = RDF.type;
	    }

	    if (!sf.getObject().contains("RDF.")
		    && !sf.getObject().contains("OWL.")) {
		if (sf.getObject().contains(SemantiCore.NAMESPACE))
		    object = model.getResource(sf.getObject());
		else {
		    object = sf.getObject();
		}
	    }

	    if (model.contains(subject, predicate, object)) {
		return true;
	    }
	}
	return false;
    }

    private boolean factHeaderHelper(Fact fact, SemanticMessage message) {
	if (fact instanceof ComposedFact) {
	    boolean term1 = factHeaderHelper(((ComposedFact) fact).getTerm1(),
		    message);

	    if (!term1)
		return false;

	    boolean term2 = factHeaderHelper(((ComposedFact) fact).getTerm2(),
		    message);

	    if (!term2)
		return false;

	    switch (((ComposedFact) fact).getOperator()) {
	    case AND:
		break;
	    case OR:
		break;
	    }
	} else {
	    SimpleFact sf = (SimpleFact) fact;

	    if (sf.getPredicate().equalsIgnoreCase("to")) {
		if (message.getTo() != null) {
		    for (int i = 0; i < message.getTo().length; i++) {
			if (sf.getObject().equals(message.getTo()[i]))
			    return true;
		    }
		}
	    } else if (sf.getPredicate().equalsIgnoreCase("from")) {
		if (sf.getObject().equals(message.getFrom()))
		    return true;
	    }
	}

	return false;
    }
}
