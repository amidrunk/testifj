package org.testifj.lang.impl;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import org.testifj.lang.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;
import org.testifj.lang.model.Statement;
import org.testifj.lang.model.impl.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Stack;

import static org.testifj.lang.ConstantPoolEntry.*;

public final class ByteCodeParserImpl implements ByteCodeParser {

    @Override
    public Element[] parse(ClassFile classFile, InputStream in) throws IOException {
        final Stack<Expression> stack = new Stack<>();
        final ArrayList<Statement> statements = new ArrayList<>();
        final ConstantPool constantPool = classFile.getConstantPool();

        final Runnable shuffleStack = () -> {
            if (!stack.isEmpty()) {
                if (stack.size() != 1) {
                    throw new ClassFileFormatException("Multiple elements remaining on stack: " + stack);
                } else {
                    final Expression expression = stack.pop();

                    if (!(expression instanceof Statement)) {
                        throw new ClassFormatException("Expression is not a valid statement: " + expression);
                    } else {
                        statements.add((Statement) expression);
                    }
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
                // Locals

                case ByteCode.aload_0:
                    stack.push(new LocalVariableReferenceImpl("this", resolveType(classFile.getName())));
                    break;

                // Operators

                case ByteCode.iadd:
                    final Expression rightOperand = stack.pop();
                    final Expression leftOperand = stack.pop();

                    stack.push(new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, int.class));

                    break;

                // Constants

                case ByteCode.iconst_0:
                    stack.push(new ConstantExpressionImpl(0));
                    break;
                case ByteCode.iconst_1:
                    stack.push(new ConstantExpressionImpl(1));
                    break;
                case ByteCode.iconst_2:
                    stack.push(new ConstantExpressionImpl(2));
                    break;
                case ByteCode.iconst_3:
                    stack.push(new ConstantExpressionImpl(3));
                    break;
                case ByteCode.iconst_4:
                    stack.push(new ConstantExpressionImpl(4));
                    break;
                case ByteCode.iconst_5:
                    stack.push(new ConstantExpressionImpl(5));
                    break;
                case ByteCode.sipush:
                    stack.push(new ConstantExpressionImpl(((in.read() << 8) & 0xFF00 | in.read() & 0xFF)));
                    break;

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

                case ByteCode.invokespecial:
                    final MethodRefEntry methodRef = (MethodRefEntry) constantPool.getEntry((in.read() << 8) & 0xFF00 | in.read() & 0xFF);
                    final ClassEntry classEntry = (ClassEntry) constantPool.getEntry(methodRef.getClassIndex());
                    final String targetClassName = constantPool.getString(classEntry.getNameIndex());
                    final NameAndTypeEntry methodNameAndType = (NameAndTypeEntry) constantPool.getEntry(methodRef.getNameAndTypeIndex());
                    final String methodDescriptor = constantPool.getString(methodNameAndType.getDescriptorIndex());
                    final String methodName = constantPool.getString(methodNameAndType.getNameIndex());
                    final SignatureImpl signature = SignatureImpl.parse(methodDescriptor);
                    final Expression[] parameters = new Expression[signature.getParameterTypes().size()];

                    for (int i = parameters.length - 1; i >= 0; i--) {
                        parameters[i] = stack.pop();
                    }

                    final Expression targetInstance = stack.pop();

                    stack.push(new MethodCallImpl(resolveType(targetClassName), methodName, signature, targetInstance, parameters));
                    break;

                // Invalid instructions

                default:
                    throw new IllegalArgumentException("Invalid byte code " + n + " (" + ByteCode.toString(byteCode) + ")");
            }
        }

        return statements.toArray(new Element[statements.size()]);
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
