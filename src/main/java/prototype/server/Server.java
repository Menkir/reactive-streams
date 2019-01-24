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
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

public final class Server {
    private final int PORT = 1337;
    private final String HOST = "127.0.0.1";
    private final Disposable channel;
    public final Coordinate signalTower = new Coordinate(2,1);
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
                        .publishOn(server)
                        .onBackpressureLatest()
                        /*.doOnNext(next -> {
                            System.out.println(Thread.currentThread().getName()
                                    + " [LOG] received from Client " +payloads.hashCode() + ": " + Serializer.deserialize(next));
                            expensiveComputation();
                        })*/
                        .map(payload -> {
                            Coordinate data = Serializer
                                    .deserialize(payload);
                            data.setSignalPower(getSignalPower(data));
                            return DefaultPayload
                                    .create(Serializer.serialize(data));
                        })
                    //.doOnRequest(req -> System.out.println("requesting " + req))
                        .share();
            channels.onNext(channel);
            return channel;
        }

        int getSignalPower(Coordinate position) {
            return 10 - (int)Math.sqrt(Math.pow(position.get_1() - signalTower.get_1(), 2) + Math.pow(position.get_2() - signalTower.get_2(), 2));
        }

        void expensiveComputation(){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
