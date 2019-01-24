package prototype.view;

import javax.swing.*;
import java.awt.*;

public class SignalTowerGraphic extends JPanel {
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.GRAY);
		g2.fillOval(0,0,20,20);
	}
}
