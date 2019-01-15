package prototype.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public final class Server {
    private final Disposable channel;

    public Server() {
        final int port = 1337;
        this.channel = RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) ->
                        Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create("127.0.0.1", port))
                .start()
                .subscribe();
    }

    public void dispose() {
        this.channel.dispose();
    }

    private class RSocketImpl extends AbstractRSocket {
        @Override
        public Flux<Payload> requestChannel(final Publisher<Payload> payloads) {
             return Flux.from(payloads)
                        .subscribeOn(Schedulers.parallel())
                        .map(payload -> {
                            Coordinate data = Serializer
                                    .deserialize(payload.getData().array());
                            System.out.println(Thread.currentThread().getName()
                                    + " [LOG] received from Client " + data);
                            data.setSignalPower(getSignalPower());
                            return DefaultPayload
                                    .create(Serializer.serialize(data));
                        });
        }

        int getSignalPower() {
            final int maxRange = 10;
            return (int) (Math.random() * maxRange);
        }
    }
}
