package prototype.async.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import org.reactivestreams.Publisher;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class RSocketImpl extends AbstractRSocket {
	private final ExecutorService executorService = Executors.newFixedThreadPool(8);
	private final Scheduler server = Schedulers.fromExecutor(executorService);

	private final WorkQueueProcessor<Flux<Payload>> channels;

	RSocketImpl(){
		channels = WorkQueueProcessor.create();
	}

	WorkQueueProcessor<Flux<Payload>> getChannels() {
		return channels;
	}

	@Override
	public Flux<Payload> requestChannel(final Publisher<Payload> payloads) {
		return Flux.from(payloads)
				.doOnNext(next -> simulateWork());
	}

	/**
	 * Simulate Server processing time by sleeping 100ms
	 */
	private void simulateWork(){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
