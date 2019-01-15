package prototype.server;

import io.rsocket.*;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import prototype.model.Coordinate;
import prototype.utility.Serialiazer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Server {
    final private Disposable channel;
    public Server(){
        this.channel = RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create("127.0.0.1", 1337))
                .start()
                .subscribe();
    }

    public void dispose(){
        this.channel.dispose();
    }

    private class RSocketImpl extends AbstractRSocket {
        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
             return Flux.from(payloads)
                        .subscribeOn(Schedulers.parallel())
                        .map(payload -> {
                            Coordinate data = Serialiazer.deserialize(payload.getData().array());
                            System.out.println(Thread.currentThread().getName() + " [LOG] received from Client " + data);
                            data.setSignalPower(getSignalPower());
                            return DefaultPayload.create(Serialiazer.serialize(data));
                        });
        }

        int getSignalPower(){
            return (int) (Math.random() * 10);
        }
    }
}
