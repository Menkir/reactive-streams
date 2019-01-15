package prototype.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


public final class Client {
    private final RSocket client;
    private final Flux<Payload> serverEndpoint;
    public Client() {
        final int port = 1337;
        final int from = 1;
        final int to = 100;
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("127.0.0.1", port))
                .start()
                .block();

        assert client != null;
        serverEndpoint = client.requestChannel(Flux.range(from, to)
                        .subscribeOn(Schedulers.parallel())
                        .map(n -> {
                            working();
                            return DefaultPayload
                                    .create(Serializer
                                            .serialize(new Coordinate(n, n)));
                        }))
                    .share()
                    .subscribeOn(Schedulers.parallel());

        serverEndpoint.subscribe(payload -> {
            Coordinate data = Serializer
                                .deserialize(payload.getData().array());
            System.out.println(Thread.currentThread().getName()
                    + " [LOG] received from Server " + data);
        });
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

}
