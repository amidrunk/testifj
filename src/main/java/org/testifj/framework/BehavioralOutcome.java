package org.testifj.framework;

import java.util.Optional;

public class BehavioralOutcome<T> {

    private final T result;

    private final boolean exceptional;

    private final boolean hasResult;

    public BehavioralOutcome(T result, boolean exceptional, boolean hasResult) {
        this.result = result;
        this.exceptional = exceptional;
        this.hasResult = hasResult;
    }

    public Optional<T> getResult() {
        return (hasResult ? Optional.of(result) : Optional.empty());
    }

    public boolean isExceptional() {
        return exceptional;
    }

    public static<T extends Throwable> BehavioralOutcome<T> exceptional(T result) {
        assert result != null : "result can't be null";

        return new BehavioralOutcome<>(result, true, true);
    }

    public static<T> BehavioralOutcome<T> successful(T result) {
        return new BehavioralOutcome<T>(result, false, true);
    }

    public static BehavioralOutcome<Void> successful() {
        return new BehavioralOutcome<Void>(null, false, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BehavioralOutcome<?> that = (BehavioralOutcome<?>) o;

        if (exceptional != that.exceptional) return false;
        if (hasResult != that.hasResult) return false;
        return result != null ? result.equals(that.result) : that.result == null;

    }

    @Override
    public int hashCode() {
        int result1 = result != null ? result.hashCode() : 0;
        result1 = 31 * result1 + (exceptional ? 1 : 0);
        result1 = 31 * result1 + (hasResult ? 1 : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "BehavioralOutcome{" +
                "result=" + result +
                ", exceptional=" + exceptional +
                ", hasResult=" + hasResult +
                '}';
    }
}
