package org.testifj;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
public final class Matching {

    public static MatchContinuance match(Object instance) {
        final AtomicBoolean executed = new AtomicBoolean();

        return new MatchContinuance() {
            @Override
            public <T> WhenContinuance<T> when(Matcher<T> matcher) {
                assert matcher != null : "Matcher can't be null";

                final MatchContinuance matchContinuance = this;

                return action -> {
                    assert action != null : "Action can't be null";

                    if (!executed.get()) {
                        if (((Matcher) matcher).matches(instance)) {
                            executed.set(true);
                            ((Action) action).execute(instance);
                        }
                    }

                    return matchContinuance;
                };
            }
        };
    }

    public interface MatchContinuance {

        <T> WhenContinuance<T> when(Matcher<T> matcher);

    }

    @FunctionalInterface
    public interface WhenContinuance<T> {

        MatchContinuance then(Action<T> action);

    }

}
