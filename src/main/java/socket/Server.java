package socket;

import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(final String... args) throws IOException {

        Scanner sc  = new Scanner(System.in);
        ServerSocket serverSocket = new ServerSocket(8080);
        Flux<Socket> socketsFlux = Flux.<Socket>create(emitter -> {
            while(true){
                try {
                    emitter.next(serverSocket.accept());
                    System.out.println("[LOG] add Socket");
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).subscribeOn(Schedulers.parallel())
          .share();

        Disposable disposable = socketsFlux.subscribe(socket -> {
            System.out.println("[LOG] receive socket connection successfully");
            Flux.create(emitter ->{
                try {
                    InputStream in = socket.getInputStream();
                    while(in.available() > 0){
                        emitter.next(in.read());
                        //Thread.sleep(200);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).onBackpressureBuffer(2)
              .subscribeOn(Schedulers.parallel())
              .subscribe(data -> System.out.println("[LOG] Client: " + socket.getPort() + " receive " + data));

        });

        while(sc.hasNext()){};
        System.out.println("[LOG] terminated");
        serverSocket.close();
        disposable.dispose();
    }
}
