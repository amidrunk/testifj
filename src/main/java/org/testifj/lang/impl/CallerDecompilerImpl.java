package org.testifj.lang.impl;

import org.testifj.Caller;
import org.testifj.lang.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class CallerDecompilerImpl implements CallerDecompiler {

    private final ClassFileReader classFileReader = new ClassFileReaderImpl();

    private final Decompiler decompiler = new DecompilerImpl();

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
                .findFirst().orElseThrow(() -> new IllegalStateException("Method not found"));

        try (InputStream in = method.getCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber())) {
            return Arrays.stream(decompiler.parse(method, in)).map(e -> new CodePointerImpl(method, e)).toArray(CodePointer[]::new);
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
