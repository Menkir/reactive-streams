package rsocket;
import view.Gui;

import java.util.Scanner;

public class App {

    public static void main(String... args){
        Scanner scanner = new Scanner(System.in);
        Server server = new Server();
        Client client = new Client();
        Gui gui = new Gui(client);

        System.out.println("Press CTRL+D to terminate Application");
        while(scanner.hasNext()){

        }
        server.dispose();
        client.dispose();
    }
}
