package org.testifj.lang;

import java.lang.reflect.Type;

public final class Types {

    public static boolean isPrimitive(Type type) {
        assert type != null : "Type can't be null";

        switch (type.getTypeName()) {
            case "boolean":
            case "byte":
            case "short":
            case "char":
            case "int":
            case "long":
            case "float":
            case "double":
                return true;
        }

        return false;
    }

    public static int getComputationalCategory(Type type) {
        assert type != null : "Type can't be null";

        switch (type.getTypeName()) {
            case "double":
            case "long":
                return 2;
            default:
                return 1;
        }
    }

}
