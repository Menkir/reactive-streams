package prototype.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Executors;


public final class Client {
    private final RSocket client;
    private final Flux<Payload> serverEndpoint;
    public enum ClientConfiguration{
    	RECTANGLE;
    }
    public Client() {
        final int port = 1337;
        Scheduler clientScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(8));
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("127.0.0.1", port))
                .start().subscribeOn(Schedulers.parallel()).block();
        assert client != null;
        serverEndpoint = client.requestChannel(
        		        Flux.interval(Duration.ofMillis(100))
				        .subscribeOn(clientScheduler)
		                .take(10)
		                .map(number -> getRectangleRoute())
				        .flatMap(flux-> flux)
				        .map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
				        .publishOn(clientScheduler)
				        .share()
        );
        serverEndpoint.publishOn(clientScheduler).subscribe(payload -> {
            Coordinate data = Serializer
                                .deserialize(payload.getData().array());
            System.out.println(getTabs() + Thread.currentThread().getName()
                    + " [LOG] received from Server " + data);
        });
    }

    private String getTabs(){
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i< 20; ++i)
    		sb.append('\t');
    	return sb.toString();
	}

    private void working() {
        final int time = 40;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        this.client.dispose();
    }

    public Flux<Payload> getServerEndpoint() {
        return this.serverEndpoint;
    }

    private Flux<Coordinate> getRectangleRoute(){
	    Coordinate[] north = new Coordinate[10];
	    Coordinate[] east = new Coordinate[10];
	    Stack<Coordinate> south = new Stack<>();
	    Stack<Coordinate> west =  new Stack<>();
	    for(int i = 0 ; i < 10; i++){
	    	north[i] = new Coordinate(i,0);
	    	east[i] = new Coordinate(9, i);
	    }

	    for(int i = 9 ; i >= 0; i--){
		    south.push(new Coordinate(i,9));
		    west.push(new Coordinate(0, i));
	    }

	    return Flux.fromArray(north)
			    .concatWith(Flux.fromArray(east))
			    .concatWith(Flux.fromStream(south.stream()))
			    .concatWith(Flux.fromStream(west.stream()));
    }
}
