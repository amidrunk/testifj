package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.testifj.lang.ConstantPoolEntry.*;

public final class DecompilerImpl implements Decompiler {

    private final DecompilerConfiguration configuration;

    private final DecompilerConfiguration coreConfiguration;

    public DecompilerImpl() {
        this(new DecompilerConfigurationImpl.Builder().build());
    }

    public DecompilerImpl(DecompilerConfiguration configuration) {
        assert configuration != null : "Configuration can't be null";

        this.configuration = configuration;
        this.coreConfiguration = createCoreConfiguration();
    }

    private static DecompilerConfiguration createCoreConfiguration() {
        final DecompilerConfigurationImpl.Builder builder = new DecompilerConfigurationImpl.Builder();

        InvokeDynamicExtensions.configure(builder);
        MethodCallExtensions.configure(builder);

        return builder.build();
    }

    @Override
    public Element[] parse(Method method, InputStream stream) throws IOException {
        final ClassFile classFile = method.getClassFile();
        final ConstantPool constantPool = classFile.getConstantPool();
        final DecompilationContext context = new DecompilationContextImpl(this, method, new ProgramCounterImpl(-1), new SimpleTypeResolver());
        final CodeStream codeStream = new InputStreamCodeStream(stream, context.getProgramCounter());

        while (true) {
            final int byteCode;

            try {
                byteCode = codeStream.nextInstruction();
            } catch (EOFException e) {
                break;
            }

            final DecompilerExtension userExtension = configuration.getDecompilerExtension(context, byteCode);

            if (userExtension != null && userExtension.decompile(context, codeStream, byteCode)) {
                continue;
            }

            switch (byteCode) {
                // Various

                case ByteCode.nop:
                    break;
                case ByteCode.pop2:
                    if (context.reduce()) {
                        context.reduce();
                    }

                    break;
                case ByteCode.pop:
                    context.reduce();
                    break;

                // Load local variable
                case ByteCode.aload:
                    ByteCodes.loadVariable(context, method, codeStream.nextByte());
                    break;
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
                    ByteCodes.storeVariable(context, method, codeStream.nextByte());
                    break;
                case ByteCode.istore_0:
                case ByteCode.istore_1:
                case ByteCode.istore_2:
                case ByteCode.istore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.istore_0);
                    break;
                case ByteCode.lstore:
                    ByteCodes.storeVariable(context, method, codeStream.nextByte());
                    break;
                case ByteCode.lstore_0:
                case ByteCode.lstore_1:
                case ByteCode.lstore_2:
                case ByteCode.lstore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.lstore_0);
                    break;
                case ByteCode.fstore:
                    ByteCodes.storeVariable(context, method, codeStream.nextByte());
                    break;
                case ByteCode.fstore_0:
                case ByteCode.fstore_1:
                case ByteCode.fstore_2:
                case ByteCode.fstore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.fstore_0);
                    break;
                case ByteCode.dstore:
                    ByteCodes.storeVariable(context, method, codeStream.nextByte());
                    break;
                case ByteCode.dstore_0:
                case ByteCode.dstore_1:
                case ByteCode.dstore_2:
                case ByteCode.dstore_3:
                    ByteCodes.storeVariable(context, method, byteCode - ByteCode.dstore_0);
                    break;
                case ByteCode.astore:
                    ByteCodes.storeVariable(context, method, codeStream.nextByte());
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
                    context.push(new ConstantExpressionImpl(codeStream.nextByte(), int.class));
                    break;

                // Constants

