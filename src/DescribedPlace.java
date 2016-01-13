import java.awt.*;
import java.io.*;


public class DescribedPlace extends NamedPlace implements Serializable {
	
	private String description;
	
	public DescribedPlace(Position p, String name, String category, Color c, String description) {
		super(p, name, category, c);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public String[] paintDescription(String str) {
		String reg = " ";
		String[] strArr = str.split(reg);
		return strArr;
	}

	@Override
	protected void paintExtend(Graphics g) {
		int line = 20;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 150, 200);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(name.toUpperCase()+"\n", 5, line);
		line += 30;
		String[] strArr = paintDescription(description);
		for (String str : strArr){
			g.drawString(str, 5, line);
			line += 20;
		}
	}
}
