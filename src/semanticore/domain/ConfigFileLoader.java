package semanticore.domain;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import semanticore.domain.control.Domain;

public class ConfigFileLoader extends DefaultHandler {
    private Hashtable<String, Method> atributos = new Hashtable<String, Method>();

    private static int id = 0;

    public ConfigFileLoader(String fileName) {
	initialize();

	DefaultHandler handler = this;

	SAXParserFactory factory = SAXParserFactory.newInstance();

	try {
	    SAXParser saxParser = factory.newSAXParser();
	    saxParser.parse(fileName, handler);
	} catch (Throwable t) {
	    t.printStackTrace();
	}
    }

    private void initialize() {
	try {

	    Method m1 = getClass().getMethod("semanticoreTag", String.class,
		    String.class, String.class);
	    Method m2 = getClass().getMethod("agentTag", String.class,
		    String.class, String.class);
	    Method m3 = getClass().getMethod("executionengineTag",
		    String.class, String.class, String.class);
	    Method m4 = getClass().getMethod("effectorengineTag", String.class,
		    String.class, String.class);
	    Method m5 = getClass().getMethod("decisionengineTag", String.class,
		    String.class, String.class);

	    atributos.put("semanticore", m1);
	    atributos.put("agent", m2);
	    atributos.put("executionengine", m3);
	    atributos.put("effectorengine", m4);
	    atributos.put("decisionengine", m5);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void semanticoreTag(String id, String atributo, String valor) {
	if (atributo.equals("showgui")) {
	    if (valor.equals("false"))
		SemantiCoreDefinitions.showSemanticoreGui = false;
	} else if (atributo.equals("showintro")) {
	    if (valor.equals("false"))
		SemantiCoreDefinitions.showIntro = false;
	} else if (atributo.equals("console")) {
	    if (valor.equals("true"))
		SemantiCoreDefinitions.notificationEnabled = true;
	}
    }

    public void agentTag(String id, String atributo, String valor) {
	Hashtable<String, String> atributos = SemantiCoreDefinitions.agentes
		.get(id);

	if (atributos != null)
	    atributos.put(atributo, valor);
	else {
	    atributos = new Hashtable<String, String>();
	    atributos.put(atributo, valor);
	    SemantiCoreDefinitions.agentes.put(id, atributos);
	}
    }

    public void executionengineTag(String id, String atributo, String valor) {
	SemantiCoreDefinitions.EXECUTION_ENGINE_HOTSPOT = valor;
    }

    public void effectorengineTag(String id, String atributo, String valor) {
	SemantiCoreDefinitions.EFFECTOR_ENGINE_HOTSPOT = valor;
    }

    public void decisionengineTag(String id, String atributo, String valor) {
	SemantiCoreDefinitions.DECISION_ENGINE_HOTSPOT = valor;
    }

    public void startElement(String namespaceURI, String lName, // local
	    String qName, Attributes attrs) throws SAXException {
	id++;

	String eName = lName;

	if ("".equals(eName))
	    eName = qName;

	for (int i = 0; i < attrs.getLength(); i++) {
	    if (attrs.getLocalName(i) != null) {

		Method m = atributos.get(eName);

		if (m != null)
		    try {
			m.invoke(this, new Object[] { Integer.toString(id),
				attrs.getQName(i), attrs.getValue(i) });
		    } catch (Exception e) {
			e.printStackTrace();
		    }
	    }
	}

	if (eName.equals("databridge")) {
	    if (attrs.getLocalName(0) != null
		    && attrs.getQName(0).equals("type"))
		SemantiCoreDefinitions.DATA_BRIDGE_HOTSPOT_TYPES.add(attrs
			.getValue(0));
	    else
		throw new SAXException(
			"SemantiCore | ConfigLoader | DataBridge hotspot not declared.");

	    if (attrs.getLocalName(1) != null
		    && attrs.getQName(1).equals("class"))
		SemantiCoreDefinitions.DATA_BRIDGE_HOTSPOT.add(attrs
			.getValue(1));
	    else
		throw new SAXException(
			"SemantiCore | ConfigLoader | DataBridge hotspot not declared.");
	} else if (eName.equals("domain")) {
	    String domainName = new String();
	    String address = new String();
	    String port = new String();

	    if (attrs.getLocalName(0) != null
		    && attrs.getQName(0).equals("name"))
		domainName = attrs.getValue(0);
	    else
		throw new SAXException(
			"SemantiCore | ConfigLoader | Domain.name");

	    if (attrs.getLocalName(1) != null
		    && attrs.getQName(1).equals("address"))
		address = attrs.getValue(1);
	    else
		throw new SAXException(
			"SemantiCore | ConfigLoader | Domain.address");

	    if (attrs.getLocalName(2) != null
		    && attrs.getQName(2).equals("port"))
		port = attrs.getValue(2);
	    else
		throw new SAXException(
			"SemantiCore | ConfigLoader | Domain.port");

	    try {
		if (address.equalsIgnoreCase("localhost"))
		    address = InetAddress.getLocalHost().getHostAddress();
		else {
		    InetAddress inetAddress;
		    inetAddress = InetAddress.getByName(address);
		    address = inetAddress.getHostAddress();
		}
	    } catch (UnknownHostException e) {
		e.printStackTrace();
	    }
	    if (SemantiCoreDefinitions.DOMAINS.add(new Domain(address, port,
		    domainName))) {
		SemantiCore.notification.print("[ I ] SemantiCore :");
		SemantiCore.notification.print("\tDomain definitions for "
			+ domainName + " was loaded");
	    }
	}

    }

    public void endElement(String namespaceURI, String sName, // simple name
	    String qName // qualified name
    ) throws SAXException {

    }

    public void characters(char buf[], int offset, int len) throws SAXException {

    }

    // ===========================================================
    // SAX DocumentHandler methods
    // ==========================================================
    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {

    }

}
