package org.testifj;

import io.recode.annotations.DSL;
import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.MapThatIs;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MapExpectations<K, V> extends ExpectationsBase<Map<K, V>, MapExpectations<K, V>> {

    public MapExpectations(ExpectationContext<Map<K, V>> context) {
        super(context);
    }

    public void toBeEmpty() {
        reportValueExpectation(MapThatIs.empty());
    }

    @SuppressWarnings("unchecked")
    public void toHaveSize(int size) {
        final Optional expectedValue = Optional.of(size);
        reportValueExpectation(MapThatIs.ofSize(size), expectedValue);
    }

    @SuppressWarnings("unchecked")
    public ToContainContinuation<K, V> toContainKey(K key) {
        final Optional expectedKey = Optional.of(key);
        reportValueExpectation(m -> m != null && m.containsKey(key), expectedKey);

        return v -> {
            final Optional expectedValue = Optional.of(v);
            // TODO: Doesn't work to provide an expected value. Need to fix that...
            reportValueExpectation(m -> m != null && Objects.equals(m.get(key), v), Optional.empty());
        };

    }

    public interface ToContainContinuation<K, V> {

        void withValue(V value);

    }

    @Override
    protected MapExpectations<K, V> newInstance(ExpectationContext<Map<K, V>> context) {
        return new MapExpectations<K, V>(context);
    }
}
