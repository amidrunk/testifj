package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.*;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.codegeneration.impl.CodePointerCodeGenerator;
import org.testifj.lang.codegeneration.impl.JavaSyntaxCodeGeneration;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.DefaultModelFactory;
import org.testifj.util.*;

import java.io.EOFException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DecompilerImpl implements Decompiler {

    private final DecompilerConfiguration configuration;

    private final CodePointerCodeGenerator debugCodeGenerator;

    public DecompilerImpl() {
        this(CoreDecompilerDelegation.configuration());
    }

    public DecompilerImpl(DecompilerConfiguration configuration) {
        assert configuration != null : "Configuration can't be null";

        this.configuration = configuration;
        this.debugCodeGenerator = new CodePointerCodeGenerator(this, JavaSyntaxCodeGeneration.configuration());
    }

    private void debug(DecompilationContext context, int lineNumber, int byteCode) {
        final String stackedExpressions = context.getStackedExpressions().stream()
                .map(e -> debugCodeGenerator.describe(new CodePointerImpl<>(context.getMethod(), e)).toString())
                .collect(Collectors.joining(", "));

        final String statements = context.getStatements().stream()
                .map(e -> debugCodeGenerator.describe(new CodePointerImpl<>(context.getMethod(), e)).toString())
                .collect(Collectors.joining(", "));

        System.out.println("\t[" + Strings.rightPad(String.valueOf(lineNumber), 3, ' ') + ", " + Strings.rightPad(String.valueOf(context.getProgramCounter().get()), 3, ' ') + "] " + Strings.rightPad(ByteCode.toString(byteCode), 20, ' ') + " <-- [" + stackedExpressions + " # " + statements + "]");
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

    private static ThreadLocal<AtomicBoolean> DEBUG_TL = new ThreadLocal<AtomicBoolean>() {
        @Override
        protected AtomicBoolean initialValue() {
            return new AtomicBoolean();
        }
    };

    public Element[] parse(Method method, CodeStream codeStream, DecompilationProgressCallback callback) throws IOException {
        final Optional<LineNumberTable> lineNumberTable = method.getLineNumberTable();

        final LineNumberCounter lineNumberCounter;

        if (!lineNumberTable.isPresent()) {
            lineNumberCounter = new NullLineNumberCounter();
        } else {
            lineNumberCounter = new LineNumberCounterImpl(codeStream.pc(), lineNumberTable.get());
        }

        final InstructionContextImpl instructionContext = new InstructionContextImpl();

        final ModelFactory modelFactory = new TransformingModelFactory(new DefaultModelFactory(() -> new ElementContextMetaData(instructionContext.getProgramCounter(), instructionContext.getLineNumber())), transformElement());

        final TransformedStack<Expression, Expression> stack = new TransformedStack<>(new SingleThreadedStack<>(), transformElement(modelFactory), Function.identity());

        final TransformedSequence<Statement, Statement> statements = new TransformedSequence<>(new LinkedSequence<>(), transformElement(modelFactory), Function.identity());

        final DecompilationContext context = new DecompilationContextImpl.Builder()
                .setDecompiler(this)
                .setMethod(method)
                .setProgramCounter(codeStream.pc())
                .setLineNumberCounter(lineNumberCounter)
                .setTypeResolver(new SimpleTypeResolver())
                .setStack(stack)
                .setStatements(statements)
                .setModelFactory(modelFactory)
                .setStartPC(codeStream.pc().get())
                .setInstructionContext(instructionContext)
                .build();

        boolean debug = ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(s -> s.contains("-agentlib:jdwp"))
                .findAny()
                .isPresent() && !DEBUG_TL.get().get();

        if (debug) {
            DEBUG_TL.get().set(true);
            System.out.println(method.getClassFile().getName() + "#" + method.getName() + "[" + codeStream.pc().get() + "/" + lineNumberCounter.get() + "]:");
        }

        while (!context.isAborted()) {
            final int byteCode;

            try {
                byteCode = codeStream.nextInstruction();
            } catch (EOFException e) {
                break;
            }

            instructionContext.update(byteCode, codeStream.pc().get(), lineNumberCounter.get());

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

        if (debug) {
            DEBUG_TL.get().set(false);
        }

        return context.getStatements().all().get().stream().toArray(Element[]::new);
    }

    private Function transformElement() {
        return new Function<Element, Element>() {
            @Override
            public Element apply(Element element) {
                while (true) {
                    boolean anyTransformationApplied = false;

                    for (ModelTransformation transformation : configuration.getTransformations(element.getElementType())) {
                        final Optional result = transformation.apply(element);

                        if (result.isPresent()) {
                            anyTransformationApplied = true;
                            element = (Expression) result.get();
                            break;
                        }
                    }

                    if (!anyTransformationApplied) {
                        break;
                    }
                }

                return element;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Function transformElement(ModelFactory modelFactory) {
        return new Function<Element, Element>() {
            @Override
            public Element apply(Element element) {
                while (true) {
                    if (!element.getMetaData().hasProgramCounter()) {
                        element = modelFactory.createFrom(element);
                    }

                    boolean anyTransformationApplied = false;

                    for (ModelTransformation transformation : configuration.getTransformations(element.getElementType())) {
                        final Optional result = transformation.apply(element);

                        if (result.isPresent()) {
                            anyTransformationApplied = true;
                            element = (Expression) result.get();
                            break;
                        }
                    }

                    if (!anyTransformationApplied) {
                        break;
                    }
                }

                return element;
            }
        };
    }

    private static final class InstructionContextImpl implements InstructionContext {

        private volatile int byteCode = -1;

        private volatile int programCounter = -1;

        private volatile int lineNumber = -1;

        @Override
        public int getByteCode() {
            return byteCode;
        }

        @Override
        public int getProgramCounter() {
            return programCounter;
        }

        @Override
        public int getLineNumber() {
            return lineNumber;
        }

        protected void update(int byteCode, int programCounter, int lineNumber) {
            this.byteCode = byteCode;
            this.programCounter = programCounter;
            this.lineNumber = lineNumber;
        }
    }

}
