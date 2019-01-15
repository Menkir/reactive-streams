package prototype.app;

import java.util.Scanner;

import prototype.client.Client;
import prototype.server.Server;
import prototype.view.Gui;

class App {

    public static void main(final String... args) {
        Scanner scanner = new Scanner(System.in);
        Server server = new Server();
        Client client = new Client();
        Gui gui = new Gui(client);

        System.out.println("Press CTRL+D to terminate Application");
        while (scanner.hasNext()) {

        }
        server.dispose();
        client.dispose();
    }
}
