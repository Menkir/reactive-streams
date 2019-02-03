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

public class RSocketImpl extends AbstractRSocket {
	private Scheduler server = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));
    private long before, after = 0;
    ArrayList<Long> clientServedTimes = new ArrayList<>();
	private final WorkQueueProcessor<Flux<Payload>> channels;

	RSocketImpl(){
		channels = WorkQueueProcessor.create();
	}

	WorkQueueProcessor<Flux<Payload>> getChannels() {
		return channels;
	}

	@Override
	public Flux<Payload> requestChannel(final Publisher<Payload> payloads) {
		Flux<Payload> channel = Flux.from(payloads)
				.subscribeOn(server)
				.publishOn(server)
				//.onBackpressureLatest()
				/*.doOnNext(next -> {
					System.out.println(Thread.currentThread().getName()
							+ " [LOG] received from Car " +payloads.hashCode() + ": " + Serializer.deserialize(next));
				})*/
                .doOnSubscribe(sub -> before = System.currentTimeMillis())
                .doOnComplete(() -> {
                    after = System.currentTimeMillis();
                    System.out.println((after - before));
                    clientServedTimes.add((after - before));
                })
				.share();
		//channels.onNext(channel);
		return channel;
	}
}
