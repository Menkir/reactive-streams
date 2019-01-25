package prototype.view;

import io.rsocket.Payload;
import prototype.server.Server;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import rx.swing.sources.ListSelectionEventSource;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Stack;

public class Monitor extends JFrame{
    private JPanel[][] map = new JPanel[10][10];
    private JLayeredPane coordinateSystem;
    private JPanel inspector;
    private JLabel lblCoordinate;
    private Stack<Disposable> disposableStack;
    public Monitor(Server server){
        this.disposableStack = new Stack<>();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(750,460));
        this.setLayout(null);

        // REACTIVE JLIST
        DefaultListModel<Map.Entry<Integer, Flux<Payload>>> listmodel = new DefaultListModel<>();
        server.getChannels()
              .subscribe(channel -> listmodel.addElement(new SimpleEntry<>(listmodel.getSize()+1, channel)));
        JList<Map.Entry<Integer, Flux<Payload>>> clientList = new JList<>(listmodel);
        ListSelectionEventSource.fromListSelectionEventsOf(clientList.getSelectionModel())
                .filter(ListSelectionEvent::getValueIsAdjusting)
                .subscribe(event -> {
                    disposeAll();
                    DefaultListSelectionModel data = ((DefaultListSelectionModel) event.getSource());
                    Map.Entry<Integer, Flux<Payload>> selectedChannel = listmodel.getElementAt(data.getAnchorSelectionIndex());
                    Flux<Payload> incomingCoordinates = selectedChannel.getValue();
                    ClientGraphic clientGraphic = new ClientGraphic(incomingCoordinates);
                    clientGraphic.setSize(new Dimension(20,20));
                    coordinateSystem.add(clientGraphic);
                    coordinateSystem.setLayer(clientGraphic, 1);
                    disposableStack.push(incomingCoordinates.subscribe(payload -> {
                        lblCoordinate.setText(Serializer.deserialize(payload).toString());
                    }));
                });
        JScrollPane scrollPane = new JScrollPane(clientList);
        scrollPane.setBounds(0,0,300, 300);


        // COORDINATESYSTEM
        coordinateSystem = new JLayeredPane();
        coordinateSystem.setLayout(null);
        coordinateSystem.setBounds(310, 0, 350,350);
        coordinateSystem.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        for(int i = 0; i< 10; ++i){
            for(int j= 0; j< 10; ++j){
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
        signalTower.setBounds(server.signalTower.get_1()*35, server.signalTower.get_2()*35, 35,35);
        coordinateSystem.add(signalTower);
        coordinateSystem.setLayer(signalTower, 2);

        // INSPECTOR
        inspector = new JPanel(new GridLayout(3,1));
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
        this.setVisible(true);
    }

    private void disposeAll(){
        while(!disposableStack.empty()){
            disposableStack.pop().dispose();
        }
    }
}
