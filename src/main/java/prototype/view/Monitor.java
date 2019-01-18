package prototype.view;

import io.rsocket.Payload;
import prototype.model.Coordinate;
import prototype.server.Server;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Monitor extends JFrame{
    private JPanel[][] map = new JPanel[10][10];
    private JLayeredPane motherPanel;
    public Monitor(Server server){
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(750,460));
        this.setLayout(null);
        DefaultListModel<Flux<Payload>> listmodel = new DefaultListModel<>();
        server.getChannels()
              .subscribe(listmodel::addElement);

        JList<Flux<Payload>> clientList = new JList<>(listmodel);
        clientList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList<Flux<Payload>> list = (JList<Flux<Payload>>) e.getSource();
                ClientGraphic viewClient = new ClientGraphic(list.getSelectedValue());
                viewClient.setSize(new Dimension(20,20));
                motherPanel.add(viewClient, 0);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        JScrollPane scrollPane = new JScrollPane(clientList);
        scrollPane.setBounds(0,0,300, 300);

         motherPanel = new JLayeredPane();
        motherPanel.setLayout(null);
        motherPanel.setBounds(310, 0, 350,350);
        motherPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        for(int i = 0; i< 10; ++i){
            for(int j= 0; j< 10; ++j){
                map[i][j] = new JPanel();
                int panelSize = 35;
                map[i][j].setBackground(Color.BLACK);
                map[i][j].setBorder(BorderFactory.createLineBorder(Color.RED));
                map[i][j].setBounds(i*panelSize,j*panelSize,panelSize,panelSize);
                motherPanel.add(map[i][j], 1);
            }
        }
        this.add(scrollPane);
        this.add(motherPanel);
        this.pack();
        this.setVisible(true);
    }
}
