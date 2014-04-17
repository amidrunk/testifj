package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.lang.imlp.DecompilationContextImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.testifj.lang.ConstantPoolEntry.*;

public final class ByteCodeParserImpl implements ByteCodeParser {

    @Override
    public Element[] parse(Method method, InputStream stream) throws IOException {
        final ClassFile classFile = method.getClassFile();
        final ConstantPool constantPool = classFile.getConstantPool();
        final DecompilationContext context = new DecompilationContextImpl();
        final ProgramCounter pc = new ProgramCounter(-1);

        final DataInputStream in = new DataInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                final int n = stream.read();

                if (n != -1) {
                    pc.advance();
                }

                return n;
            }
        });

        while (true) {
            final int n = in.read();

            if (n == -1) {
                break;
            }

            final int byteCode = n & 0xFF;

            switch (byteCode) {
                // Various

                case ByteCode.nop:
                    break;
                case ByteCode.pop2:
                    // TODO any additional handling for this?
                case ByteCode.pop:
                    context.reduceAll();
                    break;

                // Load local variable

                case ByteCode.aload_0:
                case ByteCode.aload_1:
                case ByteCode.aload_2:
                case ByteCode.aload_3:
                    ByteCodes.loadVariable(context, method, byteCode - ByteCode.aload_0);
                    break;
                case ByteCode.iload_0:
                case ByteCode.iload_1:
                case ByteCode.iload_2:
                case ByteCode.iload_3:
                    ByteCodes.loadVariable(context, method, byteCode - ByteCode.iload_0);
                    break;
                case ByteCode.lload_0:
                case ByteCode.lload_1:
                case ByteCode.lload_2:
                case ByteCode.lload_3:
                    ByteCodes.loadVariable(context, method, byteCode - ByteCode.lload_0);
                    break;

                // Store local variable

                case ByteCode.istore:
                    ByteCodes.storeVariable(context, method, in.read());
                    break;
                case ByteCode.istore_0:
                case ByteCode.istore_1:
                case ByteCode.istore_2:
                case ByteCode.istore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.istore_0);
                    break;
                case ByteCode.lstore:
                    ByteCodes.storeVariable(context, method, in.read());
                    break;
                case ByteCode.lstore_0:
                case ByteCode.lstore_1:
                case ByteCode.lstore_2:
                case ByteCode.lstore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.lstore_0);
                    break;
                case ByteCode.fstore:
                    ByteCodes.storeVariable(context, method, in.read());
                    break;
                case ByteCode.fstore_0:
                case ByteCode.fstore_1:
                case ByteCode.fstore_2:
                case ByteCode.fstore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.fstore_0);
                    break;
                case ByteCode.dstore:
                    ByteCodes.storeVariable(context, method, in.read());
                    break;
                case ByteCode.dstore_0:
                case ByteCode.dstore_1:
                case ByteCode.dstore_2:
                case ByteCode.dstore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.dstore_0);
                    break;
                case ByteCode.astore:
                    ByteCodes.storeVariable(context, method, in.read());
                    break;
                case ByteCode.astore_0:
                case ByteCode.astore_1:
                case ByteCode.astore_2:
                case ByteCode.astore_3: {
                    final int index = byteCode - ByteCode.astore_0;
                    ByteCodes.storeVariable(context, method, index);
                    break;
                }

                // Operators

                case ByteCode.iadd: {
                    final Expression rightOperand = context.pop();
                    final Expression leftOperand = context.pop();

                    context.push(new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, int.class));

                    break;
                }

                // Push constants onto stack

                case ByteCode.bipush:
                    context.push(new ConstantExpressionImpl(in.read(), int.class));
                    break;

                // Constants

                case ByteCode.ldc2w: {
                    final int index = in.readUnsignedShort();
                    final ConstantPoolEntry entry = constantPool.getEntry(index);

                    switch (entry.getTag()) {
                        case LONG:
                            context.push(new ConstantExpressionImpl(((LongEntry) entry).getValue(), long.class));
                            break;
                        case DOUBLE:
                            context.push(new ConstantExpressionImpl(((DoubleEntry) entry).getValue(), double.class));
                            break;
                        default:
                            throw new ClassFileFormatException("Invalid constant pool entry at "
                                    + index + ". Expected long or double, but was " + entry);
                    }

                    break;
                }
                case ByteCode.iconst_m1:
                case ByteCode.iconst_0:
                case ByteCode.iconst_1:
                case ByteCode.iconst_2:
                case ByteCode.iconst_3:
                case ByteCode.iconst_4:
                case ByteCode.iconst_5:
                    context.push(new ConstantExpressionImpl(byteCode - ByteCode.iconst_0, int.class));
                    break;
                case ByteCode.lconst_0:
                case ByteCode.lconst_1:
                    context.push(new ConstantExpressionImpl((long) (byteCode - ByteCode.lconst_0), long.class));
                    break;
                case ByteCode.sipush:
                    context.push(new ConstantExpressionImpl(((in.read() << 8) & 0xFF00 | in.read() & 0xFF), int.class));
                    break;

                case ByteCode.ldc1: {
                    final ConstantPoolEntry entry = constantPool.getEntry(in.read());

                    switch (entry.getTag()) {
                        case INTEGER:
                            context.push(new ConstantExpressionImpl(((IntegerEntry) entry).getValue(), int.class));
                            break;
                        case FLOAT:
                            context.push(new ConstantExpressionImpl(((FloatEntry) entry).getValue(), float.class));
                            break;
                        case STRING:
                            context.push(new ConstantExpressionImpl(constantPool.getString(((StringEntry) entry).getStringIndex()), String.class));
                            break;
                        case CLASS:
                            final Type type = resolveType(constantPool.getString(((ClassEntry) entry).getNameIndex()));
                            context.push(new ConstantExpressionImpl(type, Class.class));
                            break;
                        default:
                            throw new ClassFileFormatException("Unsupported constant pool entry: " + entry);
                    }

                    break;
                }

                // Method return

                case ByteCode.return_:
                    // Expecting empty stack; stacked expressions are statements
                    context.reduceAll();
                    context.enlist(new ReturnImpl());
                    break;
                case ByteCode.areturn:
                case ByteCode.ireturn:
                    context.enlist(new ReturnValueImpl(context.pop()));
                    break;

                // Field access
                case ByteCode.getstatic:
                case ByteCode.getfield: {
                    final int fieldRefEntryIndex = (in.read() << 8) & 0xFF | in.read() & 0xFF;
                    final FieldDescriptor fieldDescriptor = constantPool.getFieldDescriptor(fieldRefEntryIndex);

                    ByteCodes.getField(
                            context,
                            resolveType(fieldDescriptor.getClassName()),
                            SignatureImpl.parseType(fieldDescriptor.getDescriptor()),
                            fieldDescriptor.getName(),
                            byteCode == ByteCode.getstatic);

                    break;
                }

                // Control flow

                case ByteCode.if_icmpne: {
                    final int targetPC = pc.get() + (in.read() << 8) & 0xFF00 | in.read() & 0xFF;
                    final Expression rightOperand = context.pop();
                    final Expression leftOperand = context.pop();

                    context.enlist(new BranchImpl(leftOperand, OperatorType.NE, rightOperand, targetPC));
                    break;
                }

                case ByteCode.goto_: {
                    // TODO This should be some added feature that hooks goto and matches a sequence
                    final int relativePC = pc.get();
                    final int offset = (in.read() << 8) & 0xFF00 | in.read() & 0xFF;
                    final int targetPC = relativePC + offset;

                    if (context.hasStackedExpressions()) {
                        final List<Statement> statements = context.getStatements();
                        final Statement lastStatement = statements.get(statements.size() - 1);

                        pc.lookAhead(targetPC, () -> {
                            final Expression leftBranch = context.pop();
                            final Expression rightBranch = context.pop();
                            final Branch branchExpression = (Branch) lastStatement;

                            if (leftBranch.getElementType() == ElementType.CONSTANT && rightBranch.getElementType() == ElementType.CONSTANT) {
                                final ConstantExpression trueValue = (ConstantExpression) leftBranch;
                                final ConstantExpression falseValue = (ConstantExpression) rightBranch;

                                // == comparison
                                if (branchExpression.getOperatorType() == OperatorType.NE && trueValue.getConstant().equals(0) && falseValue.getConstant().equals(1)) {
                                    context.removeStatement(context.getStatements().size() - 1);
                                    context.push(new BinaryOperatorImpl(branchExpression.getLeftOperand(), OperatorType.EQ, branchExpression.getRightOperand(), boolean.class));
                                }
                            }
                        });
                    }

                    break;
                }

                // Method invocation

                case ByteCode.invokeinterface: {
                    invokeMethod(context, in, constantPool, false, true);

                    final int count = in.read();

                    if (count == 0) {
                        throw new ClassFileFormatException("Count field subsequent to interface method invocation must not be zero");
                    }

                    if (in.read() != 0) {
                        throw new ClassFileFormatException("Interface method calls must be followed by <count:byte>, 0");
                    }

                    break;
                }
                case ByteCode.invokevirtual: {
                    invokeMethod(context, in, constantPool, false, false);
                    break;
                }
                case ByteCode.invokespecial:
                    invokeMethod(context, in, constantPool, false, false);
                    break;
                case ByteCode.invokestatic:
                    invokeMethod(context, in, constantPool, true, false);
                    break;
                case ByteCode.invokedynamic: {
                    final int indexRef = (in.read() << 8) & 0xFF00 | in.read() & 0xFF;
                    final InvokeDynamicEntry invokeDynamicEntry = (InvokeDynamicEntry) constantPool.getEntry(indexRef);
                    final NameAndTypeEntry nameAndTypeEntry = (NameAndTypeEntry) constantPool.getEntry(invokeDynamicEntry.getNameAndTypeIndex());
                    final Signature getFunctionalInterfaceSignature = SignatureImpl.parse(constantPool.getString(nameAndTypeEntry.getDescriptorIndex()));
                    final String functionalInterfaceMethodName = constantPool.getString(nameAndTypeEntry.getNameIndex());
                    final BootstrapMethod bootstrapMethod = classFile.getBootstrapMethodsAttribute()
                            .orElseThrow(() -> new ClassFileFormatException("No bootstrap methods attribute is available in class " + classFile.getName()))
                            .getBootstrapMethods()
                            .get(invokeDynamicEntry.getBootstrapMethodAttributeIndex());

                    final MethodHandleEntry methodHandleEntry = (MethodHandleEntry) constantPool.getEntry(bootstrapMethod.getBootstrapMethodRef());
                    final MethodRefEntry entry = (MethodRefEntry) constantPool.getEntry(methodHandleEntry.getReferenceIndex());
                    final String className = constantPool.getClassName(entry.getClassIndex());
                    final NameAndTypeEntry bootstrapMethodNameAndType = (NameAndTypeEntry) constantPool.getEntry(entry.getNameAndTypeIndex());
                    final String bootstrapMethodName = constantPool.getString(bootstrapMethodNameAndType.getNameIndex());
                    final String bootstrapMethodDescriptor = constantPool.getString(bootstrapMethodNameAndType.getDescriptorIndex());
                    final ConstantPoolEntry[] bootstrapMethodArguments = constantPool.getEntries(bootstrapMethod.getBootstrapArguments());

                    if (bootstrapMethodArguments[0].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
                        throw new UnsupportedOperationException();
                    }

                    if (bootstrapMethodArguments[1].getTag() != ConstantPoolEntryTag.METHOD_HANDLE) {
                        throw new UnsupportedOperationException();
                    }

                    if (bootstrapMethodArguments[2].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
                        throw new UnsupportedOperationException();
                    }

                    final Signature functionalInterfaceMethodSignature = SignatureImpl.parse(constantPool.getString(bootstrapMethodArguments[0].as(MethodTypeEntry.class).getDescriptorIndex()));
                    final MethodRefEntry backingMethodRefEntry = constantPool.getEntry(bootstrapMethodArguments[1].as(MethodHandleEntry.class).getReferenceIndex()).as(MethodRefEntry.class);
                    final Type declaringType = resolveType(constantPool.getClassName(backingMethodRefEntry.getClassIndex()));
                    final NameAndTypeEntry backingMethodNameAndType = constantPool.getEntry(backingMethodRefEntry.getNameAndTypeIndex()).as(NameAndTypeEntry.class);
                    final String backingMethodName = constantPool.getString(backingMethodNameAndType.getNameIndex());
                    final Signature backingMethodSignature = SignatureImpl.parse(constantPool.getString(backingMethodNameAndType.getDescriptorIndex()));
                    final Optional<Expression> self;
                    final ReferenceKind referenceKind = ((MethodHandleEntry) bootstrapMethodArguments[1]).getReferenceKind();

                    switch (referenceKind) {
                        case INVOKE_STATIC:
                            self = Optional.empty();
                            break;
                        case INVOKE_VIRTUAL:
                            self = Optional.empty();
                            break;
                        case INVOKE_SPECIAL:
                            self = Optional.of(context.pop());
                            break;
                        default:
                            throw new UnsupportedOperationException("Reference kind not supported for dynamic invoke: " + referenceKind);
                    }

                    context.push(new LambdaImpl(
                            self,
                            referenceKind,
                            getFunctionalInterfaceSignature.getReturnType(),
                            functionalInterfaceMethodName,
                            functionalInterfaceMethodSignature,
                            declaringType,
                            backingMethodName,
                            backingMethodSignature));
                    break;
                }

                // Invalid instructions

                default:
                    throw new IllegalArgumentException("Invalid byte code " + n + " (" + ByteCode.toString(byteCode) + ") in method " + method.getName());
            }
        }

        context.reduceAll();

        return context.getStatements().stream().toArray(Element[]::new);
    }

    private void invokeMethod(DecompilationContext context, InputStream in, ConstantPool constantPool, boolean invokeStatic, boolean isInterface) throws IOException {
        final ClassEntry classEntry;
        final NameAndTypeEntry methodNameAndType;
        final int methodRefIndex = (in.read() << 8) & 0xFF00 | in.read() & 0xFF;

        if (!isInterface) {
            final MethodRefEntry methodRef = (MethodRefEntry) constantPool.getEntry(methodRefIndex);

            classEntry = (ClassEntry) constantPool.getEntry(methodRef.getClassIndex());
            methodNameAndType = (NameAndTypeEntry) constantPool.getEntry(methodRef.getNameAndTypeIndex());
        } else {
            final InterfaceMethodRefEntry methodRef = (InterfaceMethodRefEntry) constantPool.getEntry(methodRefIndex);

            classEntry = (ClassEntry) constantPool.getEntry(methodRef.getClassIndex());
            methodNameAndType = (NameAndTypeEntry) constantPool.getEntry(methodRef.getNameAndTypeIndex());
        }

        final String targetClassName = constantPool.getString(classEntry.getNameIndex());
        final String methodDescriptor = constantPool.getString(methodNameAndType.getDescriptorIndex());
        final String methodName = constantPool.getString(methodNameAndType.getNameIndex());
        final SignatureImpl signature = SignatureImpl.parse(methodDescriptor);
        final Expression[] parameters = new Expression[signature.getParameterTypes().size()];

        for (int i = parameters.length - 1; i >= 0; i--) {
            parameters[i] = context.pop();
        }

        final Expression targetInstance = (invokeStatic ? null : context.pop());

        context.push(new MethodCallImpl(resolveType(targetClassName), methodName, signature, targetInstance, parameters));
    }

    private Type resolveType(String className) {
        try {
            return Class.forName(className.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            // Unresolved type
            return new Type() {
                @Override
                public String getTypeName() {
                    return className;
                }
            };
        }
    }
}
