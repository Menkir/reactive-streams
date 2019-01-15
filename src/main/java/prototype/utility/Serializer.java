package prototype.utility;

import prototype.model.Coordinate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

public final class Serializer {
    private Serializer() {
        throw new AssertionError();
    }
    public static byte[] serialize(final Object object) {
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

    public static Coordinate deserialize(final byte[] bytes) {
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
