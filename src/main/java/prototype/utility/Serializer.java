package prototype.utility;

import io.rsocket.Payload;
import prototype.model.Measurement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

public final class Serializer {
    /**
     * This Utility Class is responsible for Serializing and Deserializing of the Measurement Class.
     * The Reason for the need is that RSocket only accepts Payload Types. In order to achieve this requirement, the Measurement Class is transformed into Byte array to create a Payload out of the DefaultPayload.create() Method.
     */
    private Serializer() {
        throw new AssertionError();
    }

	/**
	 * Serialize Measurement to Byte Array.
	 * @param measurement An Object of Type Measurement
	 * @return An Array of Bytes
	 */
	public static byte[] serialize(final Measurement measurement) {
        byte[] yourBytes = {};
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out;
            out = new ObjectOutputStream(bos);
            out.writeObject(measurement);
            out.flush();
            yourBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ignore close exception
        return yourBytes;
    }

	/**
	 * Deserialize Payload to Measurement
	 * @param payload An Object that contains RSocket specific data with Measurement byte Information included.
	 * @return A Instance of Measurement
	 */
	public static Measurement deserialize(final Payload payload) {
        ByteArrayInputStream bis = new ByteArrayInputStream(payload.getData().array());
        Object o = null;
        try (ObjectInput in = new ObjectInputStream(bis)) {
            o = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // ignore close exception
        return (Measurement) o;
    }
}
