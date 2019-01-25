package prototype.endpoints.serverImpl;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import org.reactivestreams.Publisher;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

public class RSocketImpl extends AbstractRSocket {
	Scheduler server = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));

	public Flux<Payload> requestChannel(final Publisher<Payload> payloads) {
		Flux<Payload> channel = Flux.from(payloads)
				.subscribeOn(server)
				.publishOn(server)
				.onBackpressureLatest()
				.doOnNext(next -> {
					System.out.println(Thread.currentThread().getName()
							+ " [LOG] received from Car " +payloads.hashCode() + ": " + Serializer.deserialize(next));
				})
				//.doOnRequest(req -> System.out.println("requesting " + req))
				.share();
		//channels.onNext(channel);
		return channel;
	}
}
