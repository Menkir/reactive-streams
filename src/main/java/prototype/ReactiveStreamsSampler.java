package prototype;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import prototype.model.Coordinate;
import prototype.routing.routeImpl.RectangleRoute;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.util.Objects;
import java.util.Scanner;

public class ReactiveStreamsSampler extends AbstractJavaSamplerClient implements Serializable {
    private final Scheduler scheduler = Schedulers.parallel();
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();

        // SEND SOMETHING
        Objects.requireNonNull(RSocketFactory
                .connect()
                .transport(TcpClientTransport.create("192.168.0.199", 1337))
                .start()
                .subscribeOn(scheduler)
                .publishOn(scheduler)
                .block())
                .requestChannel(Flux.just(new RectangleRoute().getRouteAsStream())
                                    .map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
                                    .subscribeOn(scheduler)
                                    .share())
                .subscribe(System.out::println);

        result.setSuccessful(true);
        result.sampleEnd();
        return result;
    }

    public static void main(final String... args){
        Scanner sc = new Scanner(System.in);
        new ReactiveStreamsSampler().runTest(null);

        while(sc.hasNext()){

        }
    }
}
