package prototype.async.view;

import io.rsocket.Payload;
import prototype.async.server.CarServer;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import rx.Subscription;
import rx.swing.sources.ListSelectionEventSource;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Stack;

public class Monitor extends JFrame{
	private CarServer carServer;
	private JLabel lblCoordinate;
	private JLayeredPane coordinateSystem;
	private DefaultListModel<Map.Entry<Integer, Flux<Payload>>> listmodel;
	private JList<Map.Entry<Integer, Flux<Payload>>> clientList;
	private Stack<Disposable> disposableStack;

	public Monitor(CarServer carServer){
		this.carServer = carServer;
		this.disposableStack = new Stack<>();
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(750,460));
		this.setLayout(null);

		// REACTIVE JLIST
		listmodel = new DefaultListModel<>();
		clientList = new JList<>(listmodel);
		carServer.getGuiProcessor()
				.subscribe(flux -> {
					listmodel.addElement(new SimpleEntry<>(listmodel.getSize(), flux));
				});

		// SCROLLPANE
		JScrollPane scrollPane = new JScrollPane(clientList);
		scrollPane.setBounds(0,0,300, 300);

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


		this.add(scrollPane);
		this.add(coordinateSystem);
		this.add(inspector);
		this.pack();
	}

	public void start(){
		this.setVisible(true);
	}

	private void disposeAll(){
		while(!disposableStack.empty()){
			disposableStack.pop().dispose();
		}
	}

	public Subscription listeningOnIncomingCoordinates(){
		return ListSelectionEventSource.fromListSelectionEventsOf(clientList.getSelectionModel())
				.filter(ListSelectionEvent::getValueIsAdjusting)
				.subscribe(event -> {
					disposeAll();
					DefaultListSelectionModel data = ((DefaultListSelectionModel) event.getSource());
					Map.Entry<Integer, Flux<Payload>> selectedChannel = listmodel.getElementAt(data.getAnchorSelectionIndex());
					Flux<Payload> incomingCoordinates = selectedChannel.getValue();
					CarPanel carPanel = new CarPanel(incomingCoordinates);
					carPanel.setSize(new Dimension(20,20));
					coordinateSystem.add(carPanel);
					coordinateSystem.setLayer(carPanel, 1);
					disposableStack.push(incomingCoordinates.subscribe(payload -> {
						lblCoordinate.setText(Serializer.deserialize(payload).toString());
					}));
				});
	}
}