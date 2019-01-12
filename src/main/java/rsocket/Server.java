package rsocket;

import io.netty.buffer.Unpooled;
import io.rsocket.*;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

class Server {
    private CloseableChannel channel = null;
    Server(){
        this.channel = RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create("192.168.0.199", 1337))
                .start()
                .block();

    }

    void dispose(){
        this.channel.dispose();
    }

    private class RSocketImpl extends AbstractRSocket {
        private Flux<Payload> clientEndpoint = null;
        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            this.clientEndpoint = Flux.from(payloads)
                                        .share()
                                        .subscribeOn(Schedulers.newSingle("ClientEndpoint"))
                                        .publish()
                                        .autoConnect();

            this.clientEndpoint.subscribe(payload -> System.out.println(Thread.currentThread().getName() + " [LOG] received from Client " + Serialiazer.deserialize(payload.getData().array())));
            return Flux.from(this.clientEndpoint)
                    .map(payload -> {
                        Coordinate data = Serialiazer.deserialize(payload.getData().array());
                        data.setSignalPower(getSignalPower());
                        return DefaultPayload.create(Serialiazer.serialize(data));
                    });
        }

        int getSignalPower(){
            return (int) (Math.random() * 10);
        }
    }
}
