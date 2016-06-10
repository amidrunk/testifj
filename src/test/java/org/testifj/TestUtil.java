package org.testifj;

public class TestUtil {

    public interface ThrowingCommand {

        void call() throws Throwable;
    }

    public static void assertThrown(ThrowingCommand command, Class<? extends Throwable> exceptionType) {
        boolean failed = false;

        try {
            command.call();
        } catch (Throwable throwable) {
            if (!exceptionType.isInstance(throwable)) {
                throw new AssertionError("Expected command to throw " + exceptionType.getSimpleName() + ", actually threw " + throwable.getClass().getSimpleName());
            }

            failed = true;
        }

        if (!failed) {
            throw new AssertionError("Expected command to fail with exception " + exceptionType.getSimpleName());
        }
    }
}
