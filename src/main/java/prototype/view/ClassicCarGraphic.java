package prototype.view;

import io.rsocket.Payload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class ClassicCarGraphic extends JPanel {
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
	private int hashcode;
	Map<Integer, ArrayDeque<Coordinate>> cars;
	ClassicCarGraphic(int hashcode, Map<Integer, ArrayDeque<Coordinate>> cars){
		setLayout(null);
		this.hashcode = hashcode;
		this.cars = cars;
		setBounds(0,0,20,20);

		// START THREAD FOR HANDLING CHANGED COORDINATES
		CompletableFuture.runAsync(() -> {
			ArrayDeque<Coordinate> queue = cars.get(hashcode);
			while(true){
				if(queue.isEmpty())
					continue;
				delay();
				Coordinate coordinate = queue.poll();
				System.out.println("[MONITOR] "+hashcode+"  receives " + coordinate);
				int x = coordinate.get_1();
				int y = coordinate.get_2();
				setLocation( x+ 35*x, y + 35*y);
			}

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

	private void delay(){
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
