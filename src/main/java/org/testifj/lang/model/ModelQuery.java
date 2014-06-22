package org.testifj.lang.model;

import java.util.Optional;
import java.util.function.Predicate;

public interface ModelQuery<S, R> {

    Optional<R> from(S from);

    @SuppressWarnings("unchecked")
    default Predicate<S> is(Predicate<? extends R> predicate) {
        assert predicate != null : "Predicate can't be null";

        return source -> {
            final Optional<R> result = ModelQuery.this.from(source);

            if (!result.isPresent()) {
                return false;
            }

            return ((Predicate) predicate).test(result.get());
        };
    }

    @SuppressWarnings("unchecked")
    default <E> ModelQuery<S, E> get(ModelQuery<? extends R, E> modelQuery) {
        assert modelQuery != null : "Model query can't be null";

        return source -> {
            final Optional<R> intermediateResult = ModelQuery.this.from(source);

            if (!intermediateResult.isPresent()) {
                return Optional.empty();
            }

            return ((ModelQuery) modelQuery).from(intermediateResult.get());
        };
    }

    interface WhereContinuation<S, R> extends ModelQuery<S, R> {

        default WhereContinuation<S, R> and(Predicate<R> predicate) {
            return where(predicate);
        }

    }

    default WhereContinuation<S, R> where(Predicate<R> predicate) {
        assert predicate != null : "Predicate can't be null";

        return from -> {
            final Optional<R> result = ModelQuery.this.from(from);

            if (!result.isPresent()) {
                return result;
            }

            if (!predicate.test(result.get())) {
                return Optional.empty();
            }

            return result;
        };
    }

    @SuppressWarnings("unchecked")
    default <T> ModelQuery<S, T> as(Class<T> type) {
        assert type != null : "Type can't be null";

        return from -> {
            final Optional<R> result = ModelQuery.this.from(from);

            if (!result.isPresent()) {
                return Optional.empty();
            }

            final R resultValue = result.get();

            if (!type.isInstance(resultValue)) {
                return Optional.empty();
            }

            return Optional.of((T) resultValue);
        };
    }

}
