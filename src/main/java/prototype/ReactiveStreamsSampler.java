package prototype;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import prototype.model.Coordinate;
import prototype.routing.routeImpl.RectangleRoute;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class ReactiveStreamsSampler extends AbstractJavaSamplerClient implements Serializable {
    private final Scheduler scheduler = Schedulers.parallel();
    private final Payload payload = DefaultPayload.create(Serializer.serialize(new Coordinate(0,0)));
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();

        int elements = javaSamplerContext.getIntParameter("ELEMENTS");
        Mono<RSocket> socket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create("192.168.0.199", 1337))
                .start();

        socket.subscribe(
                rSocket -> rSocket.requestChannel(Flux.interval(Duration.ZERO)
                        .take(elements)
                        .onBackpressureBuffer(elements)
                        .map(number -> DefaultPayload.create(Serializer.serialize(new Coordinate(number.intValue(),number.intValue()))))
                        .subscribeOn(scheduler)
                        .publishOn(scheduler)
                        .doOnError(err -> result.setSuccessful(false))
                        .share()
                ).subscribe()
        );

        result.setSuccessful(true);

        result.sampleEnd();
        return result;
    }

    public static void main(final String... args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        for(int i = 0; i< 100; ++i){
            CompletableFuture.runAsync(() -> {
                new ReactiveStreamsSampler().runTest(null);
            });
        }


        while(sc.hasNext()){

        }
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("ELEMENTS", "1");
        return arguments;
    }
}
