package prototype.app;

import java.util.Scanner;

import prototype.client.Client;
import prototype.server.Server;
import prototype.view.Monitor;

class App {

    public static void main(final String... args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Server server = new Server();
        new Monitor(server);

        for(int i = 0; i< 1; ++i){
            Thread.sleep(100);
            new Client();
        }

        System.out.println("Press CTRL+D to terminate Application");
        while (scanner.hasNext()) {

        }
        server.dispose();
    }
}
