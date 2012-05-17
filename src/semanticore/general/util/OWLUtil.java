package semanticore.general.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.Vector;

import semanticore.agent.kernel.information.ComposedFact;
import semanticore.agent.kernel.information.Fact;
import semanticore.agent.kernel.information.FunctionBasedFact;
import semanticore.agent.kernel.information.Rule;
import semanticore.agent.kernel.information.SimpleFact;
import semanticore.domain.SemantiCore;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

final public class OWLUtil {

    private static String include1 = "@include <OWL>.";

    public static String parseOWL(Fact fact) {
	return "";
    }

    public static void parseJenaRules(Rule rule, String filename) {
	try {
	    SemantiCore.notification.print("> OWLUtil : parsing...");

	    String buffer = include1 + "\n\n";

	    buffer += parseJenaRules(rule);

	    writeFile(buffer, filename);

	    SemantiCore.notification.print("> OWLUtil : finish...");
	} catch (Exception e) {
	    SemantiCore.notification.print("[ E ] > OWLUtil (parseJenaRules)");
	    e.printStackTrace();
	}
    }

    public static void copyOntModelContent(OntModel base, OntModel target) {
	try {
	    String oBase = parseOntModelToXML(base);
	    target = parseXMLToOntModel(oBase);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static boolean createStatement(OntModel model, String subject,
	    String predicate, String object) {
	model.createClass(subject);

	if (predicate.equals(RDF.type.toString())) {
	    model.add(model.createStatement(model.createResource(subject),
		    model.getProperty(predicate), object));
	    return true;
	} else {
	    model.add(model.createStatement(model.createResource(subject),
		    model.getProperty(predicate),
		    model.createTypedLiteral(object)));
	    return true;
	}
    }

    public static void parseJenaRules(Rule[] rules, String filename) {
	try {
	    SemantiCore.notification.print("> OWLUtil : parsing...");

	    String buffer = include1 + "\n\n";

	    for (int i = 0; i < rules.length; i++) {
		buffer += parseJenaRules(rules[i]) + "\n\n\n";
	    }

	    writeFile(buffer, filename);

	    SemantiCore.notification.print("> OWLUtil : finish...");
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] > OWLUtil (parseJenaRules'2)");
	}
    }

    public static List parseJenaRules(Rule[] rules) {
	try {
	    String buffer = new String();

	    for (int i = 0; i < rules.length; i++)
		buffer += parseJenaRules(rules[i]) + "\n\n\n";

	    return com.hp.hpl.jena.reasoner.rulesys.Rule.parseRules(buffer);
	} catch (Exception e) {
	    return null;
	}

    }

    // inner
    public static String parseJenaRules(Rule rule) {
	String finalRule = new String();

	finalRule = "[";

	finalRule += rule.getName() + ":\n";

	finalRule += factHelper(rule.getFact());

	finalRule += "\n\t-> " + factHelper(rule.getConsequence()) + "]";

	return finalRule;
    }

    private static String factHelper(Fact fact) {
	String teste = new String();

	if (fact instanceof ComposedFact) {
	    String term1 = factHelper(((ComposedFact) fact).getTerm1());
	    String term2 = factHelper(((ComposedFact) fact).getTerm2());

	    switch (((ComposedFact) fact).getOperator()) {
	    case AND:
		teste += term1 + "\n" + term2;// "(" + term1 + " AND "
		// + term2 + ")";
		break;
	    case OR:

		teste += term1 + "\n" + term2;// teste += "(" + term1
		// + " OR " + term2 +
		// ")";
		break;
	    }
	} else {
	    if (fact instanceof SimpleFact) {
		teste += "\t(" + ((SimpleFact) fact).getSubject() + " ";
		teste += ((SimpleFact) fact).getPredicate() + " ";
		teste += ((SimpleFact) fact).getObject() + ")";
	    } else if (fact instanceof FunctionBasedFact) {
		String[] arguments = ((FunctionBasedFact) fact).getAgruments();
		String arg = "(";

		int i = 0;
		for (i = 0; i < (arguments.length - 1); i++)
		    arg += arguments[i] + ", ";

		arg += arguments[i] + ")";

		teste += "\t" + ((FunctionBasedFact) fact).getFunctionName()
			+ arg;
	    }

	    return teste;
	}

	return teste;
    }

    private static void writeFile(String content, String filename) {
	try {
	    File file = new File(filename);

	    boolean success = file.createNewFile();

	    FileWriter fw = new FileWriter(filename);

	    BufferedWriter bw = new BufferedWriter(fw);

	    bw.write(content);

	    bw.flush();

	    bw.close();

	    fw.close();
	} catch (IOException e) {
	}
    }

    public static boolean toJenaIndividual(SimpleFact simpleFact,
	    OntModel baseSchema, OntModel sourceSchema) {
	try {
	    baseSchema.createIndividual(simpleFact.getSubject(),
		    sourceSchema.getResource(simpleFact.getObject()));
	    return true;
	} catch (Exception e) {

	    return false;
	}
    }

    public static String parseModelToXML(Model model) {
	try {
	    StringWriter content = new StringWriter();

	    model.write(content);

	    return content.toString();
	} catch (Exception e) {
	    e.printStackTrace();

	    return null;
	}
    }

    public static Model parseXMLToModel(String XMLModel) {
	try {
	    StringReader content = new StringReader(XMLModel);

	    Model model = ModelFactory.createDefaultModel();
	    model.read(content, null);

	    return model;
	} catch (Exception e) {
	    e.printStackTrace();

	    return null;
	}
    }

    public static OntModel ontModelFromFile(URI file) {
	try {
	    File f = new File(file);
	    RandomAccessFile raf = new RandomAccessFile(f, "r");

	    return readOntModel(raf);
	} catch (Exception ef) {
	    System.err.println("[ E ] Error loading ontology : "
		    + ef.getMessage());
	    return null;
	}
    }

    public static OntModel ontModelFromFile(String path) {
	//
	// =========================================================================================
	try {
	    RandomAccessFile raf = new RandomAccessFile(path, "r");

	    return readOntModel(raf);
	} catch (Exception ef) {
	    System.err.println("[ E ] Error loading ontology : "
		    + ef.getMessage());
	    return null;
	}
    }

    private static OntModel readOntModel(RandomAccessFile raf) throws Exception {
	String line;
	String xml = new String();

	while ((line = raf.readLine()) != null)
	    xml += line + "\n";

	raf.close();

	OntModel model = OWLUtil.parseXMLToOntModel(xml);

	return model;
    }

    public static OntModel parseXMLToOntModel(String XMLModel) throws Exception {
	StringReader content = new StringReader(XMLModel);

	OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
		null);
	model.read(content, null);

	return model;
    }

    public static Vector<String> getStatementsString(Model m, String namespace,
	    String property) {
	Resource nula = null;
	Vector<String> result = new Vector<String>(1, 1);

	try {
	    StmtIterator iter = m.listStatements(nula,
		    m.getProperty(namespace + property), nula);

	    while (iter.hasNext()) {
		Statement s = iter.nextStatement();
		result.add(s.getString());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return result;
    }

    public static String parseOntModelToXML(OntModel model) {
	try {
	    StringWriter content = new StringWriter();

	    model.write(content);

	    return content.toString();
	} catch (Exception e) {
	    e.printStackTrace();

	    return null;
	}
    }

    public static Literal searchLiteral(Model m, Resource s, Property p,
	    Resource o) {
	Literal object = null;
	for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
	    Statement stmt = i.nextStatement();
	    object = stmt.getLiteral();
	}
	return object;
    }
}
