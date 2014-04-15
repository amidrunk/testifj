package org.testifj.lang;

import java.util.List;

public interface BootstrapMethodsAttribute extends Attribute {

    String ATTRIBUTE_NAME = "BootstrapMethods";

    List<BootstrapMethod> getBootstrapMethods();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
