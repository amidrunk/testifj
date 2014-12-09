package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.io.*;
import java.util.Objects;

public final class SerializableThatIs {

    public static <T extends Serializable> Matcher<T> identitySerializable() {
        return instance -> {
            try {
                final ByteArrayOutputStream bout = new ByteArrayOutputStream();
                final ObjectOutputStream oout = new ObjectOutputStream(bout);

                oout.writeObject(instance);
                oout.flush();

                final ByteArrayInputStream bais = new ByteArrayInputStream(bout.toByteArray());
                final ObjectInputStream ois = new ObjectInputStream(bais);

                final Object copy = ois.readObject();

                return Objects.equals(instance, copy);
            } catch (IOException | ClassNotFoundException e) {
                return false;
            }
        };
    }

}
