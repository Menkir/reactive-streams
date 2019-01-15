package prototype.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.model.Coordinate;
import prototype.utility.Serialiazer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


public class Client{
    final private RSocket client;
    final private Flux<Payload> serverEndpoint;
    public Client() {
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("127.0.0.1", 1337))
                .start()
                .block();

        assert client != null;
        serverEndpoint = client.requestChannel(Flux.range(1,100)
                        .subscribeOn(Schedulers.parallel())
                        .map(n -> {
                            working();
                            return DefaultPayload.create(Serialiazer.serialize(new Coordinate(n,n)));
                        }))
                    .share()
                    .subscribeOn(Schedulers.parallel());

        serverEndpoint.subscribe(payload -> {
            Coordinate data = Serialiazer.deserialize(payload.getData().array());
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + Thread.currentThread().getName() + " [LOG] received from Server " + data);
        });
    }

    private void working(){
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispose(){
        this.client.dispose();
    }

    public Flux<Payload> getServerEndpoint(){return this.serverEndpoint;}

}
