package prototype.sync.view;

import io.rsocket.Payload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import prototype.utility.Tuple2;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CarPanel extends JPanel implements Observer {
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
	private UID id;

	CarPanel(UID id){
		setLayout(null);
		setBounds(0,0,20,20);
		this.id = id;
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

	@Override
	public void update(Observable o, Object arg) {
		Tuple2<UID, Coordinate> tuple = (Tuple2<UID, Coordinate>) arg;
		if(this.id != tuple.getT()) return;
		Coordinate coordinate  = tuple.getE();
		int x = coordinate.get_1();
		int y = coordinate.get_2();
		setLocation( x+ 35*x, y + 35*y);
	}

}
