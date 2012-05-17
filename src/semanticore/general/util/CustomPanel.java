package semanticore.general.util;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CustomPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final static int CIRCLE = 1;

    private Color color;

    private int shape;

    public void paintComponent(Graphics g) {
	super.paintComponents(g);

	if (shape == CIRCLE) {
	    g.setColor(Color.WHITE);
	    g.fillRect(0, 0, 32, 32);
	    g.setColor(color);
	    g.fillOval(0, 0, 32, 32);
	}
    }

    public void draw(int shapeToDraw, Color color) {
	this.shape = shapeToDraw;
	this.color = color;

	repaint();
    }

}
