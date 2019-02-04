package prototype.view;

import prototype.endpoints.classicServerImpl.Server;
import prototype.model.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ClassicMonitor extends JFrame implements Observer {
	private Server server;
	private JLabel lblCoordinate;
	private JLayeredPane coordinateSystem;
	private JList<Map.Entry<Integer, Socket>> clientList;
	private Map<Integer, ArrayDeque<Coordinate>> cars = new TreeMap<>();

	public ClassicMonitor(Server server){
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

		// SIGNAL TOWER
		JPanel signalTower = new SignalTowerGraphic();
		signalTower.setBounds(4*35, 4*35, 35,35);
		coordinateSystem.add(signalTower);
		coordinateSystem.setLayer(signalTower, 2);

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
		Server.Tuple<Integer, Coordinate> tuple = (Server.Tuple<Integer, Coordinate>) arg;

		if(!cars.containsKey(tuple.getT())){
			cars.put(tuple.getT(), new ArrayDeque<>());
			cars.get(tuple.getT()).add(tuple.getE());

			// ADD CAR GRAPHIC
			ClassicCarGraphic car = new ClassicCarGraphic(tuple.getT(), cars);
			car.setSize(new Dimension(20,20));
			coordinateSystem.add(car);
			coordinateSystem.setLayer(car, 1);
		} else cars.get(tuple.getT()).add(tuple.getE());
	}

}
