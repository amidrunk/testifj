package org.testifj.framework;

// Should probably be a "test burst" with matchers being enlisted into a context.
public interface TestContext {

    void expect(Expectation expectation);

}
