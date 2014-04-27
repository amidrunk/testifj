package org.testifj.lang.impl;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ArrayInitializerImpl;
import org.testifj.lang.model.impl.ArrayLoadImpl;
import org.testifj.lang.model.impl.ArrayStoreImpl;
import org.testifj.lang.model.impl.NewArrayImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public final class ArrayDecompilerExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        // TODO Improve, document. Also, it should be possible to specify priority order (on(DUP).where(...).withPriority(HIGH).then(...)
        configurationBuilder.extend(ByteCode.dup, (context, code, byteCode) -> {
            final Expression expression = context.peek();

            if (expression.getElementType() != ElementType.NEW_ARRAY) {
                return false;
            }

            return true;
        });

        configurationBuilder.extend(ByteCode.aaload, aaload());
        configurationBuilder.extend(ByteCode.anewarray, anewarray());
        configurationBuilder.extend(ByteCode.aastore, aastore());
        configurationBuilder.extend(ByteCode.newarray, newarray());
        configurationBuilder.extend(ByteCode.iastore, iastore());
    }

    public static DecompilerExtension aaload() {
        return (context,codeStream,byteCode) -> {
            final Expression index = context.pop();
            final Expression array = context.pop();
            final Type arrayType = array.getType();
            final Type componentType;

            if (!(arrayType instanceof Class)) {
                final String typeName = arrayType.getTypeName();

                if (typeName.charAt(0) != '[') {
                    throw new ClassFileFormatException("Can't execute 'aaload' on non-array type: " + typeName);
                }

                componentType = context.resolveType(typeName.substring(1));
            } else {
                componentType = ((Class) arrayType).getComponentType();

                if (componentType == null) {
                    throw new ClassFileFormatException("Can't execute 'aaload' on non-array type: " + arrayType.getTypeName());
                }
            }

            context.push(new ArrayLoadImpl(array, index, componentType));

            return true;
        };
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
        return (context, code, byteCode) -> arrayStore(context);
    }

    public static DecompilerExtension newarray() {
        return (context,codeStream,byteCode) -> {
            final int type = codeStream.nextUnsignedByte();
            final Class arrayType;

            switch (type) {
                case 4:
                    arrayType = boolean[].class;
                    break;
                case 5:
                    arrayType = char[].class;
                    break;
                case 6:
                    arrayType = float[].class;
                    break;
                case 7:
                    arrayType = double[].class;
                    break;
                case 8:
                    arrayType = byte[].class;
                    break;
                case 9:
                    arrayType = short[].class;
                    break;
                case 10:
                    arrayType = int[].class;
                    break;
                case 11:
                    arrayType = long[].class;
                    break;
                default:
                    throw new ClassFileFormatException("Invalid type code for primitive array: " + type);
            }

            final Expression length = context.pop();

            context.push(new NewArrayImpl(arrayType, arrayType.getComponentType(), length, Collections.emptyList()));

            return true;
        };
    }

    public static DecompilerExtension iastore() {
        return (context,codeStream,byteCode) -> arrayStore(context);
    }

    private static boolean arrayStore(DecompilationContext context) {
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
    }

}
