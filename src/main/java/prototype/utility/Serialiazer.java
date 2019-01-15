package prototype.utility;

import prototype.model.Coordinate;

import java.io.*;

public class Serialiazer {
    public static byte[] serialize(Object object){
        byte[] yourBytes = {};
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out;
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            yourBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ignore close exception
        return yourBytes;
    }

    public static Coordinate deserialize(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Object o = null;
        try (ObjectInput in = new ObjectInputStream(bis)) {
            o = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // ignore close exception
        return (Coordinate) o;
    }
}
