package org.testifj;

import org.testifj.framework.*;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordingTestContextProvider implements TestContextProvider {

    private final LinkedList<Expectation<?>> expectations = new LinkedList<>();

    private final AtomicBoolean enforceExpectations = new AtomicBoolean(true);

    private final TestContextProvider enforcingTestContextProvider = new DefaultTestContextProvider();

    @Override
    public TestContext getTestContext() {
        final Optional<TestContext> targetTestContext = (enforceExpectations.get()
                ? Optional.of(enforcingTestContextProvider.getTestContext())
                : Optional.empty());

        return expectation -> {
            expectations.add(expectation);

            if (targetTestContext.isPresent()) {
                targetTestContext.get().expect(expectation);
            }
        };
    }

    public void enforceExpectations(boolean enforce) {
        this.enforceExpectations.set(enforce);
    }

    public AllExpectations expectations() {
        return new AllExpectations();
    }

    public void reset() {
        expectations.clear();
        enforceExpectations.set(true);
    }

    public class AllExpectations {

        public Optional<Expectation<?>> take() {
            return (expectations.isEmpty() ? Optional.empty() : Optional.of(expectations.remove(0)));
        }
    }
}
