package org.testifj.lang;

import java.lang.reflect.Type;

public final class Types {

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
