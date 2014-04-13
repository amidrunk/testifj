package org.testifj.lang.impl;

import org.testifj.lang.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class ClassFileReaderImpl implements ClassFileReader {

    private final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    public ClassFile read(InputStream in) throws IOException, ClassFormatError {
        assert in != null : "Input stream can't be null";

        final DataInputStream din = new DataInputStream(in);
        final int magicNumber = din.readInt();

        if (magicNumber != MAGIC_NUMBER) {
            throw new ClassFormatError("Stream must begin with magic number (0xCAFEBABE)");
        }

        final AtomicReference<ClassFile> classFileReference = new AtomicReference<>();
        final Supplier<ClassFile> classFileSupplier = classFileReference::get;

        final int minorVersion = din.readShort();
        final int majorVersion = din.readShort();
        final ConstantPool constantPool = readConstantPool(din);
        final int accessFlags = din.readShort();
        final String className = toClassName(constantPool.getClassName(din.readShort()));
        final String superClassName = toClassName(constantPool.getClassName(din.readShort()));
        final String[] interfaceNames = readInterfaces(din, constantPool);
        final Field[] fields = readFields(classFileSupplier, din, constantPool);
        final List<Method> methods = new LinkedList<>();
        final List<Constructor> constructors = new LinkedList<>();

        readMethods(classFileSupplier, din, constantPool, methods, constructors);

        final Attribute[] classAttributes = readAttributes(din, constantPool);

        final ClassFile classFile = DefaultClassFile.fromVersion(minorVersion, majorVersion)
                .withConstantPool(constantPool)
                .withSignature(accessFlags, className, superClassName, interfaceNames)
                .withFields(fields)
                .withConstructors(constructors.toArray(new Constructor[constructors.size()]))
                .withMethods(methods.toArray(new Method[methods.size()]))
                .withAttributes(classAttributes)
                .create();

        classFileReference.set(classFile);

        return classFile;
    }

    protected void readMethods(Supplier<ClassFile> classFileSupplier, DataInputStream din, ConstantPool constantPool, List<Method> methods, List<Constructor> constructors) throws IOException {
        final int count = din.readShort();

        for (int i = 0; i < count; i++) {
            final int accessFlags = din.readShort();
            final String name = constantPool.getString(din.readShort());
            final String signature = constantPool.getString(din.readShort());
            final Attribute[] attributes = readAttributes(din, constantPool);

            if ("<init>".equals(name)) {
                constructors.add(new DefaultConstructor(classFileSupplier, accessFlags, name, signature, attributes));
            } else {
                methods.add(new DefaultMethod(classFileSupplier, accessFlags, name, signature, attributes));
            }
        }
    }

    protected Field[] readFields(Supplier<ClassFile> classFileSupplier, DataInputStream din, ConstantPool constantPool) throws IOException {
        final Field[] fields = new Field[din.readShort()];

        for (int i = 0; i < fields.length; i++) {
            final int accessFlags = din.readShort();
            final String name = constantPool.getString(din.readShort());
            final String signature = constantPool.getString(din.readShort());
            final Attribute[] attributes = readAttributes(din, constantPool);

            fields[i] = new DefaultField(classFileSupplier, accessFlags, name, signature, attributes);
        }

        return fields;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected Attribute[] readAttributes(DataInputStream din, ConstantPool constantPool) throws IOException {
        final Attribute[] attributes = new Attribute[din.readShort()];

        for (int i = 0; i < attributes.length; i++) {
            final String name = constantPool.getString(din.readShort());
            final int length = din.readInt();
            final byte[] buffer = new byte[length];

            din.read(buffer);

            switch (name) {
                case CodeAttribute.ATTRIBUTE_NAME: {
                    final DataInputStream codeStream = new DataInputStream(new ByteArrayInputStream(buffer));

                    final int maxStack = codeStream.readShort();
                    final int maxLocals = codeStream.readShort();
                    final int codeLength = codeStream.readInt();

                    attributes[i] = new CodeAttributeImpl(ByteBuffer.wrap(buffer), maxStack, maxLocals, ByteBuffer.wrap(buffer, 8, codeLength));
                    break;
                }
                default:
                    attributes[i] = new UnknownAttribute(name, buffer);
                    break;
            }
        }

        return attributes;
    }

    protected String[] readInterfaces(DataInputStream din, ConstantPool constantPool) throws IOException {
        final String[] interfaceNames = new String[din.readShort()];

        for (int i = 0; i < interfaceNames.length; i++) {
            interfaceNames[i] = toClassName(constantPool.getClassName(din.readShort()));
        }

        return interfaceNames;
    }

    protected String toClassName(String string) {
        return string.replace('/', '.');
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected ConstantPool readConstantPool(DataInputStream din) throws IOException {
        final int constantPoolCount = din.readShort();
        final DefaultConstantPool.Builder builder = new DefaultConstantPool.Builder();

        for (int i = 1; i < constantPoolCount; i++) {
            final ConstantPoolEntryTag tag = ConstantPoolEntryTag.fromTag(din.readByte());

            switch (tag) {
                case CLASS:
                    builder.addEntry(new ConstantPoolEntry.ClassEntry(din.readShort()));
                    break;
                case FIELD_REF:
                    builder.addEntry(new ConstantPoolEntry.FieldRefEntry(din.readShort(), din.readShort()));
                    break;
                case METHOD_REF:
                    builder.addEntry(new ConstantPoolEntry.MethodRefEntry(din.readShort(), din.readShort()));
                    break;
                case INTERFACE_METHOD_REF:
                    builder.addEntry(new ConstantPoolEntry.InterfaceMethodRefEntry(din.readShort(), din.readShort()));
                    break;
                case STRING:
                    builder.addEntry(new ConstantPoolEntry.StringEntry(din.readShort()));
                    break;
                case INTEGER:
                    builder.addEntry(new ConstantPoolEntry.IntegerEntry(din.readInt()));
                    break;
                case FLOAT:
                    builder.addEntry(new ConstantPoolEntry.FloatEntry(din.readFloat()));
                    i++;
                    break;
                case LONG:
                    builder.addEntry(new ConstantPoolEntry.LongEntry(din.readLong()));
                    i++;
                    break;
                case DOUBLE:
                    builder.addEntry(new ConstantPoolEntry.DoubleEntry(din.readDouble()));
                    break;
                case NAME_AND_TYPE:
                    builder.addEntry(new ConstantPoolEntry.NameAndTypeEntry(din.readShort(), din.readShort()));
                    break;
                case UTF8:
                    final int length = din.readShort();
                    final byte[] buffer = new byte[length];

                    din.read(buffer);
                    builder.addEntry(new ConstantPoolEntry.UTF8Entry(new String(buffer)));
                    break;
                case METHOD_HANDLE:
                    builder.addEntry(new ConstantPoolEntry.MethodHandleEntry(ReferenceKind.valueOf(din.readByte()), din.readShort()));
                    break;
                case METHOD_TYPE:
                    builder.addEntry(new ConstantPoolEntry.MethodTypeEntry(din.readShort()));
                    break;
                case INVOKE_DYNAMIC:
                    builder.addEntry(new ConstantPoolEntry.InvokeDynamicEntry(din.readShort(), din.readShort()));
                    break;
                default:
                    throw new ClassFormatError("Unknown class pool entry tag: " + tag);
            }
        }

        return builder.create();
    }
}
