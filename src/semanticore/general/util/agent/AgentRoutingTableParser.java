package semanticore.general.util.agent;

import semanticore.domain.model.AgentRoutingTable;
import semanticore.domain.model.SemanticAgent;

public class AgentRoutingTableParser {
    public AgentRoutingTable parse(SemanticAgent agent) {
	return parse(agent, null);
    }

    public AgentRoutingTable parse(SemanticAgent agent, String domainPartName) {
	if (agent != null) {
	    if (domainPartName != null)
		return new AgentRoutingTable(agent, domainPartName);
	    else
		return new AgentRoutingTable(agent);
	}
	return null;
    }
}
