package org.testifj;

import org.hamcrest.*;
import org.hamcrest.Description;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;

public final class MockitoMatchers {

    private static MockingProgress mockingProgress = new ThreadSafeMockingProgress();

    @SuppressWarnings("unchecked")
    public static <T> T argThat(Predicate<T> predicate) {
        mockingProgress.getArgumentMatcherStorage().reportMatcher(new BaseMatcher() {
            @Override
            public boolean matches(Object item) {
                return predicate.test((T) item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("TOOD: Decompile predicate");
            }
        });

        return null;
    }

}
