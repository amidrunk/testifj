package org.testifj.lang.decompile.impl;

import org.testifj.CodeLocation;
import org.testifj.lang.Methods;
import org.testifj.lang.Range;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.ClassFileReader;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.ClassFileReaderImpl;
import org.testifj.lang.classfile.impl.Lambdas;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;
import org.testifj.util.Sequence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class CodeLocationDecompilerImpl implements CodeLocationDecompiler {

    private final ClassFileReader classFileReader;

    private final Decompiler decompiler;

    public CodeLocationDecompilerImpl() {
        this(new ClassFileReaderImpl(), new DecompilerImpl());
    }

    public CodeLocationDecompilerImpl(ClassFileReader classFileReader, Decompiler decompiler) {
        this.classFileReader = classFileReader;
        this.decompiler = decompiler;
    }

    @Override
    public CodePointer[] decompileCodeLocation(CodeLocation codeLocation) throws IOException {
        return decompileCodeLocation(codeLocation, DecompilationProgressCallback.NULL);
    }

    public CodePointer[] decompileCodeLocation(CodeLocation codeLocation, DecompilationProgressCallback callback) throws IOException {
        assert codeLocation != null : "codeLocation can't be null";
        assert callback != null : "callback can't be null";

        return codeForCaller(codeLocation, callback);
    }

    private CodePointer[] codeForCaller(CodeLocation codeLocation, DecompilationProgressCallback callback) throws IOException {
        final ClassFile classFile = loadClassFile(codeLocation.getClassName());

        if (classFile == null) {
            return null;
        }

        final Method method = resolveMethodFromClassFile(classFile, codeLocation);
        final Range codeRange = method.getCodeRangeForLineNumber(codeLocation.getLineNumber());

        try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
            code.skip(codeRange.getFrom());

            final AtomicReference<Expression> lingeringExpression = new AtomicReference<>();
            final AtomicInteger exitStackSize = new AtomicInteger(-1);

            final Element[] elements = decompiler.parse(method, code, new CompositeDecompilationProgressCallback(new DecompilationProgressCallbackAdapter() {
                @Override
                public void afterInstruction(DecompilationContext context) {
                    // Abort as soon as (a) we've exceeded the PC and (b) the stack is empty
                    if (context.getProgramCounter().get() >= codeRange.getTo()) {
                        final List<Expression> stackedExpressions = context.getStackedExpressions();

                        exitStackSize.compareAndSet(-1, stackedExpressions.size());

                        if (stackedExpressions.isEmpty()) {
                            context.abort();
                        } else {
                            if (lingeringExpression.get() == null) {
                                if (stackedExpressions.size() == 1) {
                                    lingeringExpression.set(stackedExpressions.get(0));
                                }
                            } else {
                                if (stackedExpressions.size() > exitStackSize.get()) {
                                    context.pop();
                                    context.abort();
                                } else {
                                    final Sequence<Statement> statements = context.getStatements();

                                    if (!statements.isEmpty() && statements.last().get().equals(lingeringExpression.get())) {
                                        context.pop();
                                        context.abort();
                                    }
                                }
                            }
                        }
                    }
                }
            }, callback));

            return Arrays.stream(elements).map(e -> new CodePointerImpl<>(method, e)).toArray(CodePointer[]::new);
        }
    }

    private Method resolveMethodFromClassFile(ClassFile classFile, CodeLocation codeLocation) {
        return classFile.getMethods().stream()
                .filter(m -> m.getName().equals(codeLocation.getMethodName()))
                .filter(m -> Methods.containsLineNumber(m, codeLocation.getLineNumber()))
                .map(m -> {
                    if (!m.isLambdaBackingMethod()) {
                        return m;
                    }

                    try {
                        return Lambdas.withEnclosedVariables(decompiler, m);
                    } catch (IOException e) {
                        // Ignore and hope for the best
                        return m;
                    }
                }).findFirst().orElseThrow(() -> new IllegalStateException("Method '" + codeLocation.getMethodName() + "' not found on line number " + codeLocation.getLineNumber() + " in class '" + classFile.getName() + "'"));
    }

    private ClassFile loadClassFile(String className) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/" + className.replace('.', '/') + ".class")) {
            if (in == null) {
                return null;
            }

            return classFileReader.read(in);
        }
    }
}