package org.testifj.delegate;

import org.junit.Test;
import org.testifj.Description;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.OptionalThatIs.present;
import static org.testifj.matchers.core.StringShould.containString;

@SuppressWarnings("unchecked")
public class ExpectationVerificationTest {

    private final ExpectationVerificationContext expectationVerificationContext = mock(ExpectationVerificationContext.class);
    private final Description description = mock(Description.class);
    private final ExpectationVerification compliantVerification = ExpectationVerification.compliant(expectationVerificationContext, description);
    private final Description verificationFailureDescription = mock(Description.class);
    private final ExpectationVerification inCompliantVerification = ExpectationVerification.notCompliant(expectationVerificationContext, description, verificationFailureDescription);

    @Test
    public void createCompliantVerificationShouldNotAcceptInvalidParameters() {
        expect(() -> ExpectationVerification.compliant(null, mock(Description.class))).toThrow(AssertionError.class);
        expect(() -> ExpectationVerification.compliant(mock(ExpectationVerificationContext.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void createNotCompliantVerificationShouldNotAcceptInvalidParameters() {
        expect(() -> ExpectationVerification.notCompliant(null, mock(Description.class), mock(Description.class)));
        expect(() -> ExpectationVerification.notCompliant(mock(ExpectationVerificationContext.class), null, mock(Description.class)));
        expect(() -> ExpectationVerification.notCompliant(mock(ExpectationVerificationContext.class), mock(Description.class), null));
    }

    @Test
    public void compliantVerificationShouldContainExpectationProperties() {
        given(compliantVerification).then(it -> {
            expect(it.getExpectation()).toBe(expectationVerificationContext);
            expect(it.getExpectationDescription()).toBe(description);
            expect(it.isCompliant()).toBe(true);
            expect(it.getVerificationFailureDescription()).not().toBe(present());
        });
    }

    @Test
    public void notCompliantVerificationShouldContainExpectationAndVerificationDescription() {
        given(inCompliantVerification).then(it -> {
            expect(it.getExpectation()).toBe(expectationVerificationContext);
            expect(it.getExpectationDescription()).toBe(description);
            expect(it.isCompliant()).toBe(false);
            expect(it.getVerificationFailureDescription()).toBe(present());
            expect(it.getVerificationFailureDescription().get()).toBe(verificationFailureDescription);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(compliantVerification).toBe(equalTo(compliantVerification));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(compliantVerification).not().toBe(equalTo(null));
        expect((Object) compliantVerification).not().toBe(equalTo("foo"));
    }

    @Test
    public void compliantInstancesWithEqualPropertiesShouldBeEqual() {
        final ExpectationVerification other = ExpectationVerification.compliant(expectationVerificationContext, description);

        expect(compliantVerification).toBe(equalTo(other));
        expect(compliantVerification.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void inCompliantInstancesWithEqualPropertiesShouldBeEqual() {
        final ExpectationVerification other = ExpectationVerification.notCompliant(expectationVerificationContext, description, verificationFailureDescription);

        expect(inCompliantVerification).toBe(equalTo(other));
        expect(inCompliantVerification.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void compliantAndInCompliantInstancesShouldNotBeEqual() {
        expect(compliantVerification).not().toBe(equalTo(inCompliantVerification));
        expect(compliantVerification.hashCode()).not().toBe(equalTo(inCompliantVerification.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(inCompliantVerification.toString()).then(it -> {
            expect(it).to(containString(expectationVerificationContext.toString()));
            expect(it).to(containString(description.toString()));
            expect(it).to(containString(verificationFailureDescription.toString()));
        });
    }

}
