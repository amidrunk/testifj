package org.testifj;

import java.util.List;

public interface ExpectationFailure {

    List<StackTraceElement> getCallStack();

}
