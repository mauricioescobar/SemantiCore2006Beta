package semanticore.domain.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import semanticore.domain.model.AgentRoutingTable;

final public class FrameAgents extends JFrame implements ActionListener,
	Observer {
    private JButton btOk;

    private DefaultTableModel defaultModel;

    private JTable table = new JTable();

    private Enumeration<AgentRoutingTable> elements;

    private LinkedList<String> agents = new LinkedList<String>();

    public FrameAgents(Enumeration<AgentRoutingTable> e) {
	this.elements = e;

	// TABLE -------------------------------
	defaultModel = new DefaultTableModel() {
	    public int getColumnCount() {
		return 3;
	    }
	};

	String[] columnNames = { "Name", "Component", "Domain" };

	// table = new JTable ( data, columnNames );
	defaultModel.setColumnIdentifiers(columnNames);
	table = new JTable(defaultModel);
	JScrollPane scrollPaneTable = new JScrollPane(table);

	btOk = new JButton("Close");
	btOk.setBackground(new Color(235, 233, 237));
	btOk.setFont(new Font("Verdana", Font.PLAIN, 11));
	btOk.setBorder(BorderFactory.createLineBorder(new Color(197, 195, 199),
		3));

	btOk.setBounds(200, 230, 100, 30);
	scrollPaneTable.setBounds(25, 25, 450, 200);

	btOk.addActionListener(this);

	this.getContentPane().add(btOk);
	this.getContentPane().add(scrollPaneTable);

	this.setTitle("Agents - SemantiCore 2006");
	this.setSize(500, 300);
	this.setLocationRelativeTo(null);
	this.setLayout(null);
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	this.getContentPane().setBackground(Color.WHITE);

	init();

	this.setVisible(true);
    }

    private void init() {
	while (elements.hasMoreElements()) {
	    AgentRoutingTable agent = elements.nextElement();

	    String key;
	    if (agent.getDomainPartName().length() > 0) {
		key = agent.getName() + agent.getComponent()
			+ agent.getDomainPartName() + "."
			+ agent.getDomainName();
		if (!agents.contains(key)) {
		    agents.add(key);
		    defaultModel.addRow(new Object[] {
			    agent.getName(),
			    agent.getComponent(),
			    agent.getDomainPartName() + "."
				    + agent.getDomainName() });
		}
	    } else {
		key = agent.getName() + agent.getComponent()
			+ agent.getDomainName();
		if (!agents.contains(key)) {
		    agents.add(key);
		    defaultModel.addRow(new Object[] { agent.getName(),
			    agent.getComponent(), agent.getDomainName() });
		}
	    }
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btOk)
	    this.dispose();
    }

    public void update(Observable arg0, Object arg1) {
	try {
	    Object[] params = (Object[]) arg1;
	    Integer operation = (Integer) params[0];
	    AgentRoutingTable agent = (AgentRoutingTable) params[1];

	    String key;

	    switch (operation) {
	    case GUISemantiCore.ADD_AGENT:

		if (agent.getDomainPartName().length() > 0) {
		    key = agent.getName() + agent.getComponent()
			    + agent.getDomainPartName() + "."
			    + agent.getDomainName();
		    if (!agents.contains(key)) {
			if (agents.add(key))
			    defaultModel.addRow(new Object[] {
				    agent.getName(),
				    agent.getComponent(),
				    agent.getDomainPartName() + "."
					    + agent.getDomainName() });
		    }
		} else {
		    key = agent.getName() + agent.getComponent()
			    + agent.getDomainName();
		    if (!agents.contains(key)) {
			if (agents.add(key))
			    defaultModel
				    .addRow(new Object[] { agent.getName(),
					    agent.getComponent(),
					    agent.getDomainName() });
		    }
		}
		break;

	    case GUISemantiCore.REMOVE_AGENT:
		if (agent.getDomainPartName().length() > 0) {
		    key = agent.getName() + agent.getComponent()
			    + agent.getDomainPartName() + "."
			    + agent.getDomainName();

		    if (agents.remove(key)) {
			for (int i = 0; i < defaultModel.getRowCount(); i++) {
			    String nome = (String) defaultModel
				    .getValueAt(i, 0);
			    String component = (String) defaultModel
				    .getValueAt(i, 1);
			    String domain = (String) defaultModel.getValueAt(i,
				    2);

			    if (nome.equals(agent.getName())
				    && component.equals(agent.getComponent())
				    && domain.equals(agent.getDomainPartName()
					    + "." + agent.getDomainName())) {
				defaultModel.removeRow(i);
				return;
			    }
			}
		    }
		} else {
		    key = agent.getName() + agent.getComponent()
			    + agent.getDomainName();

		    if (agents.remove(key)) {
			for (int i = 0; i < defaultModel.getRowCount(); i++) {
			    String nome = (String) defaultModel
				    .getValueAt(i, 0);
			    String component = (String) defaultModel
				    .getValueAt(i, 1);
			    String domain = (String) defaultModel.getValueAt(i,
				    2);

			    if (nome.equals(agent.getName())
				    && component.equals(agent.getComponent())
				    && domain.equals(agent.getDomainName())) {
				defaultModel.removeRow(i);
				return;
			    }
			}
		    }
		}
		break;
	    }
	} catch (Exception e) {

	}
    }
}
