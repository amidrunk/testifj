package org.testifj.lang.impl;

import org.testifj.lang.ByteCode;
import org.testifj.lang.DecompilerConfiguration;
import org.testifj.lang.DecompilerExtension;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ArrayInitializerImpl;
import org.testifj.lang.model.impl.ArrayStoreImpl;
import org.testifj.lang.model.impl.NewArrayImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public final class ArrayDecompilerExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.extend(ByteCode.anewarray, anewarray());
        configurationBuilder.extend(ByteCode.aastore, aastore());
    }

    public static DecompilerExtension anewarray() {
        return (context, code, byteCode) -> {
            final String componentTypeName = context.getMethod().getClassFile().getConstantPool().getClassName(code.nextUnsignedShort());
            final Type componentType = context.resolveType(componentTypeName);
            final Type arrayType = context.resolveType("[L" + componentTypeName + ";");
            final Expression length = context.pop();

            context.push(new NewArrayImpl(arrayType, componentType, length, Collections.emptyList()));

            // Ignore dup since element initialization push the array back to the stack
            if (code.peekByte() == ByteCode.dup) {
                code.commit();
            }

            return true;
        };
    }

    public static DecompilerExtension aastore() {
        return (context, code, byteCode) -> {
            Expression value = context.pop();
            Expression index = context.pop();
            Expression array = context.pop();

            if (array.getElementType() == ElementType.NEW_ARRAY) {
                final NewArray newArray = (NewArray) array;
                final ArrayList<ArrayInitializer> initializers = new ArrayList<>(newArray.getInitializers());

                initializers.add(new ArrayInitializerImpl((Integer) ((Constant) index).getConstant(), value));

                context.push(new NewArrayImpl(newArray.getType(), newArray.getComponentType(), newArray.getLength(), initializers));
            } else {
                context.enlist(new ArrayStoreImpl(array, index, value));
            }

            return true;
        };
    }

}
