package semanticore.domain.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

final public class About extends JFrame implements ActionListener,
	MouseListener {
    private Image logo = getToolkit().getImage(
	    getClass().getClassLoader().getResource(
		    "semanticore/general/images/logo_p_vertical.jpg"));

    private JButton btOk;

    private JLabel label1 = new JLabel("Please visit");

    private JLabel build = new JLabel("V210207-01");

    private JLabel label2 = new JLabel("http://semanticore.pucrs.br");

    public About() {
	btOk = new JButton("Close");
	btOk.setBackground(new Color(235, 233, 237));
	// btOk.setCursor ( new Cursor ( Cursor.HAND_CURSOR ) );
	btOk.setFont(new Font("Verdana", Font.PLAIN, 11));
	btOk.setBorder(BorderFactory.createLineBorder(new Color(197, 195, 199),
		3));
	btOk.setBounds(100, 160, 100, 30);

	btOk.addActionListener(this);
	label2.addMouseListener(this);

	label1.setBounds(110, 90, 200, 50);
	label2.setBounds(70, 110, 200, 50);
	build.setBounds(225, 160, 80, 50);

	this.getContentPane().add(btOk);
	this.getContentPane().add(label1);
	this.getContentPane().add(label2);
	this.getContentPane().add(build);

	this.setTitle("SemantiCore 2006");
	this.setUndecorated(true);
	this.setSize(300, 200);
	this.setLocationRelativeTo(null);
	this.setLayout(null);
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	this.getContentPane().setBackground(Color.WHITE);

	this.setVisible(true);
    }

    public void paint(Graphics g) {
	super.paint(g);

	g.setColor(Color.BLACK);
	g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);

	g.drawImage(logo, 60, 10, this);

    }

    public static void main(String[] args) {
	new About();
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btOk)
	    this.dispose();
    }

    public void mouseClicked(MouseEvent e) {
	// TODO Auto-generated method stub
	if (e.getSource() == label2) {
	    if (System.getProperties().getProperty("os.name").toLowerCase()
		    .contains("win")) {
		try {
		    Runtime.getRuntime().exec(
			    "rundll32 SHELL32.DLL,ShellExec_RunDLL "
				    + "iexplore " + label2.getText());
		} catch (Exception e1) {
		    // e1.printStackTrace ( );
		}
	    }
	}
    }

    public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub

    }
}
