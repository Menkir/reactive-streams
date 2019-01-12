package rsocket;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import io.netty.buffer.Unpooled;
import io.rsocket.Frame;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


public class Client{
    private RSocket client = null;
    public Flux<Payload> serverEndpoint = null;
   // ClientPublisher clientPublisher = null;
    Client(){
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 1337))
                .start()
                .block();

        //clientPublisher = new ClientPublisher();
        assert client != null;
        this.serverEndpoint = client.requestChannel(Flux.range(1,100)
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

    /*static class ClientPublisher implements Publisher<Payload> {
        @Override
        public void subscribe(Subscriber<? super Payload> s) {
            int secondsToWait = (int)((Math.random()*10)+1);
            int i = -1;
            double waited = 0;
            while(secondsToWait >= waited){
                long before = System.currentTimeMillis();
                ++i;
                s.onNext(DefaultPayload.create(Serialiazer.serialize(new Coordinate(i,i))));
                try {
                    Thread.sleep((long)(Math.random()*1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++i;
                long after = System.currentTimeMillis();
                waited += ((double)(after - before)) / 1000;
            }
            s.onComplete();
        }
    }*/
}
