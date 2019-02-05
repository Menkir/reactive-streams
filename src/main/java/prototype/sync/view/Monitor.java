package prototype.sync.view;

import prototype.sync.server.Server;
import prototype.model.Coordinate;
import prototype.utility.Tuple2;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.rmi.server.UID;
import java.util.*;

public class Monitor extends JFrame implements Observer {
	private Server server;
	private JLabel lblCoordinate;
	private JLayeredPane coordinateSystem;
	private JList<Map.Entry<Integer, Socket>> clientList;
	private Map<Integer, ArrayDeque<Coordinate>> cars = new TreeMap<>();
	private CarGraphicController graphicController = new CarGraphicController();

	public Monitor(Server server){
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(750,460));
		this.setLayout(null);
		this.server = server;
		this.server.addObserver(this);

		// SCROLLPANE
		//JScrollPane scrollPane = new JScrollPane(clientList);
		//scrollPane.setBounds(0,0,300, 300);

		// COORDINATESYSTEM
		coordinateSystem = new JLayeredPane();
		coordinateSystem.setLayout(null);
		coordinateSystem.setBounds(310, 0, 350,350);
		coordinateSystem.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		for(int i = 0; i< 10; ++i){
			for(int j= 0; j< 10; ++j){
				JPanel[][] map = new JPanel[10][10];
				map[i][j] = new JPanel();
				int panelSize = 35;
				map[i][j].setBackground(Color.BLACK);
				map[i][j].setBorder(BorderFactory.createLineBorder(Color.RED));
				map[i][j].setBounds(i*panelSize,j*panelSize,panelSize,panelSize);
				coordinateSystem.add(map[i][j]);
				coordinateSystem.setLayer(map[i][j], 0);
			}
		}

		// INSPECTOR
		JPanel inspector = new JPanel(new GridLayout(3, 1));
		inspector.setBounds(0, 310, 300,100);
		inspector.setBackground(Color.WHITE);
		inspector.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblCoordinate = new JLabel("");
		Font myFont = new Font(lblCoordinate.getFont().getFontName(), Font.PLAIN, 20);
		lblCoordinate.setFont(myFont);
		inspector.add(lblCoordinate);


		//this.add(scrollPane);
		this.add(coordinateSystem);
		this.add(inspector);
		this.pack();
	}

	public void start(){
		this.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof UID) {
			CarPanel car = new CarPanel((UID) arg);
			car.setSize(new Dimension(20, 20));
			coordinateSystem.add(car);
			coordinateSystem.setLayer(car, 1);
			graphicController.addCar(car);
		} else if(arg instanceof Tuple2){
			Tuple2 tuple = (Tuple2) arg;
			UID id = (UID) tuple.getT();
			Coordinate coordinate = (Coordinate) tuple.getE();
			graphicController.updateGraphic(coordinate, id);
		}
	}

	private class CarGraphicController extends Observable {
		public void addCar(CarPanel car){
			this.addObserver(car);
		}

		public void updateGraphic(Coordinate coordinate, UID uid){
			setChanged();
			notifyObservers(new Tuple2<>(uid, coordinate));
		}
	}

}
