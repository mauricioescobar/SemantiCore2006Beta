package semanticore.domain.model;

import java.io.IOException;
import java.io.Serializable;

import semanticore.general.util.OWLUtil;

import com.hp.hpl.jena.ontology.OntModel;

public class OntologyModel implements Serializable {
    public OntModel model;

    public OntologyModel(OntModel model) {
	this.model = model;
    }

    public OntModel getModel() {
	return model;
    }

    public void setOntModel(OntModel model) {
	this.model = model;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	String xml = OWLUtil.parseOntModelToXML(model);
	out.writeObject(xml);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
	    ClassNotFoundException {
	try {
	    Object o = in.readObject();
	    if (o instanceof String) {
		String xml = (String) o;
		model = OWLUtil.parseXMLToOntModel(xml);
	    }
	} catch (Exception e) {
	    System.err.println("[ OntologyModel ] erro reading ontModel : "
		    + e.getMessage());
	}
    }
}
