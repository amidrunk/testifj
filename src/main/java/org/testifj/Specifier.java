package org.testifj;

import org.testifj.lang.Procedure;

public interface Specifier {

    void should(String description, Procedure procedure);

}
