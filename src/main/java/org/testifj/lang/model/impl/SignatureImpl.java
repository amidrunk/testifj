package org.testifj.lang.model.impl;

import org.testifj.lang.ClassFileFormatException;
import org.testifj.lang.model.Signature;
import org.testifj.util.StringReader;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class SignatureImpl implements Signature {

    private final String specification;

    private final Type[] parameterTypes;

    private final Type returnType;

    private SignatureImpl(String specification, Type[] parameterTypes, Type returnType) {
        this.specification = specification;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public List<Type> getParameterTypes() {
        return Arrays.asList(parameterTypes);
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    public static SignatureImpl parse(String spec) {
        assert spec != null && !spec.isEmpty() : "Signature specification can't be null or empty";

        final StringReader reader = new StringReader(spec);
        final List<Type> parameterTypes = new LinkedList<>();

        if (!reader.read("(")) {
            throw new ClassFileFormatException("Signature must start with '(': '" + spec + "'");
        }

        while (true) {
            final int n = reader.peek();

            if (n == -1) {
                throw new ClassFileFormatException("Invalid signature around; expected ')' before EOF");
            }

            if (n == ')') {
                reader.skip(1);
                break;
            }

            parameterTypes.add(readType(reader));
        }

        final Type returnType = readType(reader);

        return new SignatureImpl(spec, parameterTypes.toArray(new Type[parameterTypes.size()]), returnType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SignatureImpl signature = (SignatureImpl) o;

        if (!Arrays.equals(parameterTypes, signature.parameterTypes)) return false;
        if (!returnType.equals(signature.returnType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(parameterTypes);
        result = 31 * result + returnType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return specification;
    }

    public static Type parseType(String string) {
        return readType(new StringReader(string));
    }

    private static Type readType(StringReader reader) {
        final int shortType = reader.read();

        if (shortType == -1) {
            throw new ClassFileFormatException("Could not read type due to premature EOF");
        }

        switch (shortType) {
            case 'V':
                return void.class;
            case 'B':
                return byte.class;
            case 'C':
                return char.class;
            case 'D':
                return double.class;
            case 'F':
                return float.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'S':
                return short.class;
            case 'Z':
                return boolean.class;
            case 'L': {
                final Optional<String> typeName = reader.readUntil(Pattern.compile(";"));

                if (!typeName.isPresent()) {
                    throw new ClassFileFormatException("Malformed signature around '..." + reader.remainder() + "'; expected ';' after object");
                } else {
                    reader.skip(1);

                    try {
                        return Class.forName(typeName.get().replace('/', '.'));
                    } catch (ClassNotFoundException e) {
                        throw new ClassFileFormatException("Invalid class reference: '" + typeName.get() + "'");
                    }
                }
            }
            case '[': {
                final int shortComponentType = reader.peek();
                final Class componentType = (Class) readType(reader);
                final String arrayClassName;

                if (componentType.isPrimitive()) {
                    arrayClassName = "[" + (char) shortComponentType;
                } else if (componentType.isArray()) {
                    arrayClassName = "[" + componentType.getName();
                } else {
                    arrayClassName = "[L" + componentType.getName() + ";";
                }

                try {
                    return Class.forName(arrayClassName);
                } catch (ClassNotFoundException e) {
                    throw new ClassFileFormatException("Invalid array format: '" + arrayClassName + "'");
                }
            }
            default:
                throw new ClassFileFormatException("Invalid type in signature '" + (char) shortType + "'");
        }
    }

}
