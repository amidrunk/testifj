package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerExtension;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;
import org.testifj.util.Priority;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public final class ArrayDecompilerExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.dup)
                .withPriority(Priority.HIGH)
                .when((context, byteCode) -> context.peek().getElementType() == ElementType.NEW_ARRAY)
                .then(DecompilerExtension.NOP);

        configurationBuilder.on(ByteCode.aaload).then(aaload());
        configurationBuilder.on(ByteCode.anewarray).then(anewarray());
        configurationBuilder.on(ByteCode.aastore).then(aastore());
        configurationBuilder.on(ByteCode.newarray).then(newarray());
        configurationBuilder.on(ByteCode.iastore).then(iastore());
        configurationBuilder.on(ByteCode.arraylength).then(arraylength());
        configurationBuilder.on(ByteCode.iaload).then(iaload());
        configurationBuilder.on(ByteCode.laload).then(laload());
        configurationBuilder.on(ByteCode.faload).then(faload());
        configurationBuilder.on(ByteCode.daload).then(daload());
        configurationBuilder.on(ByteCode.baload).then(baload());
        configurationBuilder.on(ByteCode.caload).then(caload());
        configurationBuilder.on(ByteCode.saload).then(saload());
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
        };
    }

    public static DecompilerExtension iastore() {
        return (context,codeStream,byteCode) -> arrayStore(context);
    }

    public static DecompilerExtension arraylength() {
        return (context,codeStream,byteCode) -> {
            final Expression array = context.pop();

            context.push(new FieldReferenceImpl(array, array.getType(), int.class, "length"));
        };
    }

    public static DecompilerExtension iaload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, int.class);
        };
    }

    public static DecompilerExtension laload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, long.class);
        };
    }

    public static DecompilerExtension faload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, float.class);
        };
    }

    public static DecompilerExtension daload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, double.class);
        };
    }

    public static DecompilerExtension baload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, boolean.class);
        };
    }

    public static DecompilerExtension caload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, char.class);
        };
    }

    public static DecompilerExtension saload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, short.class);
        };
    }

    private static void arrayLoad(DecompilationContext context, Class<?> type) {
        final Expression index = context.pop();
        final Expression array = context.pop();

        context.push(new ArrayLoadImpl(array, index, type));
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
