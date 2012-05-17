package semanticore.agent.effector;

import java.io.Serializable;

import semanticore.domain.model.SemanticMessage;
import semanticore.general.util.OWLUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class Effector implements Serializable {
    protected String name;
    protected EffectorComponent effectorComponent;

    public Effector(String name, EffectorComponent owner) {
	this.name = name;
	effectorComponent = owner;
    }

    public abstract void publish(Object assertion);

    protected boolean publish(SemanticMessage message) {
	try {
	    return effectorComponent.getOwner().sendMessage(message);
	} catch (Exception e) {
	    System.err.println("[ E ] Effector : erro ao enviar mensagem [ "
		    + e.getMessage() + " ]");
	    e.printStackTrace();
	    return false;
	}
    }

    public abstract void publish(Object[] data) throws Exception;

    protected OntModel createOntoMessage(String from, String to,
	    OntModel content) {
	String NS = this.effectorComponent.getOwner().getNamespace();
	OntModel messageSchema = (OntModel) this.effectorComponent.getOwner()
		.getMessageSchema();

	OntModel messageData = ModelFactory.createOntologyModel();
	messageData.createIndividual(NS + "message1",
		messageSchema.getResource(NS + "Message"));

	messageData.add((Statement) messageData.createStatement(
		(Resource) messageData.getResource(NS + "message1"),
		messageSchema.getProperty(NS + "from"), from));
	messageData.add((Statement) messageData.createStatement(
		(Resource) messageData.getResource(NS + "message1"),
		messageSchema.getProperty(NS + "to"), to));

	String ontoToXML = OWLUtil.parseModelToXML(content);
	messageData.add((Statement) messageData.createStatement(
		(Resource) messageData.getResource(NS + "message1"),
		messageSchema.getProperty(NS + "content"),
		messageData.createTypedLiteral(ontoToXML)));
	return messageData;
    }

    public String getName() {
	return this.name;
    }
}
