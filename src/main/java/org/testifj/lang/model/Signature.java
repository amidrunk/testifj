package org.testifj.lang.model;

import java.lang.reflect.Type;
import java.util.List;

public interface Signature {

    List<Type> getParameterTypes();

    Type getReturnType();

}
