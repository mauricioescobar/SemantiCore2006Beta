package semanticore.domain.gui.Tree;

import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeRoot extends DefaultMutableTreeNode {

    public String name;

    public Hashtable<String, TreeNodeDomain> domains = new Hashtable<String, TreeNodeDomain>();

    public TreeNodeRoot(String rootName) {
	super(rootName);

	this.name = rootName;
    }

    public void addDomain(TreeNodeDomain domain) {
	this.domains.put(domain.name, domain);
    }

    public TreeNodeDomain removeDomain(String domainName) {
	return domains.remove(domainName);
    }

    public TreeNodeDomain getDomain(String domainName) {
	return domains.get(domainName);
    }
}