                case ByteCode.ldc2w: {
                    final int index = codeStream.nextUnsignedShort();
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
                    context.push(new ConstantExpressionImpl(codeStream.nextUnsignedShort(), int.class));
                    break;

                case ByteCode.ldc1: {
                    final ConstantPoolEntry entry = constantPool.getEntry(codeStream.nextUnsignedByte());

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
                    final int fieldRefEntryIndex = codeStream.nextUnsignedShort();
                    final FieldRefDescriptor fieldRefDescriptor = constantPool.getFieldRefDescriptor(fieldRefEntryIndex);

                    ByteCodes.getField(
                            context,
                            resolveType(fieldRefDescriptor.getClassName()),
                            SignatureImpl.parseType(fieldRefDescriptor.getDescriptor()),
                            fieldRefDescriptor.getName(),
                            byteCode == ByteCode.getstatic);

                    break;
                }

                // Control flow

                case ByteCode.if_icmpne: {
                    final int targetPC = context.getProgramCounter().get() + codeStream.nextUnsignedShort();
                    final Expression rightOperand = context.pop();
                    final Expression leftOperand = context.pop();

                    context.enlist(new BranchImpl(leftOperand, OperatorType.NE, rightOperand, targetPC));
                    break;
                }

                case ByteCode.goto_: {
                    // TODO This should be some added feature that hooks goto and matches a sequence
                    final int relativePC = context.getProgramCounter().get();
                    final int offset = codeStream.nextUnsignedShort();
                    final int targetPC = relativePC + offset;

                    if (context.hasStackedExpressions()) {
                        final List<Statement> statements = context.getStatements();
                        final Statement lastStatement = statements.get(statements.size() - 1);

                        context.getProgramCounter().lookAhead(targetPC, () -> {
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

                //case ByteCode.invokeinterface: {
                case -2: {
                    invokeMethod(context, codeStream, constantPool, false, true);

                    final int count = codeStream.nextByte();

                    if (count == 0) {
                        throw new ClassFileFormatException("Count field subsequent to interface method invocation must not be zero");
                    }

                    if (codeStream.nextByte() != 0) {
                        throw new ClassFileFormatException("Interface method calls must be followed by <count:byte>, 0");
                    }

                    break;
                }
                case ByteCode.invokevirtual: {
                    invokeMethod(context, codeStream, constantPool, false, false);
                    break;
                }
                case ByteCode.invokespecial:
                    invokeMethod(context, codeStream, constantPool, false, false);
                    break;
                case ByteCode.invokestatic:
                    invokeMethod(context, codeStream, constantPool, true, false);
                    break;

                case -1: {
                // case ByteCode.invokedynamic: {
                    final int indexRef = codeStream.nextUnsignedShort();
                    final InvokeDynamicDescriptor invokeDynamicDescriptor = constantPool.getInvokeDynamicDescriptor(indexRef);

                    final Signature getFunctionalInterfaceSignature = SignatureImpl.parse(invokeDynamicDescriptor.getMethodDescriptor());
                    final String functionalInterfaceMethodName = invokeDynamicDescriptor.getMethodName();

                    final BootstrapMethod bootstrapMethod = classFile.getBootstrapMethodsAttribute()
                            .orElseThrow(() -> new ClassFileFormatException("No bootstrap methods attribute is available in class " + classFile.getName()))
                            .getBootstrapMethods()
                            .get(invokeDynamicDescriptor.getBootstrapMethodAttributeIndex());

                    final MethodHandleDescriptor methodHandle = constantPool.getMethodHandleDescriptor(bootstrapMethod.getBootstrapMethodRef());
                    final ConstantPoolEntryDescriptor[] descriptors = constantPool.getDescriptors(bootstrapMethod.getBootstrapArguments());

                    if (descriptors[0].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
                        throw new UnsupportedOperationException();
                    }

                    if (descriptors[1].getTag() != ConstantPoolEntryTag.METHOD_HANDLE) {
                        throw new UnsupportedOperationException();
                    }

                    if (descriptors[2].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
                        throw new UnsupportedOperationException();
                    }

                    final Signature functionalInterfaceMethodSignature = SignatureImpl.parse(descriptors[0].as(MethodTypeDescriptor.class).getDescriptor());
                    final MethodHandleDescriptor backingMethodHandle = descriptors[1].as(MethodHandleDescriptor.class);
                    final Type declaringType = resolveType(backingMethodHandle.getClassName());
                    final String backingMethodName = backingMethodHandle.getMethodName();
                    final Signature backingMethodSignature = SignatureImpl.parse(backingMethodHandle.getMethodDescriptor());


                    final Optional<Expression> self;
                    final ReferenceKind referenceKind = backingMethodHandle.getReferenceKind();

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

                    final LocalVariableReference[] enclosedVariables = new LocalVariableReference[backingMethodSignature.getParameterTypes().size()];

                    for (int i = enclosedVariables.length - 1; i >= 0; i--) {
                        enclosedVariables[i] = (LocalVariableReference) context.pop();
                    }

                    context.push(new LambdaImpl(
                            self,
                            referenceKind,
                            getFunctionalInterfaceSignature.getReturnType(),
                            functionalInterfaceMethodName,
                            functionalInterfaceMethodSignature,
                            declaringType,
                            backingMethodName,
                            backingMethodSignature,
                            Arrays.asList(enclosedVariables)));

                    break;
                }

                // Invalid instructions

                default:
                    final DecompilerExtension coreExtension = coreConfiguration.getDecompilerExtension(context, byteCode);

                    if (coreExtension != null) {
                        if (coreExtension.decompile(context, codeStream, byteCode)) {
                            break;
                        }
                    }

                    throw new IllegalArgumentException("Invalid byte code " + byteCode + " (" + ByteCode.toString(byteCode) + ") in method " + method.getName());
            }
        }

        context.reduceAll();

        return context.getStatements().stream().toArray(Element[]::new);
    }

    private void invokeMethod(DecompilationContext context, CodeStream stream, ConstantPool constantPool, boolean invokeStatic, boolean isInterface) throws IOException {
        final ClassEntry classEntry;
        final NameAndTypeEntry methodNameAndType;
        final int methodRefIndex = stream.nextUnsignedShort();

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
