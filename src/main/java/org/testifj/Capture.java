package org.testifj;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * Class that allows capturing of references.
 * </p>
 * <p>
 * This class is not thread-safe.
 * </p>
 *
 * @param <T> The type of object that should be captured.
 */
public final class Capture<T> {

    private volatile T target;

    private final AtomicBoolean captured = new AtomicBoolean();

    public T get() {
        final T capturedTarget = this.target;

        if (!captured.get()) {
            throw new IllegalStateException("Value has not been captured");
        }

        return capturedTarget;
    }

    public boolean isCaptured() {
        return captured.get();
    }

    public void set(T target) {
        if (!captured.compareAndSet(false, true)) {
            throw new IllegalStateException("Value is already captured");
        }

        this.target = target;
    }
}
