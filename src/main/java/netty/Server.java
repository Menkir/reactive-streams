package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

@SuppressWarnings("unchecked")
public class Server {

    private final int port = 1337;
    private final String address = Inet4Address.getLocalHost().getHostAddress();
    private static DisposableServer server = null;
    private LoopResources serverResources = LoopResources.create("netty.Server");
    private static Scanner sc = new Scanner(System.in);
    public Server() throws UnknownHostException {
        Hooks.onOperatorDebug();
        server = TcpServer.create()
                .port(port)
                .host(address)
                .runOn(new NioEventLoopGroup(4))
                .handle((in, out) -> in.receive()
                                       .doOnNext(data -> {
                                            ByteBuf buf = (ByteBuf) data;
                                            int number  = buf.getInt(0);
                                            System.out.println("receive: " + number);
                }).then())
                .bindNow();
    }

    public static void main(final String... args) throws UnknownHostException, InterruptedException {
        new Server();
        while(sc.hasNext()){}
        System.out.println("netty.Server Thread is terminated.");
        server.disposeNow();
    }

}
