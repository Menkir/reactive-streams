package rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


public class Client{
    private RSocket client = null;
    private Flux<Payload> serverEndpoint = null;
    Client(){
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("127.0.0.1", 1337))
                .start()
                .block();

        assert client != null;
        this.serverEndpoint = client
                .requestChannel(Flux.range(1,100)
                                    .map(n -> DefaultPayload.create(Serialiazer.serialize(new Coordinate(n,n))))
                                    .doOnEach(each -> {
                                         try {
                                                 Thread.sleep(100);
                                             } catch (InterruptedException e) {
                                                 e.printStackTrace();
                                               }
                                     })
                                    .publish().autoConnect());

        this.serverEndpoint.subscribeOn(Schedulers.newSingle("ServerEndpoint"))
                .share()
                .subscribeOn(Schedulers.immediate())
                .publish()
                .autoConnect()
                .subscribe(payload -> {
                                Coordinate data = Serialiazer.deserialize(payload.getData().array());
                                System.out.println(Thread.currentThread().getName() + " [LOG] received from Server " + data);
                            });

    }

    void dispose(){
        this.client.dispose();
    }

    public Flux<Payload> getServerEndpoint(){return this.serverEndpoint;}

}
