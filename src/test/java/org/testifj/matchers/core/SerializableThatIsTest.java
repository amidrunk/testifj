package org.testifj.matchers.core;

import org.junit.Test;
import org.testifj.Matcher;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

public class SerializableThatIsTest {

    @Test
    public void identitySerializableShouldMatchValidSerializable() {
        expect(SerializableThatIs.identitySerializable().matches("foo")).toBe(true);
    }

    @Test
    public void identitySerializableShouldNotMatchInvalidSerializableType() {
        expect(SerializableThatIs.identitySerializable().matches(new NonSerializableClass("foo"))).toBe(false);
    }

    private static final class NonSerializableClass implements Serializable {

        private final String string;

        public NonSerializableClass(String string) {
            this.string = string;
        }

        private void writeObject(java.io.ObjectOutputStream out)
                throws IOException {

        }

        private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NonSerializableClass that = (NonSerializableClass) o;

            if (string != null ? !string.equals(that.string) : that.string != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return string != null ? string.hashCode() : 0;
        }
    }

}