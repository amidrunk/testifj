package org.testifj;

public interface ExpectedExceptionNotThrown extends ExpectationFailure {

    Class<? extends Throwable> getExpectedException();

}
