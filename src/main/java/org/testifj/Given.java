package org.testifj;

public final class Given {

    public static <T> GivenContinuance<T> given(final T instance) {
        return new GivenContinuance<T>() {
            @Override
            public WhenContinuance<T> when(Action<T> action) {
                return new WhenContinuance<T>() {
                    @Override
                    public void then(Predicate<T> predicate) {
                        action.execute(instance);

                        assert predicate.test(instance) : predicate.describe();
                    }
                };
            }
        };
    }

    public static interface GivenContinuance<T> {

        WhenContinuance<T> when(Action<T> action);

    }

    public static interface WhenContinuance<T> {

        void then(Predicate<T> predicate);

    }

}
