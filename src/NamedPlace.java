import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NamedPlace extends JComponent implements Serializable {

	protected String name;
	private String category;
	private Color c;
	private PlaceListen pl = new PlaceListen();
	private int x, y;
	private boolean marked = false;
	private boolean extended = false;
	protected Font font = new Font("SansSerif", Font.BOLD, 16);

	public NamedPlace(Position p, String name, String category, Color c) {
		this.name = name;
		this.category = category;
		x = p.getX();
		y = p.getY();
		x = x - 15;
		y = y - 30;
		this.c = c;
		setBounds(x, y, 30, 30);
		Dimension d = new Dimension(30, 30);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		addMouseListener(pl);
	}

	class PlaceListen extends MouseAdapter implements Serializable {
		public void mouseReleased(MouseEvent mev) {
			if (SwingUtilities.isLeftMouseButton(mev)) {
				if (marked) {
					marked = false;
				} else if (!marked) {
					marked = true;
				}
			}

			if (SwingUtilities.isRightMouseButton(mev)) {
				if (extended) {
					setBounds(getX(), getY(), 30, 30);
					extended = false;
				} else if (!extended) {
					if (NamedPlace.this instanceof DescribedPlace) {
						setBounds(getX(), getY(), 150, 200);
					} else {
						setBounds(getX(), getY(), 151, 31);
					}
					extended = true;
				}
			}
			validate();
			repaint();
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(c);
		if (!extended) {
			int[] xes = { 0, 30, 15 };
			int[] yes = { 0, 0, 30 };
			g.fillPolygon(xes, yes, 3);
			g.setColor(Color.BLACK);
			g.drawPolygon(xes, yes, 3);
		}
		if (marked && !extended) {
			g.setColor(Color.RED);
			g.drawRect(0, 0, 29, 29);
			g.drawRect(1, 1, 27, 27);
		}
		if (extended) {
			paintExtend(g);
		}
	}

	protected void paintExtend(Graphics g) {
		g.setColor(Color.RED);
		g.drawRect(0, 0, 150, 30);
		g.drawRect(1, 1, 148, 28);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(name, 5, 21);
	}

	public String getPlaceName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

	public Position getPosition() {
		return new Position(getX(), getY());
	}
}
