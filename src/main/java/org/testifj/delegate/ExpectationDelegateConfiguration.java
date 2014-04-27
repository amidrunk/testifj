package org.testifj.delegate;

import org.testifj.Predicate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ExpectationDelegateConfiguration {

    private final List<MatchedExpectationDelegateExtension> extensions;

    private ExpectationDelegateConfiguration(List<MatchedExpectationDelegateExtension> extensions) {
        this.extensions = extensions;
    }

    public <T extends Expectation> ExpectationDelegateExtension<T> getExtension(ExpectationVerificationContext<T> expectation) {
        assert expectation != null : "Expectation can't be null";

        return extensions.stream()
                .filter(e -> e.getPredicate().test(expectation))
                .map(MatchedExpectationDelegateExtension::getExtension)
                .findFirst()
                .orElse(null);
    }

    public static final class Builder {

        private final List<MatchedExpectationDelegateExtension> extensions = new ArrayList<>();

        public <T extends Expectation> OnContinuation<T> on(Predicate<ExpectationVerificationContext<T>> expectationPredicate) {
            assert expectationPredicate != null : "Expectation predicate can't be null";

            return extension -> {
                assert extension != null : "Extension can't be null";

                extensions.add(new MatchedExpectationDelegateExtension(expectationPredicate, extension));

                return Builder.this;
            };
        }

        public ExpectationDelegateConfiguration build() {
            return new ExpectationDelegateConfiguration(extensions);
        }

        public interface OnContinuation<T extends Expectation> {

            Builder then(ExpectationDelegateExtension<T> extension);

        }
    }

    private final static class MatchedExpectationDelegateExtension {

        private final Predicate predicate;

        private final ExpectationDelegateExtension extension;

        private MatchedExpectationDelegateExtension(Predicate predicate,
                                                    ExpectationDelegateExtension extension) {
            this.predicate = predicate;
            this.extension = extension;
        }

        public Predicate getPredicate() {
            return predicate;
        }

        public ExpectationDelegateExtension getExtension() {
            return extension;
        }
    }

}
