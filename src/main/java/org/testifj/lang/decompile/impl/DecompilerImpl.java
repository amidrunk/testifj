package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.LineNumberTable;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Element;
import org.testifj.util.Strings;

import java.io.EOFException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Iterator;
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

        final int startPC = codeStream.pc().get();
        final DecompilationContext context = new DecompilationContextImpl(this, method, codeStream.pc(), lineNumberCounter, new SimpleTypeResolver(), startPC);

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
                break;
            }

            if (debug) {
                debug(context, lineNumberCounter.get(), byteCode);
            }

            advice(configuration, context, codeStream, byteCode);

            final DecompilerDelegate delegate = configuration.getDecompilerDelegate(context, byteCode);

            if (delegate != null) {
                delegate.apply(context, codeStream, byteCode);
            }

            correct(configuration, context, codeStream, byteCode);

            callback.afterInstruction(context);
        }

        context.reduceAll();

        return context.getStatements().all().get().stream().toArray(Element[]::new);
    }
}
