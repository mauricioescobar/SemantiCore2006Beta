package semanticore.domain.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import semanticore.domain.SemantiCore;
import semanticore.domain.model.Goal;
import semanticore.domain.model.GoalInput;
import semanticore.general.util.OWLUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class GUIGoal extends JDialog implements ActionListener, MouseListener {
    private Goal goal;

    private JButton buttonSubmit;

    private JButton buttonUpdate;

    private JButton buttonSeeOntology;

    private JComboBox comboSubject;

    private JComboBox comboPredicate;

    private JComboBox comboObject;

    private DefaultListModel listModel;

    private JList list;

    private JScrollPane scrool;

    private String namespace;

    public GUIGoal(Goal g, String namespace, JFrame frameOwner) {
	super(frameOwner, true);

	this.goal = g;

	initializeGUIComponents();
	setComponentsLookAndFell();
	setComponentsSize();
	setComponentsLocation();

	// Frame
	list.addMouseListener(this);
	buttonSubmit.addActionListener(this);
	buttonUpdate.addActionListener(this);
	buttonSeeOntology.addActionListener(this);

	this.getContentPane().add(comboSubject);
	this.getContentPane().add(comboPredicate);
	this.getContentPane().add(comboObject);
	this.getContentPane().add(buttonSubmit);
	this.getContentPane().add(buttonUpdate);
	this.getContentPane().add(buttonSeeOntology);
	this.getContentPane().add(scrool);

	this.setSize(650, 500);
	this.setLayout(null);
	this.setLocationRelativeTo(getOwner());
	this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

	this.setResizable(false);
	this.setTitle("Goal Setup [ " + goal.getID() + " ]");
	this.namespace = namespace;

	initList();
	mapOntology();

	// this.setVisible ( true );
    }

    private void initList() {
	listModel.addElement("==========================================");
	listModel.addElement("Statements to be created");
	listModel.addElement(" ");

	Iterator<GoalInput> iter = goal.concepts.iterator();
	while (iter.hasNext()) {
	    GoalInput goalInput = iter.next();
	    String owlClass = goalInput.owlClass; // recupera o nome da class
	    String property = goalInput.owlProperty; // recupera a propriedade

	    listModel.addElement("Class: " + owlClass + " - Property: "
		    + property);
	}
	listModel.addElement("==========================================");
	listModel.addElement("Created statements");
	listModel.addElement(" ");
    }

    private void mapOntology() {
	try {

	    comboSubject.removeAllItems();
	    comboPredicate.removeAllItems();
	    comboObject.removeAllItems();

	    // printStatements ( goal.getOntology ( ), null, goal.getOntology (
	    // ).getProperty ( NS + "status" ), null );

	    comboSubject.addItem(namespace);
	    comboPredicate.addItem("");
	    comboObject.addItem("");

	    // devo listar as classes
	    OntModel model = goal.getOntology();

	    Resource r = null;
	    // StmtIterator iter = model.listStatements ( null,
	    // model.getProperty ( namespace + "Medico" ), r );
	    StmtIterator iter = model.listStatements();

	    String predicateAnt = "";
	    String objectAnt = "";

	    comboPredicate
		    .addItem("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

	    while (iter.hasNext()) {
		Statement s = iter.nextStatement();

		String predicate = s.getPredicate().toString();
		String object = s.getObject().toString();

		if (!predicateAnt.contains(predicate)
			&& predicate.contains(namespace)) {
		    comboPredicate.addItem(predicate);
		    predicateAnt += "|" + predicate;
		}
		if (!objectAnt.contains(object) && object.contains(namespace)) {
		    comboObject.addItem(object);
		    objectAnt += "|" + object;
		}
	    }

	    predicateAnt = null;
	    objectAnt = null;

	    System.gc();
	} catch (Exception e) {

	}
    }

    public void showUp() {
	this.show();
    }

    private void initializeGUIComponents() {
	buttonSubmit = new JButton("Submit");
	buttonUpdate = new JButton("Create Statement");
	buttonSeeOntology = new JButton("Show ontology");

	comboSubject = new JComboBox();
	comboSubject.setEditable(true);
	comboPredicate = new JComboBox();
	comboPredicate.setEditable(true);
	comboObject = new JComboBox();
	comboObject.setEditable(true);

	listModel = new DefaultListModel();
	list = new JList(listModel);
	scrool = new JScrollPane(list);
    }

    private void setComponentsSize() {
	comboSubject.setSize(550, 50);
	comboPredicate.setSize(550, 50);
	comboObject.setSize(550, 50);

	buttonSubmit.setSize(150, 50);
	buttonUpdate.setSize(150, 50);
	buttonSeeOntology.setSize(150, 50);
	scrool.setSize(550, 200);
    }

    private void setComponentsLocation() {
	comboSubject.setLocation(50, 10);
	comboPredicate.setLocation(50, 60);
	comboObject.setLocation(50, 110);

	buttonUpdate.setLocation(50, 170);
	buttonSeeOntology.setLocation(250, 170);
	buttonSubmit.setLocation(450, 170);
	scrool.setLocation(50, 240);
    }

    private void setComponentsLookAndFell() {
	Font courierNew = new Font("Courier New", Font.PLAIN, 14);

	this.comboSubject.setBorder(new TitledBorder(BorderFactory
		.createBevelBorder(BevelBorder.LOWERED,
			new Color(164, 164, 164), new Color(164, 164, 164)),
		" Subject ", TitledBorder.LEFT, TitledBorder.CENTER, new Font(
			"Verdana", Font.PLAIN, 12)));
	this.comboSubject.setFont(courierNew);
	this.comboSubject.setOpaque(false);

	this.comboPredicate.setBorder(new TitledBorder(BorderFactory
		.createBevelBorder(BevelBorder.LOWERED,
			new Color(164, 164, 164), new Color(164, 164, 164)),
		" Predicate ", TitledBorder.LEFT, TitledBorder.CENTER,
		new Font("Verdana", Font.PLAIN, 12)));
	this.comboPredicate.setFont(courierNew);
	this.comboPredicate.setOpaque(false);

	this.comboObject.setBorder(new TitledBorder(BorderFactory
		.createBevelBorder(BevelBorder.LOWERED,
			new Color(164, 164, 164), new Color(164, 164, 164)),
		" Object ", TitledBorder.LEFT, TitledBorder.CENTER, new Font(
			"Verdana", Font.PLAIN, 12)));
	this.comboObject.setFont(courierNew);
	this.comboObject.setOpaque(false);

	list.setFont(courierNew);
    }

    private boolean createStatement(String subject, String predicate,
	    String object) {
	try {

	    return OWLUtil.createStatement(goal.getOntology(), subject,
		    predicate, object);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("========= [ Create Statement ] =========");
	    e.printStackTrace();
	    SemantiCore.notification
		    .print("========================================");
	    return false;
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == buttonSubmit) {
	    int operation = JOptionPane.showConfirmDialog(null, "Start goal ?",
		    "Goal", JOptionPane.YES_NO_OPTION);

	    switch (operation) {
	    case JOptionPane.YES_OPTION:
		this.goal.start();
		this.dispose();
		break;

	    case JOptionPane.NO_OPTION:
		break;
	    }

	} else if (e.getSource() == buttonUpdate) {
	    if (createStatement(comboSubject.getSelectedItem().toString(),
		    comboPredicate.getSelectedItem().toString(), comboObject
			    .getSelectedItem().toString())) {
		String statement = comboSubject.getSelectedItem().toString()
			+ " " + comboPredicate.getSelectedItem().toString()
			+ " " + comboObject.getSelectedItem().toString();
		// JOptionPane.showMessageDialog ( null, "Update : Statement " +
		// statement );
		listModel.addElement(statement);
	    }
	} else if (e.getSource() == buttonSeeOntology) {
	    try {
		// new UIConsole ( OWLUtil.parseOntModelToXML ( goal.getOntology
		// ( ) ), "Goal : " + goal.getID ( ) );
		SemantiCore.notification
			.print("\n====================================================");
		SemantiCore.notification.print(OWLUtil.parseOntModelToXML(goal
			.getOntology()));
		SemantiCore.notification
			.print("====================================================\n");
	    } catch (Exception ex) {
		// TODO: handle exception
	    }
	}
    }

    public void mouseClicked(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub

    }
}
