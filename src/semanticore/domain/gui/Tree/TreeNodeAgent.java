package semanticore.domain.gui.Tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeAgent extends DefaultMutableTreeNode {

    public String name;

    public TreeNodeAgent(String agentName) {
	super(agentName);
	this.name = agentName;
    }
}
