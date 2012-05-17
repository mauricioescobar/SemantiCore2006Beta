package semanticore.domain.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.control.Domain;
import semanticore.domain.control.DomainPart;
import semanticore.domain.gui.Tree.TreeNodeAgent;
import semanticore.domain.gui.Tree.TreeNodeDomain;
import semanticore.domain.gui.Tree.TreeNodeDomainPart;
import semanticore.domain.gui.Tree.TreeNodeRoot;
import semanticore.domain.model.AgentRoutingTable;
import semanticore.domain.model.SemanticAgent;
import semanticore.domain.model.SemanticMessage;

public class GUISemantiCore extends Observable implements ActionListener,
	WindowListener, Observer, Runnable {

    private JFrame frame = new JFrame() {
	private Image logo = this.getToolkit().getImage(
		getClass().getClassLoader().getResource(
			"semanticore/general/images/logo_p_horizontal.jpg"));

	@Override
	public void paint(Graphics g) {
	    super.paint(g);

	    g.drawImage(logo, 315, 55, this);
	}
    };

    private JButton bt1 = new JButton("External Domains");

    private JButton bt2 = new JButton("Agents");

    private JButton bt3 = new JButton("Send a Message");

    private JButton bt4 = new JButton("Domain parts");

    private Environment environment;

    // ------------------------------------------------------------
    private DefaultTreeModel treeModel;

    private JTree tree;

    private TreeNodeRoot root;

    private Hashtable<DefaultMutableTreeNode, LinkedList<DefaultMutableTreeNode>> tableDomainNodes = new Hashtable<DefaultMutableTreeNode, LinkedList<DefaultMutableTreeNode>>();

    // --------------------------------------------------------------------------------------
    private JMenu fileMenu = new JMenu("File");

    private JMenuItem shutDownPlataform = new JMenuItem("Shutdown SemantiCore");

    private JMenu actionsMenu = new JMenu("Actions");

    private JMenuItem actionSendMessage = new JMenuItem("Send Message");

    private JMenu domainMenu = new JMenu("Environment");

    private JMenuItem showRoutingTable = new JMenuItem("Routing Table (Agent)");

    private JMenuItem showRoutingTableDomain = new JMenuItem(
	    "Routing Table (Domain)");

    private JMenuItem showRoutingTableInterDomain = new JMenuItem(
	    "Routing Table (InterDomain)");

    private JMenu menuAjuda = new JMenu("Help");

    private JMenuItem subMenuAbout = new JMenuItem("About SemantiCore");

    private JPopupMenu popUpAgentMenu = new JPopupMenu();

    private JMenuItem agetMenuItemGoal = new JMenuItem("Goals");

    // --------------------------------------------------------------------------------------

    public static final int ADD_DOMAIN_PART = 0;

    public static final int ADD_DOMAIN = 1;

    public static final int ADD_AGENT = 2;

    public static final int REMOVE_DOMAIN_PART = 3;

    public static final int REMOVE_DOMAIN = 4;

    public static final int REMOVE_AGENT = 5;

    private FrameAgents fa = null;

    private String selecionPath = null;

    private Font courierNew_15 = new Font("Courier New", Font.PLAIN, 16);

    public GUISemantiCore(Environment env) {

	this.environment = env;

	JScrollPane scrollTree;

	// Tree ---------------------------------------------------------
	// root = new DefaultMutableTreeNode ( "SemantiCore" );
	root = new TreeNodeRoot("SemantiCore");
	treeModel = new DefaultTreeModel(root);
	tree = new JTree(treeModel);
	tree.setEditable(false);
	tree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.setShowsRootHandles(true);
	tree.setFont(courierNew_15);
	scrollTree = new JScrollPane(tree);

	// MENU ------------------------
	JMenuBar menuBar = new JMenuBar();

	fileMenu.add(shutDownPlataform);
	fileMenu.setMnemonic('F');

	actionsMenu.add(actionSendMessage);
	actionsMenu.setMnemonic('A');

	domainMenu.setMnemonic('E');
	domainMenu.add(showRoutingTable);
	domainMenu.add(showRoutingTableDomain);
	domainMenu.add(showRoutingTableInterDomain);

	menuAjuda.add(subMenuAbout);
	menuAjuda.setMnemonic('H');

	menuBar.add(fileMenu);
	menuBar.add(actionsMenu);
	menuBar.add(domainMenu);
	menuBar.add(menuAjuda);

	popUpAgentMenu.add(agetMenuItemGoal);
	// ------------------------------

	// Bounds -----------------------------------------------------
	bt1.setBounds(10, 10, 200, 40);
	bt2.setBounds(10, 60, 200, 40);
	bt3.setBounds(10, 110, 200, 40);
	bt4.setBounds(10, 160, 200, 40);

	scrollTree.setBounds(10, 60, 495, 305);

	// Action Listener ---------------------------------------------
	actionSendMessage.addActionListener(this);
	shutDownPlataform.addActionListener(this);
	showRoutingTable.addActionListener(this);
	showRoutingTableDomain.addActionListener(this);
	showRoutingTableInterDomain.addActionListener(this);
	subMenuAbout.addActionListener(this);
	agetMenuItemGoal.addActionListener(this);

	// Frame -------------------------------------------------------
	frame.setResizable(false);
	frame.setJMenuBar(menuBar);
	frame.getContentPane().add(scrollTree);
	frame.setLayout(null);
	frame.getContentPane().setBackground(Color.WHITE);

	frame.setSize(520, 430);
	frame.setLocationRelativeTo(null);

	MouseListener ml = new MouseAdapter() {
	    public void mousePressed(MouseEvent e) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());

		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

		if (selRow != -1) {
		    selecionPath = null;

		    SemanticAgent selected = environment
			    .getLocalAgentByName(selPath.getLastPathComponent()
				    .toString());

		    if (selected != null && selected instanceof IMouseEvent)
			((IMouseEvent) selected).mousePressed(e);

		    if (SwingUtilities.isRightMouseButton(e)) {
			if (e.getClickCount() == 1) {
			    if (selected != null) {
				selecionPath = selPath.getLastPathComponent()
					.toString();
				popUpAgentMenu.show(tree, e.getX(), e.getY());
			    }
			}
		    }
		}
	    }
	};

	tree.addMouseListener(ml);

	frame.addWindowListener(this);
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	if (environment.isPartOfDomain())
	    frame.setTitle(environment.getDomainPartName() + "."
		    + environment.getDomainName() + " - SemantiCore");
	else
	    frame.setTitle(environment.getDomainName() + " - SemantiCore");

	try {
	    new GuiService(this, -1, "").addTreeDomain(environment
		    .getDomainName());
	    new GuiService(this, -1, "").addTreeDomainPart(
		    environment.getDomainName(), "Main");

	    if (environment.isPartOfDomain())
		new GuiService(this, -1, "").addTreeDomainPart(
			environment.getDomainName(),
			environment.getDomainPartName());
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
    }

    private boolean nodeIsAgent(String selPath) {
	try {
	    if (environment.getLocalAgentByName(selPath) != null)
		return true;
	} catch (Exception e) {
	    return false;
	}
	return false;
    }

    public void showUp() {
	frame.setVisible(true);

	new Thread(this).start();
    }

    protected static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = GUISemantiCore.class.getClassLoader()
		.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }

    public void actionPerformed(ActionEvent e) {
	try {
	    if (e.getSource() == subMenuAbout) {
		new About();
	    } else if (e.getSource() == agetMenuItemGoal) {
		if (selecionPath != null) {
		    SemanticAgent agent = environment
			    .getLocalAgentByName(selecionPath);
		    agent.setupGoals();
		}
	    } else if (e.getSource() == shutDownPlataform) {
		close();
	    } else if (e.getSource() == showRoutingTableDomain) {
		new FrameDomain(this.environment);
	    } else if (e.getSource() == showRoutingTable) {
		fa = new FrameAgents(this.environment.getControlBridge()
			.getAgentRoutingTableElements());
		addObserver(fa);
	    } else if (e.getSource() == this.actionSendMessage) {
		String to = JOptionPane.showInputDialog("To ?");
		String content = JOptionPane.showInputDialog("Content ?");

		if (to == null || to.trim().length() == 0)
		    to = SemanticMessage.sendToAll;
		if (content == null)
		    content = new String();

		environment.sendSemanticMessage(new SemanticMessage(
			"SemantiCore Message", "SemantiCore", to, content));
	    } else if (e.getSource() == showRoutingTableInterDomain) {
		new FrameInterDomain(this.environment);
	    }
	} catch (Exception ex) {

	}
    }

    private void removeTreeNode(MutableTreeNode node) {
	try {
	    MutableTreeNode parent = (MutableTreeNode) (node.getParent());

	    if (parent != null)
		treeModel.removeNodeFromParent(node);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print(">>>> GUI : error (removeTreeNode - 2) ");
	    e.printStackTrace();
	}

	tree.repaint();
    }

    private DefaultMutableTreeNode treeListContains(
	    LinkedList<DefaultMutableTreeNode> list, String objectName) {
	Iterator<DefaultMutableTreeNode> iter = list.iterator();

	DefaultMutableTreeNode node = null;

	while (iter.hasNext()) {
	    node = iter.next();

	    if (node.getUserObject().toString().equals(objectName))
		return node;
	}

	return null;
    }

    private DefaultMutableTreeNode tableDomainContains(
	    DefaultMutableTreeNode node) {
	Enumeration<DefaultMutableTreeNode> e = tableDomainNodes.keys();

	while (e.hasMoreElements()) {
	    DefaultMutableTreeNode n = e.nextElement();

	    if (n.toString().equals(node.toString()))
		return n;
	}

	return null;
    }

    private void close() {
	int operation = JOptionPane.showConfirmDialog(null,
		"Exit application ?", "SemantiCore",
		JOptionPane.YES_NO_CANCEL_OPTION);

	switch (operation) {
	case JOptionPane.YES_OPTION:
	    System.exit(0);
	    break;

	case JOptionPane.NO_OPTION:
	    JOptionPane.showMessageDialog(null, "SemantiCore still running!");
	    frame.setVisible(false);
	    break;

	case JOptionPane.CANCEL_OPTION:
	    break;
	}
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
	close();
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void update(Observable arg0, Object param) {
	frame.repaint();

	setChanged();
	notifyObservers(param);

	try {
	    Object[] params = (Object[]) param;
	    new GuiService(this, (Integer) params[0], params[1]).start();
	} catch (Exception e) {
	    SemantiCore.notification.print("[ E ] GuiSemanticore : ");
	    SemantiCore.notification.print("\t" + e.getMessage());
	}
    }

    private class GuiService extends Thread {
	private Integer operation = -1;

	private Object param = null;

	private GUISemantiCore gui = null;

	public GuiService(GUISemantiCore gui, Integer operation, Object param) {
	    this.operation = operation;
	    this.param = param;
	    this.gui = gui;
	}

	@Override
	public void run() {
	    AgentRoutingTable agent = null;

	    switch (operation) {
	    case ADD_DOMAIN:
		Domain domain = (Domain) param;
		addTreeDomain(domain.getDomainName());
		break;
	    case REMOVE_DOMAIN:
		Domain d = (Domain) param;
		removeTreeDomain(d.getDomainName());
		break;

	    case ADD_DOMAIN_PART:
		DomainPart part = (DomainPart) param;
		addTreeDomainPart(part.getDomainName(),
			part.getDomainPartName());
		break;
	    case REMOVE_DOMAIN_PART:
		DomainPart p = (DomainPart) param;
		removeTreeDomainPart(p.getDomainName(), p.getDomainPartName());
		break;

	    case ADD_AGENT:
		agent = (AgentRoutingTable) param;
		addTreeAgent(agent.getName(), agent.getDomainName(),
			agent.getDomainPartName());
		break;
	    case REMOVE_AGENT:
		agent = (AgentRoutingTable) param;
		removeTreeAgent(agent.getName(), agent.getDomainName(),
			agent.getDomainPartName());
		break;

	    }
	}

	public void addTreeDomain(String domain) {
	    TreeNodeDomain tnd = new TreeNodeDomain(domain);

	    root.addDomain(tnd);

	    treeModel.insertNodeInto(tnd, root, root.domains.size() - 1);
	}

	protected void removeTreeDomain(String domainName) {
	    try {
		TreeNodeDomain domain = root.removeDomain(domainName);

		if (domain != null)
		    removeTreeNode(domain);
	    } catch (Exception e) {
		System.err
			.println("[ E ] RemoveTreeDomain : " + e.getMessage());
	    }
	}

	public void addTreeDomainPart(String domainName, String domainPartName) {
	    try {
		TreeNodeDomain tnd = root.getDomain(domainName);
		TreeNodeDomainPart tndp = new TreeNodeDomainPart(domainPartName);

		if (tnd != null) {
		    tnd.addDomainPart(tndp);

		    if (domainPartName.equals("Main"))
			treeModel.insertNodeInto(tndp, tnd, 0);
		    else
			treeModel.insertNodeInto(tndp, tnd, 1);
		}
	    } catch (Exception e) {
		System.err
			.println("[ E ] AddTreeDomainPart: " + e.getMessage());
	    }
	}

	protected void removeTreeDomainPart(String domainName,
		String domainPartName) {
	    try {
		TreeNodeDomain domain = root.getDomain(domainName);

		if (domain != null) {
		    TreeNodeDomainPart tndp = domain
			    .removeDomainPart(domainPartName);

		    if (tndp != null)
			removeTreeNode(tndp);
		}
	    } catch (Exception e) {
		System.err.println("[ E ] RemoveTreeDomainPart : "
			+ e.getMessage());
	    }
	}

	public void addTreeAgent(String agentName, String domainName,
		String domainPart) {
	    try {
		TreeNodeAgent tna = new TreeNodeAgent(agentName);
		TreeNodeDomain tnd = root.getDomain(domainName);

		if (tnd != null) {
		    TreeNodeDomainPart tndp = tnd.getDomainPart(domainPart);

		    if (tndp != null) {
			if (tndp.addAgent(tna))
			    treeModel.insertNodeInto(tna, tndp, 0);
		    } else {
			tndp = tnd.getDomainPart("Main");
			if (tndp.addAgent(tna))
			    treeModel.insertNodeInto(tna, tndp, 0);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
		System.err.println("[ E ] GUI : addTreeAgent > "
			+ e.getMessage());
	    }
	}
    }

    public void removeTreeAgent(String agentName, String domainName,
	    String domainPart) {
	try {
	    TreeNodeDomain tnd = root.getDomain(domainName);
	    if (tnd != null) {
		TreeNodeDomainPart tndp = tnd.getDomainPart(domainPart);
		if (tndp != null) {
		    TreeNodeAgent tna = tndp.removeAgent(agentName);
		    if (tna != null)
			removeTreeNode(tna);
		} else {
		    tndp = tnd.getDomainPart("Main");
		    TreeNodeAgent tna = tndp.removeAgent(agentName);
		    if (tna != null)
			removeTreeNode(tna);
		}
	    }
	} catch (Exception e) {
	    System.err.println("[ E ] GUI : removeTreeAgent > "
		    + e.getMessage());
	}
    }

    public void run() {
	while (frame.isVisible()) {
	    Enumeration<AgentRoutingTable> agents = environment
		    .getControlBridge().getAgentRoutingTableElements();

	    while (agents.hasMoreElements()) {
		try {
		    new GuiService(this, this.ADD_AGENT, agents.nextElement())
			    .start();
		} catch (Exception e) {
		}
	    }

	    try {
		Thread.sleep(3000);
	    } catch (InterruptedException e) {
	    }
	}
    }
}
