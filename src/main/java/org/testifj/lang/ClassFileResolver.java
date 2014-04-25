package org.testifj.lang;

import java.lang.reflect.Type;

public interface ClassFileResolver {

    ClassFile resolveClassFile(Type type) throws ClassFileResolutionException;

}
