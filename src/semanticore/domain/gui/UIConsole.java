package semanticore.domain.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UIConsole extends JFrame {
    public JTextArea textArea;

    public UIConsole(String text, String title) {
	textArea = new JTextArea(text);

	JScrollPane scroll = new JScrollPane(textArea);

	this.getContentPane().add(scroll);
	this.setSize(640, 480);
	this.setTitle(title);
	this.setVisible(true);
    }
}
