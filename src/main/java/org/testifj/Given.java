package org.testifj;

import org.testifj.lang.Procedure;

public final class Given {

    public static <T> GivenContinuance<T> given(final T instance) {
        return new GivenContinuance<T>() {
            @Override
            public void then(Action<T> action) {
                try {
                    action.execute(instance);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }

            @Override
            public WhenContinuance<T> when(Action<T> whenAction) {
                return new WhenContinuance<T>() {
                    @Override
                    public void then(Action<T> thenAction) {
                        try {
                            whenAction.execute(instance);
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }

                        try {
                            thenAction.execute(instance);
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }
                    }

                    @Override
                    public void then(Procedure procedure) {
                        try {
                            whenAction.execute(instance);
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }

                        try {
                            procedure.call();
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }
                    }
                };
            }
        };
    }

    public static interface GivenContinuance<T> {

        WhenContinuance<T> when(Action<T> action);

        void then(Action<T> action);

    }

    public static interface WhenContinuance<T> {

        void then(Action<T> action);

        void then(Procedure procedure);

    }

}
