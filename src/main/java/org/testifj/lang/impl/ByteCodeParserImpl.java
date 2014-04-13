package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Stack;

import static org.testifj.lang.ConstantPoolEntry.*;

public final class ByteCodeParserImpl implements ByteCodeParser {

    @Override
    public Element[] parse(Method method, InputStream in) throws IOException {
        final ClassFile classFile = method.getClassFile();
        final Stack<Expression> stack = new Stack<>();
        final ArrayList<Statement> statements = new ArrayList<>();
        final ConstantPool constantPool = classFile.getConstantPool();

        final Runnable shuffleStack = () -> {
            while (!stack.isEmpty()) {
                final Expression expression = stack.remove(0);

                if (!(expression instanceof Statement)) {
                    throw new ClassFileFormatException("Expression is not a valid statement: " + expression);
                } else {
                    statements.add((Statement) expression);
                }
            }
        };

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

                // Locals

                case ByteCode.aload_0:
                case ByteCode.aload_1:
                case ByteCode.aload_2:
                case ByteCode.aload_3: {
                    loadVariable(method, byteCode - ByteCode.aload_0, stack); // TODO Check type
                    break;
                }
                case ByteCode.iload_0:
                case ByteCode.iload_1:
                case ByteCode.iload_2:
                case ByteCode.iload_3: {
                    loadVariable(method, byteCode - ByteCode.iload_0, stack);  // TODO Check type
                    break;
                }
                case ByteCode.istore_1: {
                    final LocalVariable localVariable = method.getLocalVariableForIndex(1);
                    statements.add(new VariableAssignmentImpl(stack.pop(), localVariable.getVariableName(), localVariable.getVariableType()));
                    break;
                }

                case ByteCode.fstore_0:
                case ByteCode.fstore_1:
                case ByteCode.fstore_2:
                case ByteCode.fstore_3: {
                    final int index = byteCode - ByteCode.fstore_0;
                    storeVariable(method, index, stack, float.class);
                    break;
                }

                case ByteCode.astore_0:
                case ByteCode.astore_1:
                case ByteCode.astore_2:
                case ByteCode.astore_3: {
                    final int index = byteCode - ByteCode.astore_0;
                    storeVariable(method, index, stack, null);
                    break;
                }

                // Operators

                case ByteCode.iadd:
                    final Expression rightOperand = stack.pop();
                    final Expression leftOperand = stack.pop();

                    stack.push(new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, int.class));

                    break;

                // Push constants onto stack

                case ByteCode.bipush:
                    stack.push(new ConstantExpressionImpl(in.read(), int.class));
                    break;

                // Constants

                case ByteCode.iconst_m1:
                    stack.push(new ConstantExpressionImpl(-1, int.class));
                    break;
                case ByteCode.iconst_0:
                    stack.push(new ConstantExpressionImpl(0, int.class));
                    break;
                case ByteCode.iconst_1:
                    stack.push(new ConstantExpressionImpl(1, int.class));
                    break;
                case ByteCode.iconst_2:
                    stack.push(new ConstantExpressionImpl(2, int.class));
                    break;
                case ByteCode.iconst_3:
                    stack.push(new ConstantExpressionImpl(3, int.class));
                    break;
                case ByteCode.iconst_4:
                    stack.push(new ConstantExpressionImpl(4, int.class));
                    break;
                case ByteCode.iconst_5:
                    stack.push(new ConstantExpressionImpl(5, int.class));
                    break;
                case ByteCode.sipush:
                    stack.push(new ConstantExpressionImpl(((in.read() << 8) & 0xFF00 | in.read() & 0xFF), int.class));
                    break;

                case ByteCode.ldc1: {
                    final ConstantPoolEntry entry = constantPool.getEntry(in.read());

                    switch (entry.getTag()) {
                        case INTEGER:
                            stack.push(new ConstantExpressionImpl(((IntegerEntry) entry).getValue(), int.class));
                            break;
                        case FLOAT:
                            stack.push(new ConstantExpressionImpl(((FloatEntry) entry).getValue(), float.class));
                            break;
                        case STRING:
                            stack.push(new ConstantExpressionImpl(constantPool.getString(((StringEntry) entry).getStringIndex()), String.class));
                            break;
                    }

                    break;
                }

                // Method return

                case ByteCode.return_:
                    // Expecting empty stack; stacked expressions are statements
                    shuffleStack.run();
                    statements.add(new ReturnImpl());
                    break;
                case ByteCode.ireturn:
                    statements.add(new ReturnValueImpl(stack.pop()));
                    break;

                // Method invocation

                case ByteCode.invokeinterface: {
                    invokeMethod(in, stack, constantPool, false, true);

                    final int count = in.read();

                    if (count == 0) {
                        throw new ClassFileFormatException("Count field subsequent to interface method invocation must not be zero");
                    }

                    if (in.read() != 0) {
                        throw new ClassFileFormatException("Interface method calls must be followed by <count:byte>, 0");
                    }

                    break;
                }
                case ByteCode.invokespecial:
                    invokeMethod(in, stack, constantPool, false, false);
                    break;
                case ByteCode.invokestatic:
                    invokeMethod(in, stack, constantPool, true, false);
                    break;

                // Invalid instructions

                default:
                    throw new IllegalArgumentException("Invalid byte code " + n + " (" + ByteCode.toString(byteCode) + ")");
            }
        }

        shuffleStack.run();

        return statements.toArray(new Element[statements.size()]);
    }

    private void storeVariable(Method method, int index, Stack<Expression> stack, Class expectedType) {
        if (expectedType != null) {
            // TODO Check type
        }

        final LocalVariable localVariable = method.getLocalVariableForIndex(index);
        stack.push(new VariableAssignmentImpl(stack.pop(), localVariable.getVariableName(), localVariable.getVariableType()));
    }

    private void invokeMethod(InputStream in, Stack<Expression> stack, ConstantPool constantPool, boolean invokeStatic, boolean isInterface) throws IOException {
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
            parameters[i] = stack.pop();
        }

        final Expression targetInstance = (invokeStatic ? null : stack.pop());

        stack.push(new MethodCallImpl(resolveType(targetClassName), methodName, signature, targetInstance, parameters));
    }

    private void loadVariable(Method method, int index, Stack<Expression> stack) {
        final LocalVariable localVariable = method.getLocalVariableForIndex(index);
        stack.push(new LocalVariableReferenceImpl(localVariable.getVariableName(), localVariable.getVariableType()));
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
