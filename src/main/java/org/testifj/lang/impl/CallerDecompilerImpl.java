package org.testifj.lang.impl;

import org.testifj.Caller;
import org.testifj.lang.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

public final class CallerDecompilerImpl implements CallerDecompiler {

    private final ClassFileReader classFileReader;

    private final Decompiler decompiler;

    public CallerDecompilerImpl() {
        this(new ClassFileReaderImpl(), new DecompilerImpl());
    }

    public CallerDecompilerImpl(ClassFileReader classFileReader, Decompiler decompiler) {
        this.classFileReader = classFileReader;
        this.decompiler = decompiler;
    }

    @Override
    public CodePointer[] decompileCaller(Caller caller) throws IOException {
        assert caller != null : "Caller can't be null";

        return codeForCaller(caller);
    }

    private CodePointer[] codeForCaller(Caller caller) throws IOException {
        final ClassFile classFile = loadClassFile(caller.getCallerStackTraceElement().getClassName());

        if (classFile == null) {
            return null;
        }

        final Method method = classFile.getMethods().stream()
                .filter(m -> m.getName().equals(caller.getCallerStackTraceElement().getMethodName()))
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
                }).findFirst().orElseThrow(() -> new IllegalStateException("Method not found"));

        try (InputStream in = method.getCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber())) {
            final Element[] elements = decompiler.parse(method, in);

            return Arrays.stream(elements).map(e -> new CodePointerImpl<>(method, e)).toArray(CodePointer[]::new);
        }
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
