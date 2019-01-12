package socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    public static void main(final String... args) throws IOException {
        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(8080));
        if(clientSocket.isConnected()){
            System.out.println("[LOG] Connection established");
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            for(int i = 0; i < 100_000; ++i){
                System.out.println("[LOG] write " + i);
                out.write(i);
            }
        } else{
            System.out.println("[LOG] Connection failed");
        }
        clientSocket.close();
    }
}
