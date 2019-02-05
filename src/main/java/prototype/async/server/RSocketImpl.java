package prototype.endpoints.reactiveServerImpl;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import org.reactivestreams.Publisher;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class RSocketImpl extends AbstractRSocket {
	private Scheduler server = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));

	private final WorkQueueProcessor<Flux<Payload>> channels;

	RSocketImpl(){
		channels = WorkQueueProcessor.create();
	}

	WorkQueueProcessor<Flux<Payload>> getChannels() {
		return channels;
	}

	public void shutdownScheduler(){
		server.dispose();
	}
	@Override
	public Flux<Payload> requestChannel(final Publisher<Payload> payloads) {
		Flux<Payload> share = Flux.from(payloads)
				.subscribeOn(server)
				.publishOn(server)
				//.doOnNext((e) -> System.out.println("Ping " + Serializer.deserialize(e)))
				.share();
		channels.onNext(share);
		return share;
	}
}
