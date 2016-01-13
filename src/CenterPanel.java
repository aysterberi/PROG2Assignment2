import javax.swing.*;
import java.awt.*;

public class CenterPanel extends JPanel {

	private ImageIcon mapImage;

	public CenterPanel(String fileName) {
		if (fileName != null) {
		mapImage = new ImageIcon(fileName);
		int width = mapImage.getIconWidth();
		int height = mapImage.getIconHeight();
		Dimension d = new Dimension(width, height);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setLayout(null);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mapImage.getImage(), 0, 0, getWidth(), getHeight(), this);
	}

}
