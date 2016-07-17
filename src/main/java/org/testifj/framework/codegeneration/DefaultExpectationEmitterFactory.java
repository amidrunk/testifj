package org.testifj.framework.codegeneration;

import io.recode.CodeLocation;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.CodePointer;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;
import io.recode.model.ElementType;
import io.recode.model.MethodCall;
import org.testifj.framework.BehaviouralExpectation;
import org.testifj.framework.Expectation;
import org.testifj.framework.InlineExpectationReference;
import org.testifj.framework.ValueExpectation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class DefaultExpectationEmitterFactory implements ExpectationEmitterFactory {

    private final CodeLocationDecompiler codeLocationDecompiler;

    public DefaultExpectationEmitterFactory() {
        this(new CodeLocationDecompilerImpl());
    }

    public DefaultExpectationEmitterFactory(CodeLocationDecompiler codeLocationDecompiler) {
        assert codeLocationDecompiler != null : "codeLocationDecompiler can't be null";
        this.codeLocationDecompiler = codeLocationDecompiler;
    }

    @Override
    public ExpectationEmitter createContext(Expectation expectation) {
        assert expectation != null : "expectation can't be null";

        if (!(expectation instanceof ValueExpectation) && !(expectation instanceof BehaviouralExpectation)) {
            throw new IllegalArgumentException("expectation must be a ValueExpectation or a BehaviouralExpectation");
        }

        if (!(expectation.getExpectationReference() instanceof InlineExpectationReference)) {
            throw new IllegalArgumentException("expectation reference must be an InlineExpectationReference");
        }

        final InlineExpectationReference expectationReference = (InlineExpectationReference) expectation.getExpectationReference();
        final CodeLocation codeLocation = expectationReference.toCodeLocation();

        final CodePointer[] codePointers;

        try {
            codePointers = codeLocationDecompiler.decompileCodeLocation(codeLocation);
        } catch (IOException e) {
            throw new CodeGenerationException("The code referenced by the expectation could not be decompiled. " +
                    "See cause exception for more details. " +
                    "If this is a bug, please report it at http://www.testifj.org.", e);
        }

        final CodePointer codePointer = Arrays.stream(codePointers)
                .filter(cp -> cp.getElement().getElementType() == ElementType.METHOD_CALL)
                .filter(cp -> !cp.getElement().as(MethodCall.class).getMethodName().equals("getClass")) // This is a javac/jvm hack when passing method references
                .findFirst()
                .orElseThrow(() -> new CodeGenerationException("Method call not found on " + expectationReference.getClassName() + "::" + expectationReference.getMethodName() + "[" + expectationReference.getLineNumber() + "]"));

        if (codePointer.getElement().getElementType() != ElementType.METHOD_CALL) {
            throw new CodeGenerationException("The provided expectation reference at " + codeLocation.getClassName()
                    + "::" + codeLocation.getMethodName() + "[" + codeLocation.getLineNumber() + "] does not reference a method call");
        }

        return new Emitter(expectation, codePointer, CodeGenerationUtil.unpackCallChain(codePointer));
    }

    private static final class Emitter implements ExpectationEmitter {

        private final Expectation expectation;

        private final CodePointer<MethodCall> codePointer;

        private final List<CodePointer<MethodCall>> methodCalls;

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private final PrintWriter out = new PrintWriter(baos);

        private Emitter(Expectation expectation, CodePointer codePointer, List<CodePointer<MethodCall>> methodCalls) {
            this.expectation = expectation;
            this.codePointer = codePointer;
            this.methodCalls = methodCalls;
        }

        @Override
        public Expectation getExpectation() {
            return expectation;
        }

        @Override
        public List<CodePointer<MethodCall>> getMethodCalls() {
            return Collections.unmodifiableList(methodCalls);
        }

        @Override
        public PrintWriter out() {
            return out;
        }

        @Override
        public ExpectationEmitter emit(String string) {
            assert string != null : "string can't be null";

            out.append(string);

            return this;
        }

        @Override
        public String collect(Charset charset) {
            assert charset != null : "charset can't be null";

            out.flush();

            return new String(baos.toByteArray(), charset);
        }
    }
}
