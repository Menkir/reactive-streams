package prototype;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import prototype.model.Coordinate;
import prototype.routing.routeImpl.RectangleRoute;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

public class JavaSocketSampler extends AbstractJavaSamplerClient implements Serializable {
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        int elements = javaSamplerContext.getIntParameter("ELEMENTS");
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("192.168.0.199", 1337));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            for(int i = 0; i< elements; ++i){
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(new Coordinate(i,i));
                oos.flush();

                ois = new ObjectInputStream(socket.getInputStream());
                Coordinate c = (Coordinate) ois.readObject();
            }
            oos.close();
            socket.close();
            result.setSuccessful(true);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            result.setSuccessful(false);
        }

        result.sampleEnd();
        return result;
    }

    public static void main(final String... args){
        new JavaSocketSampler().runTest(null);
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("ELEMENTS", "1");
        return arguments;
    }
}
