package semanticore.domain.gui.Tree;

import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeDomain extends DefaultMutableTreeNode {

    public String name;

    public Hashtable<String, TreeNodeDomainPart> parts = new Hashtable<String, TreeNodeDomainPart>();

    public TreeNodeDomain(String name) {
	super(name);

	this.name = name;
    }

    public void addDomainPart(TreeNodeDomainPart domainPart) {
	parts.put(domainPart.name, domainPart);
    }

    public TreeNodeDomainPart removeDomainPart(String domainPartName) {
	return parts.remove(domainPartName);
    }

    public TreeNodeDomainPart getDomainPart(String domainPartName) {
	return parts.get(domainPartName);
    }
}
