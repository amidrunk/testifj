package org.testifj.lang.decompile.impl;

import org.testifj.lang.*;
import org.testifj.lang.classfile.*;
import org.testifj.lang.classfile.impl.ByteCodes;
import org.testifj.lang.decompile.*;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;
import org.testifj.util.Strings;

import java.io.EOFException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class DecompilerImpl implements Decompiler {

    private final DecompilerConfiguration configuration;

    private final CodePointerCodeGenerator debugCodeGenerator;

    public DecompilerImpl() {
        this(CoreDecompilerDelegation.configuration());
    }

    public DecompilerImpl(DecompilerConfiguration configuration) {
        assert configuration != null : "Configuration can't be null";

        this.configuration = configuration;
        this.debugCodeGenerator = new CodePointerCodeGenerator(this);
    }

    private void debug(DecompilationContext context, int lineNumber, int byteCode) {
        final String[] stackedExpressions = context.getStackedExpressions().stream()
                .map(e -> debugCodeGenerator.describe(new CodePointerImpl<>(context.getMethod(), e)).toString())
                .toArray(String[]::new);

        System.out.println("\t[" + Strings.rightPad(String.valueOf(lineNumber), 3, ' ') + "] " + Strings.rightPad(ByteCode.toString(byteCode), 20, ' ') + " <-- " + Arrays.asList(stackedExpressions));
    }

    @Override
    public Element[] parse(Method method, CodeStream stream) throws IOException {
        return parse(method, stream, DecompilationProgressCallback.NULL);
    }

    private void advice(DecompilerConfiguration configuration, DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
        for (Iterator<DecompilerDelegate> iterator = configuration.getAdvisoryDecompilerEnhancements(context, byteCode); iterator.hasNext(); ) {
            iterator.next().apply(context, codeStream, byteCode);
        }
    }

    private void correct(DecompilerConfiguration configuration, DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
        for (Iterator<DecompilerDelegate> iterator = configuration.getCorrectionalDecompilerEnhancements(context, byteCode); iterator.hasNext(); ) {
            iterator.next().apply(context, codeStream, byteCode);
        }
    }

    public Element[] parse(Method method, CodeStream codeStream, DecompilationProgressCallback callback) throws IOException {
        final ClassFile classFile = method.getClassFile();
        final ConstantPool constantPool = classFile.getConstantPool();
        final Optional<LineNumberTable> lineNumberTable = method.getLineNumberTable();

        final LineNumberCounter lineNumberCounter;

        if (!lineNumberTable.isPresent()) {
            lineNumberCounter = new NullLineNumberCounter();
        } else {
            lineNumberCounter = new LineNumberCounterImpl(codeStream.pc(), lineNumberTable.get());
        }

        final DecompilationContext context = new DecompilationContextImpl(this, method, codeStream.pc(), lineNumberCounter, new SimpleTypeResolver());
        final int startPC = codeStream.pc().get();

        boolean debug = ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(s -> s.contains("-agentlib:jdwp"))
                .findAny()
                .isPresent();

        if (debug) {
            System.out.println(method.getClassFile().getName() + "#" + method.getName() + "[" + codeStream.pc().get() + "/" + lineNumberCounter.get() + "]:");
        }

        while (!context.isAborted()) {
            final int byteCode;

            try {
                byteCode = codeStream.nextInstruction();
            } catch (EOFException e) {
                // If stack is not reducable, we need to look ahead a bit...
                break;
            }

            if (debug) {
                debug(context, lineNumberCounter.get(), byteCode);
            }

            advice(configuration, context, codeStream, byteCode);

            final DecompilerDelegate delegate = configuration.getDecompilerExtension(context, byteCode);

            boolean handled = false;

            if (delegate != null) {
                delegate.apply(context, codeStream, byteCode);
                handled = true;
            }

            if (!handled) {
                switch (byteCode) {
                    // Various

                    // Control flow

                    case ByteCode.if_acmpne:
                    case ByteCode.if_icmpne: {
                        final int targetPC = context.getProgramCounter().get() + codeStream.nextUnsignedShort();
                        final Expression rightOperand = context.pop();
                        final Expression leftOperand = context.pop();

                        context.enlist(new BranchImpl(leftOperand, OperatorType.NE, rightOperand, targetPC));
                        break;
                    }

                    case ByteCode.goto_: {
                        // TODO This should be some added feature that hooks goto and matches a sequence

                        final Optional<ExceptionTableEntry> exceptionTableEntry = Methods.getExceptionTableEntryForCatchLocation(method, codeStream.pc().get());

                        if (exceptionTableEntry.isPresent()) {
                            System.out.println("TODO: This MUST be handled! Find corresponding try - if pc at start of decompile is before try - ignore it and escape immediately! Or set the decompiler in an must-get-abort-immediately state");
                            context.abort();
                            break;
                        }

                        final int relativePC = context.getProgramCounter().get();
                        final int offset = codeStream.nextSignedShort();
                        final int targetPC = relativePC + offset;

                        // TODO Put startPC in context and place this in an extension
                        if (targetPC < startPC) {
                            context.abort();
                            break;
                        }

                        if (context.hasStackedExpressions()) {
                            final List<Statement> statements = context.getStatements();
                            final Statement lastStatement = statements.get(statements.size() - 1);

                            context.getProgramCounter().lookAhead(targetPC, () -> {
                                final Expression leftBranch = context.pop();
                                final Expression rightBranch = context.pop();
                                final Branch branchExpression = (Branch) lastStatement;

                                if (leftBranch.getElementType() == ElementType.CONSTANT && rightBranch.getElementType() == ElementType.CONSTANT) {
                                    final Constant trueValue = (Constant) leftBranch;
                                    final Constant falseValue = (Constant) rightBranch;

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
                    case ByteCode.invokevirtual: {
                        invokeMethod(context, codeStream, constantPool, false, false);
                        break;
                    }
                    case ByteCode.invokestatic:
                        invokeMethod(context, codeStream, constantPool, true, false);
                        break;

                    // Invalid instructions

                    default:
                        throw new IllegalArgumentException("Invalid byte code " + byteCode + " (" + ByteCode.toString(byteCode) + ") in method " + method.getName());
                }
            }

            correct(configuration, context, codeStream, byteCode);

            callback.afterInstruction(context);
        }

        //debug(context, -1, ByteCode.nop);

        context.reduceAll();

        return context.getStatements().stream().toArray(Element[]::new);
    }

    private void invokeMethod(DecompilationContext context, CodeStream stream, ConstantPool constantPool, boolean invokeStatic, boolean isInterface) throws IOException {
        final int methodRefIndex = stream.nextUnsignedShort();
        final MethodRefDescriptor methodRefDescriptor = constantPool.getDescriptor(methodRefIndex, MethodRefDescriptor.class);
        final String targetClassName = methodRefDescriptor.getClassName();
        final String methodDescriptor = methodRefDescriptor.getDescriptor();
        final String methodName = methodRefDescriptor.getMethodName();
        final MethodSignature signature = MethodSignature.parse(methodDescriptor);
        final Expression[] parameters = new Expression[signature.getParameterTypes().size()];

        for (int i = parameters.length - 1; i >= 0; i--) {
            parameters[i] = context.pop();
        }

        final Expression targetInstance = (invokeStatic ? null : context.pop());
        final MethodCallImpl methodCall = new MethodCallImpl(resolveType(targetClassName), methodName, signature, targetInstance, parameters);

        context.push(methodCall);

        // static, void call always a stand-alone statement (TODO this is a reduce point or an enhancement maybe?)
        if (methodCall.isStatic() && methodCall.getType().equals(void.class)) {
            context.reduceAll();
        }
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
