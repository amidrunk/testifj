package org.testifj.framework;

import java.util.Optional;

public interface CastSuffix<T> {

    @SuppressWarnings("unchecked")
    default <S extends T> Optional<S> as(Class<S> type) {
        assert type != null : "type can't be null";

        if (!type.isInstance(this)) {
            return Optional.empty();
        }

        return Optional.of((S) this);
    }
}
