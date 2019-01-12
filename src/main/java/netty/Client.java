package netty;

import com.sun.media.jfxmedia.logging.Logger;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.PendingWriteQueue;
import io.netty.channel.nio.NioEventLoopGroup;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.function.BiFunction;

public class Client {

    private final int port = 1337;
    private static Connection client = null;
    private final LoopResources clientresources = LoopResources.create("netty.Client");
    private static Scanner sc = new Scanner(System.in);
    public Client() throws UnknownHostException {
       // Hooks.onOperatorDebug();
        client = TcpClient.create()
                .port(port)
                .host(Inet4Address.getLocalHost().getHostAddress())
                .runOn(new NioEventLoopGroup(4))
                .handle((in, out) -> out.send(Flux.just(Unpooled.copyInt(42), Unpooled.copyInt(24))).then().log())
                .wiretap(true)
                .connectNow();

    }

    public static void main(final String... args) throws Exception {
        new Client();
        while(sc.hasNext()){}
        System.out.println("netty.Client Thread is terminated.");
        client.disposeNow();
    }
}
