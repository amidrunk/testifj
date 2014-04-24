package org.testifj.lang;

import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;

public interface MethodReference {

    Type getTargetType();

    String getName();

    Signature getSignature();

}
