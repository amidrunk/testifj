package org.testifj.framework;

public class NullableOptional<T> {

    private final T value;

    private final boolean present;

    private NullableOptional(T value, boolean present) {
        this.value = value;
        this.present = present;
    }

    public boolean isPresent() {
        return present;
    }

    public T get() {
        if (!present) {
            throw new IllegalStateException("value not present");
        }

        return value;
    }

    public static <T> NullableOptional<T> empty() {
        return new NullableOptional<T>(null, false);
    }

    public static <T> NullableOptional<T> of(T instance) {
        return new NullableOptional<T>(instance, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NullableOptional<?> that = (NullableOptional<?>) o;

        if (present != that.present) return false;
        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (present ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NullableOptional{" +
                "value=" + value +
                ", present=" + present +
                '}';
    }
}
