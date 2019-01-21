package prototype.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.udp.UdpServer;

import java.util.concurrent.Executors;

public final class Server {
    private final int PORT = 1337;
    private final String HOST = "127.0.0.1";

    private final Disposable channel;
    private WorkQueueProcessor <Flux<Payload>> channels;
    public Server() {
        this.channel = RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) ->
                            Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create(HOST, PORT))
                .start()
                .subscribe();
        channels = WorkQueueProcessor.create();
    }

    public void dispose() {
        this.channel.dispose();
    }

    public Flux<Flux<Payload>> getChannels(){
        return channels;
    }

    private class RSocketImpl extends AbstractRSocket {
        Scheduler server = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));
        @Override
        public Flux<Payload> requestChannel(final Publisher<Payload> payloads) {
            Flux<Payload> channel = Flux.from(payloads)
                        .subscribeOn(server)
                        .doOnNext(next -> System.out.println(Thread.currentThread().getName()
                                + " [LOG] received from Client " + Serializer.deserialize(next)))
                        .map(payload -> {
                            Coordinate data = Serializer
                                    .deserialize(payload);
                            data.setSignalPower(getSignalPower());
                            return DefaultPayload
                                    .create(Serializer.serialize(data));
                        })
                        .share();

            channel.subscribe(payload -> {
                progress();
            });

            channels.onNext(channel);
            return channel;
        }

        int getSignalPower() {
            final int maxRange = 10;
            return (int) (Math.random() * maxRange);
        }

        void progress(){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
