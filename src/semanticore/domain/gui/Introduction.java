package semanticore.domain.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import semanticore.domain.SemantiCoreDefinitions;

public class Introduction extends JFrame {
    private Toolkit toolkit = getToolkit();

    private Image logo = toolkit.getImage(getClass().getClassLoader()
	    .getResource("semanticore/general/images/Logo_Vetor_Vertical.jpg"));

    private JProgressBar progressBar = new JProgressBar(0, 10);

    private int value = 0;

    public Introduction() {
	this.setTitle(SemantiCoreDefinitions.title);
	this.setUndecorated(true);
	// this.setSize ( 697, 294 );
	this.setSize(520, 218);
	this.setLocationRelativeTo(null);
	this.setLayout(new BorderLayout());
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	this.getContentPane().add(progressBar, BorderLayout.SOUTH);

	this.progressBar.setString(Integer.toString(value));
	// this.progressBar.setStringPainted ( true );
    }

    public void showUp() {
	this.setVisible(true);
    }

    public void inc() {
	value++;
	this.progressBar.setValue(value);
	this.progressBar.setString(Integer.toString(value * 10));
    }

    public void paint(Graphics g) {
	super.paint(g);

	g.drawImage(logo, 1, 1, this);
	g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    }
}
