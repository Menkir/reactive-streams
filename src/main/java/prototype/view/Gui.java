package prototype.view;

import prototype.client.Client;
import prototype.model.Coordinate;
import prototype.utility.Serialiazer;

import javax.swing.*;
import java.awt.*;

public class Gui  extends JFrame{

    private JTextArea txtArea;

    public Gui(Client c){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(500,1000));
        c.getServerEndpoint()
            .subscribe(next -> {
                Coordinate data = Serialiazer.deserialize(next.getData().array());
                String txt = "("+data.get_1() + "|" + data.get_2()+"), "+data.getSignalPower()+"/10\n";
                txtArea.setText(txtArea.getText() + txt);
            });

        txtArea = new JTextArea(1, 1);
        txtArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(txtArea);

        this.add(scrollPane);
        this.pack();
        this.setVisible(true);
    }

}
