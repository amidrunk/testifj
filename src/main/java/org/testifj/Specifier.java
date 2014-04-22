package org.testifj;

public interface Specifier {

    void should(String description, Procedure procedure);

}
