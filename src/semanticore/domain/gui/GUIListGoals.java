package semanticore.domain.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import semanticore.domain.model.Goal;

public class GUIListGoals extends JFrame implements ActionListener,
	MouseListener {
    private Goal goal;

    private DefaultListModel listModel;

    private JList list;

    private JLabel labelGoals;

    private JScrollPane scrool;

    protected Hashtable<String, Goal> goals;

    private JPopupMenu popUpAgentGoal = new JPopupMenu();

    private JMenuItem goalMenuItemStart = new JMenuItem("Start");

    private JMenuItem goalMenuItemReset = new JMenuItem("Reset");

    public GUIListGoals(Hashtable<String, Goal> goals) {
	this.goals = goals;

	initializeGUIComponents();
	setComponentsLookAndFell();
	setComponentsSize();
	setComponentsLocation();

	// PopUpMenu
	popUpAgentGoal.add(goalMenuItemStart);
	popUpAgentGoal.add(goalMenuItemReset);

	goalMenuItemReset.addActionListener(this);
	goalMenuItemStart.addActionListener(this);
	// ----

	// Frame
	list.addMouseListener(this);

	this.getContentPane().add(labelGoals);
	this.getContentPane().add(scrool);

	this.setSize(650, 400);
	this.getContentPane().setBackground(Color.WHITE);
	this.setLayout(null);
	this.setLocationRelativeTo(null);
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	this.setTitle("Goals");
	this.setResizable(false);

	this.list.setFont(new Font("Courier New", Font.PLAIN, 14));

	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	initList();

	showUp();
    }

    public void updateList() {
	listModel.removeAllElements();
	initList();
    }

    private void initList() {
	Enumeration<Goal> elements = goals.elements();

	while (elements.hasMoreElements()) {
	    Goal g = elements.nextElement();

	    listModel.addElement(g.toString());
	}
    }

    public void showUp() {
	this.setVisible(true);
    }

    private void initializeGUIComponents() {
	labelGoals = new JLabel("Goals");

	listModel = new DefaultListModel();
	list = new JList(listModel);
	scrool = new JScrollPane(list);
    }

    private void setComponentsSize() {
	labelGoals.setSize(200, 80);
	scrool.setSize(550, 250);
    }

    private void setComponentsLocation() {
	labelGoals.setLocation(50, 10);
	scrool.setLocation(50, 70);
    }

    private void setComponentsLookAndFell() {
	Font f1 = new Font("Verdana", Font.PLAIN, 18);
	labelGoals.setFont(f1);
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == goalMenuItemStart) {
	    try {
		String selectedValue = (String) list.getSelectedValue();
		String gId = selectedValue.substring(0,
			selectedValue.indexOf(" "));

		Integer.parseInt(gId);

		evaluateGoal(gId);

		updateList();
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
	if (e.getSource() == list) {
	    if (e.getButton() == 3) {
		list.setSelectedIndex(list.locationToIndex(new Point(e.getX(),
			e.getY())));

		if (list.getSelectedIndex() >= 0)
		    popUpAgentGoal.show(list, e.getX(), e.getY());
	    }

	}
    }

    public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
    }

    private void evaluateGoal(String goalId) {
	Goal g = goals.get(goalId);

	if (g != null) {
	    try {
		if (g.concepts != null)
		    g.setupGoal(this);
		else {
		    g.start();
		}
	    } catch (Exception e) {
		System.err.println("[ E ] Goal setup error! ( "
			+ e.getMessage() + " )");
		e.printStackTrace();
	    }
	}
    }
}
