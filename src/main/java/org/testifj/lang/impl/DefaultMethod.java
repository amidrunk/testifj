package org.testifj.lang.impl;

import org.testifj.lang.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class DefaultMethod implements Method {

    private final Supplier<ClassFile> classFile;

    private final int accessFlags;

    private final String name;

    private final String signature;

    private final Attribute[] attributes;

    public DefaultMethod(Supplier<ClassFile> classFile, int accessFlags, String name, String signature, Attribute[] attributes) {
        assert classFile != null : "Class file can't be null";
        assert name != null : "name can't be null";
        assert signature != null : "signature can't be null";
        assert attributes != null : "attributes can't be null";

        this.classFile = classFile;
        this.accessFlags = accessFlags;
        this.name = name;
        this.signature = signature;
        this.attributes = attributes;
    }

    @Override
    public ClassFile getClassFile() {
        return classFile.get();
    }

    @Override
    public int getAccessFlags() {
        return accessFlags;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Arrays.asList(attributes);
    }

    @Override
    public CodeAttribute getCode() {
        final Optional<Attribute> optionalCodeAttribute = Arrays.asList(attributes).stream()
                .filter(a -> a.getName().equals(CodeAttribute.ATTRIBUTE_NAME))
                .findFirst();

        return (CodeAttribute) optionalCodeAttribute
                .orElseThrow(() -> new IllegalStateException("Code attribute is not present for method '" + getName() + "'"));
    }

    @Override
    public InputStream getCodeForLineNumber(int lineNumber) {
        final LineNumberTable lineNumberTable = (LineNumberTable) getCode().getAttributes().stream()
                .filter(a -> a.getName().equals(LineNumberTable.ATTRIBUTE_NAME))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Line numbers are not present for method '" + getName() + "'"));

        final LineNumberTableEntry startEntry = lineNumberTable.getEntries().stream()
                .filter(e -> e.getLineNumber() == lineNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Code is not available for line number '" + lineNumber + "'"));

        final Optional<LineNumberTableEntry> endEntry = lineNumberTable.getEntries().stream()
                .filter(e -> e.getStartPC() > startEntry.getStartPC())
                .sorted((e1, e2) -> e1.getStartPC() - e2.getStartPC())
                .findFirst();

        try (InputStream inputStream = getCode().getCode()) {
            inputStream.skip(startEntry.getStartPC());

            if (endEntry.isPresent()) {
                final byte[] buffer = new byte[endEntry.get().getStartPC() - startEntry.getStartPC()];

                inputStream.read(buffer);

                return new ByteArrayInputStream(buffer);
            } else {
                return inputStream;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LocalVariable getLocalVariableForIndex(int index) {
        assert index >= 0 : "Index must be positive";

        final Optional<Attribute> attribute = getCode().getAttributes().stream()
                .filter(a -> a.getName().equals(LocalVariableTable.ATTRIBUTE_NAME))
                .findFirst();

        if (!attribute.isPresent()) {
            throw new IllegalStateException("Local variable table is not present in method '" + getName() + "'");
        }

        final LocalVariableTable localVariableTable = (LocalVariableTable) attribute.get();

        final Optional<LocalVariable> localVariable = localVariableTable.getLocalVariables().stream()
                .filter(v -> v.getIndex() == index)
                .findFirst();

        if (!localVariable.isPresent()) {
            throw new IllegalStateException("No local variable exists for index " + index);
        }

        return localVariable.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultMethod)) {
            return false;
        }

        final DefaultMethod other = (DefaultMethod) obj;

        return other.accessFlags == accessFlags
                && other.name.equals(name)
                && other.signature.equals(signature)
                && Arrays.equals(other.attributes, attributes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{
                accessFlags,
                name.hashCode(),
                signature.hashCode(),
                Arrays.hashCode(attributes)
        });
    }

    @Override
    public String toString() {
        return "DefaultMethod{" +
                "accessFlags=" + accessFlags + ", " +
                "name=\"" + name + "\", " +
                "signature=\"" + signature + "\", " +
                "attributes=" + Arrays.asList(attributes) +
                "}";
    }
}
