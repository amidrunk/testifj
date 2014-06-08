package org.testifj;

public final class Describe {

    public static void describe(String description, Procedure procedure) {
        assert description != null : "Description can't be null";
        assert procedure != null : "Procedure can't be null";

        try {
            procedure.call();
        } catch (AssertionError e) {
            throw new AssertionError(description + ": " + e.getMessage()); // TODO needs improvement
        } catch (Exception e) {
            throw new AssertionError("Procedure caused exception", e);
        }
    }

}
