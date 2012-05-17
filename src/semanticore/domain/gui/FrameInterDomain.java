package semanticore.domain.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import semanticore.domain.Environment;
import semanticore.domain.control.Domain;

final public class FrameInterDomain extends JFrame implements ActionListener,
	Runnable {
    private JButton btOk;

    private DefaultTableModel defaultModel;

    private JTable table = new JTable();

    private Environment environment;

    public FrameInterDomain(Environment env) {
	this.environment = env;

	// TABLE -------------------------------
	defaultModel = new DefaultTableModel() {
	    public int getColumnCount() {
		return 3;
	    }
	};

	String[] columnNames = { "Name", "Address", "Port" };
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

	this.setTitle("Inter Domain - SemantiCore 2006");
	this.setSize(500, 300);
	this.setLocationRelativeTo(null);
	this.setLayout(null);
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	this.getContentPane().setBackground(Color.WHITE);

	this.setVisible(true);

	new Thread(this).start();
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btOk) {
	    this.dispose();
	}
    }

    public void run() {
	while (this.isDisplayable()) {
	    try {
		Enumeration<Domain> parts = this.environment.getControlBridge()
			.getInterDomainRoutingTable().elements();

		int size = defaultModel.getRowCount();
		for (int i = 0; i < size; i++)
		    defaultModel.removeRow(0);

		while (parts.hasMoreElements()) {
		    Domain part = parts.nextElement();
		    defaultModel.addRow(new Object[] { part.getDomainName(),
			    part.getAddress(), part.getPort() });
		}

		Thread.sleep(2000);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
