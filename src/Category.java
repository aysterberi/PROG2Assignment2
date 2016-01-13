import javax.swing.*;
import java.awt.*;

public class Category extends JComponent{
	
	private String catName;
	private Color catColor;
	
	public Category(String catName, Color catColor) {
		this.catName = catName;
		this.catColor = catColor;
	}
	
	public String toString() {
		return catName;
	}

	public String getCatName() {
		return catName;
	}

	public Color getCatColor() {
		return catColor;
	}
	
}
