package org.testifj.framework;

import io.recode.CodeLocation;

import java.util.Arrays;
import java.util.Optional;

public class InlineExpectationReference implements ExpectationReference {

    private final StackTraceElement[] stackTrace;

    private final int stackTraceOffset;

    private final int lineOffset;

    private InlineExpectationReference(StackTraceElement[] stackTrace, int stackTraceOffset, int lineOffset) {
        this.stackTrace = stackTrace;
        this.stackTraceOffset = stackTraceOffset;
        this.lineOffset = lineOffset;
    }

    public String getClassName() {
        return stackTrace[stackTraceOffset].getClassName();
    }

    public String getMethodName() {
        return stackTrace[stackTraceOffset].getMethodName();
    }

    public String getFileName() {
        return stackTrace[stackTraceOffset].getFileName();
    }

    public int getLineNumber() {
        return stackTrace[stackTraceOffset].getLineNumber() + lineOffset;
    }

    public CodeLocation toCodeLocation() {
        return new CodeLocation() {
            @Override
            public String getClassName() {
                return InlineExpectationReference.this.getClassName();
            }

            @Override
            public String getMethodName() {
                return InlineExpectationReference.this.getMethodName();
            }

            @Override
            public String getFileName() {
                return InlineExpectationReference.this.getFileName();
            }

            @Override
            public int getLineNumber() {
                return InlineExpectationReference.this.getLineNumber();
            }

            @Override
            public Optional<CodeLocation> getCaller() {
                if (stackTraceOffset < stackTrace.length - 1) {
                    return Optional.of(create(stackTrace, stackTraceOffset + 1).toCodeLocation());
                }

                return Optional.empty();
            }

            @Override
            public String toString() {
                return "InlineExpectationReference::codeLocation { className=" + getClassName() + ", methodName=" + getMethodName() + ", lineNumber=" + getLineNumber() + ", fileName=" + getFileName() + " }";
            }
        };
    }

    public static InlineExpectationReference create(StackTraceElement[] stackTrace, int stackTraceOffset) {
        return create(stackTrace, stackTraceOffset, 0);
    }

    public static InlineExpectationReference create(StackTraceElement[] stackTrace, int stackTraceOffset, int lineOffset) {
        assert stackTrace != null : "stackTrace can't be null";
        assert stackTraceOffset > 0 && stackTraceOffset < stackTrace.length : "stackTraceOffset must reference a valid stack trace element";

        return new InlineExpectationReference(stackTrace, stackTraceOffset, lineOffset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InlineExpectationReference that = (InlineExpectationReference) o;

        if (stackTraceOffset != that.stackTraceOffset) return false;
        if (lineOffset != that.lineOffset) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(stackTrace, that.stackTrace);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(stackTrace);
        result = 31 * result + stackTraceOffset;
        result = 31 * result + lineOffset;
        return result;
    }

    @Override
    public String toString() {
        return "InlineExpectationReference{" +
                "stackTrace=" + Arrays.toString(stackTrace) +
                ", stackTraceOffset=" + stackTraceOffset +
                ", lineOffset=" + lineOffset +
                '}';
    }
}
