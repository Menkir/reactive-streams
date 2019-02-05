package prototype.async.view;

import io.rsocket.Payload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;

public class ClientGraphic extends JPanel {
	Color[] colors = {
			Color.GRAY,
			Color.PINK,
			Color.YELLOW,
			Color.RED,
			Color.ORANGE,
			Color.BLUE,
			Color.GREEN,
			Color.CYAN,
			Color.MAGENTA
	};
	private Color myColor;
	private Disposable disposable;
	ClientGraphic(Flux<Payload> flux){
		setLayout(null);
		disposable = flux.subscribe(payload -> {
			Coordinate coordinate = Serializer.deserialize(payload);
			int x = coordinate.get_1();
			int y = coordinate.get_2();
			setBounds(0,0,20,20);
			setLocation( x+ 35*x, y + 35*y);
		});
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if(myColor == null){
			g2.setColor(colors[(((int) (Math.random() * colors.length-1)))]);
			myColor = g2.getColor();
		} else
			g2.setColor(myColor);


		g2.fillOval(0,0,20,20);
	}

	public Disposable getDisposable(){
		return this.disposable;
	}
}
