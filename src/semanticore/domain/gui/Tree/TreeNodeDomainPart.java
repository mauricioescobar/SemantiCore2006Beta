package semanticore.domain.gui.Tree;

import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeDomainPart extends DefaultMutableTreeNode {

    public String name;

    public Hashtable<String, TreeNodeAgent> agents = new Hashtable<String, TreeNodeAgent>();

    public TreeNodeDomainPart(String domainPartName) {
	super(domainPartName);

	this.name = domainPartName;
    }

    public boolean addAgent(TreeNodeAgent agent) {
	if (agents.containsKey(agent.name))
	    return false;
	else {
	    agents.put(agent.name, agent);
	    return true;
	}
    }

    public TreeNodeAgent removeAgent(String agentName) {
	return agents.remove(agentName);
    }
}
