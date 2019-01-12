package view;

import reactor.core.scheduler.Schedulers;
import rsocket.Client;
import rsocket.Coordinate;
import rsocket.Serialiazer;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class Gui  extends JFrame{

    private JLabel lbl;

    public Gui(Client c){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300,300);
        int ctr = 0;
        c.serverEndpoint
                .subscribeOn(Schedulers.newSingle("Gui"))
                .share()
                .publish()
                .autoConnect()
                .subscribe(next -> {
                    Coordinate data = Serialiazer.deserialize(next.getData().array());
                    lbl.setText("("+data.get_1() + "|" + data.get_2()+"), "+data.getSignalPower()+"/10");
                });

        lbl = new JLabel("DUMMY TEXT");
        this.add(lbl);
        this.pack();
        this.setVisible(true);
    }

}
