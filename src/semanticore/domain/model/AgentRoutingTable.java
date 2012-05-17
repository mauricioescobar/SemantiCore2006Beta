package semanticore.domain.model;

import java.io.Serializable;

import semanticore.general.util.agent.AgentRoutingTableParser;

public class AgentRoutingTable implements Serializable {
    private String key;

    private String agentName;

    private String domainName;

    private String domainPartName;

    private String component;

    public static AgentRoutingTableParser parser = new AgentRoutingTableParser();

    public AgentRoutingTable(SemanticAgent agent) {
	this(agent.getName(), "sensorial", agent.getDomain(), "");
    }

    public AgentRoutingTable(SemanticAgent agent, String domainPartName) {
	this(agent.getName(), "sensorial", agent.getDomain(), domainPartName);
    }

    private AgentRoutingTable(String agentName, String component,
	    String domainName, String domainPartName) {
	this.agentName = agentName;
	this.key = agentName + "." + component + "@" + domainPartName + "."
		+ domainName;
	this.domainName = domainName;
	this.domainPartName = domainPartName;
	this.component = component;
    }

    public String getDomainName() {
	return domainName;
    }

    public String getKey() {
	return key;
    }

    public String getDomainPartName() {
	return domainPartName;
    }

    public String getName() {
	return agentName;
    }

    public void setName(String name) {
	this.agentName = name;
    }

    public String getComponent() {
	return component;
    }

}
