package org.testifj.lang;

import java.lang.reflect.Type;

public final class SimpleTypeResolver implements TypeResolver {
    @Override
    public Type resolveType(String name) {
        assert name != null && !name.isEmpty() : "Type name can't be null or empty";

        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return new UnresolvedType(name);
        }
    }
}
