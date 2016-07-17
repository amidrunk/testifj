package org.testifj.integrationtest;

import oracle.jvm.hotspot.jfr.Producer;
import org.junit.After;
import org.testifj.Procedure;
import org.testifj.framework.DefaultTestContextProvider;
import org.testifj.framework.TestContextProviders;

public abstract class AbstractIntegrationTestBase {

    private final TestContextProviders.TestContextProviderOverride providerOverride = TestContextProviders.overrideTestContextProvider(new DefaultTestContextProvider());

    @After
    public void tearDown() {
        providerOverride.restore();
    }

    protected void expectAssertionError(Procedure procedure, String subString) {
        expectException(procedure, AssertionError.class, subString);
    }

    protected void expectException(Procedure procedure, Class<? extends Throwable> exceptionType, String subString) {
        boolean failed = false;

        try {
            procedure.call();
        } catch (Throwable e) {
            if (!exceptionType.isInstance(e)) {
                throw new AssertionError("expected exception of type " + exceptionType.getSimpleName() + ", actually got " + e.getClass().getSimpleName(), e);
            }

            if (!e.getMessage().contains(subString)) {
                throw new AssertionError("expected exception message \"" + e.getMessage() + "\" to contain \"" + subString + "\"");
            }

            failed = true;
        }

        if (!failed) {
            throw new AssertionError("expected exception of type " + exceptionType.getSimpleName());
        }
    }

    protected void expectNoException(Procedure procedure) {
        try {
            procedure.call();
        } catch (Exception e) {
            throw new AssertionError("Expected no exception, but got " + e.getClass().getSimpleName(), e);
        }
    }
}
