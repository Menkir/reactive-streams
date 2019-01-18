package prototype.view;

import io.rsocket.Payload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;

public class ClientGraphic extends JPanel {
	ClientGraphic(Flux<Payload> flux){
		setLayout(null);
		flux.subscribe(payload -> {
			Coordinate coordinate = Serializer.deserialize(payload.getData().array());
			int x = coordinate.get_1();
			int y = coordinate.get_2();
			setLocation( x+ 35*x, y + 35*y);
			repaint();
		});
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.yellow);
		g2.fillOval(0,0,20,20);
	}
}
