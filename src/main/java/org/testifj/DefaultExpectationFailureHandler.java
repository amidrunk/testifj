package org.testifj;

import org.testifj.lang.classfile.ClassFileReader;
import org.testifj.lang.classfile.impl.ClassFileReaderImpl;
import org.testifj.lang.decompile.CallerDecompiler;
import org.testifj.lang.codegeneration.CodeGenerator;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.impl.CallerDecompilerImpl;
import org.testifj.lang.codegeneration.impl.CodePointerCodeGenerator;
import org.testifj.lang.decompile.impl.DecompilerImpl;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

/**
 * TODO use some MessageBuilder of sorts...
 *
 * MessageBuilder.newValueMismatch()
 *      .setExpectedExpressionDescription(...)
 *      .setExpectedValueDescription(...)
 *      .setActualValueDescription(...)
 *      .setActualValueDescription(...)
 *
 * Use advisors to enable transformation of methods etc.
 *
 * DSLElementDescriber
 */
public final class DefaultExpectationFailureHandler implements ExpectationFailureHandler {

    private final ClassFileReader classFileReader;

    private final Decompiler decompiler;

    private final CodeGenerator<CodePointer> syntaxElementCodeGenerator;

    private final DescriptionFormat descriptionFormat;

    private final CallerDecompiler callerDecompiler;

    private DefaultExpectationFailureHandler(ClassFileReader classFileReader,
                                             Decompiler decompiler,
                                             CodeGenerator<CodePointer> syntaxElementCodeGenerator,
                                             DescriptionFormat descriptionFormat) {
        this.classFileReader = classFileReader;
        this.decompiler = decompiler;
        this.syntaxElementCodeGenerator = syntaxElementCodeGenerator;
        this.descriptionFormat = descriptionFormat;
        this.callerDecompiler = new CallerDecompilerImpl(classFileReader, decompiler);
    }

    @Override
    public void handleExpectationFailure(ExpectationFailure failure) {
        if (failure instanceof ValueMismatchFailure) {
            handleValueMismatchFailure((ValueMismatchFailure) failure);
        } else if (failure instanceof ExpectedExceptionNotThrown) {
            handleExpectedExceptionNotThrownException((ExpectedExceptionNotThrown) failure);
        }
    }

    private void handleValueMismatchFailure(ValueMismatchFailure failure) {
        final Description description = forCaller(failure.getCaller(), (codePointers)
                -> describeValueMismatch(codePointers[0], failure.getValue(), failure.getExpectedValue()));

        throw new AssertionError(descriptionFormat.format(description));
    }

    private void handleExpectedExceptionNotThrownException(ExpectedExceptionNotThrown failure) {
        final Description description = forCaller(failure.getCaller(), (codePointers) -> {
            Description procedureDescription = null;
            boolean inverted = false;
            final CodePointer codePointer = codePointers[0];
            final Element element = codePointer.getElement();

            if (element.getElementType() == ElementType.METHOD_CALL) {
                final MethodCall methodCall = (MethodCall) element;

                if (methodCall.getMethodName().equals("toThrow")) {
                    MethodCall expectCall = (MethodCall) methodCall.getTargetInstance();

                    if (expectCall.getMethodName().equals("not")) {
                        expectCall = (MethodCall) expectCall.getTargetInstance();
                        inverted = true;
                    }

                    procedureDescription = syntaxElementCodeGenerator.describe(codePointer.forElement(expectCall.getParameters().get(0)));
                }
            }

            if (procedureDescription == null) {
                procedureDescription = syntaxElementCodeGenerator.describe(codePointers[0]);
            }

            return BasicDescription.from("Expected [")
                    .appendDescription(procedureDescription)
                    .appendText("] " + (inverted ? "not" : "") + " to throw " + failure.getExpectedException().getName());
        });

        throw new AssertionError(description.toString());
    }

    public static final Object NULL = new Object() {
        @Override
        public String toString() {
            return "null";
        }
    };

    private Description describeValueMismatch(CodePointer codePointer, Object actualValue, Optional<Object> expectedValue) {
        final Element element = codePointer.getElement();

        if (element.getElementType() == ElementType.METHOD_CALL) {
            final MethodCall methodCall = (MethodCall) element;

            if (methodCall.getMethodName().equals("toBe")) {
                final Expression expectedValueExpression = methodCall.getParameters().get(0);
                final MethodCall toBeContinuation = (MethodCall) methodCall.getTargetInstance();
                final boolean inverted = toBeContinuation.getMethodName().equals("not");
                final MethodCall expectCall;

                if (inverted) {
                    expectCall = (MethodCall) toBeContinuation.getTargetInstance();
                } else {
                    expectCall = toBeContinuation;
                }

                final Expression actualValueExpression = expectCall.getParameters().get(0);

                final Description actualValueDescription = getValueDescription(codePointer.forElement(actualValueExpression), Optional.of(actualValue == null ? NULL : actualValue));
                final Description expectedValueDescription = getValueDescription(codePointer.forElement(expectedValueExpression), expectedValue);

                return BasicDescription.from("Expected ")
                        .appendDescription(actualValueDescription)
                        .appendDescription(BasicDescription.from((inverted ? " not " : " ") + "to be "))
                        .appendDescription(expectedValueDescription);
            }
        }

        return syntaxElementCodeGenerator.describe(codePointer);
    }

    private Description getValueDescription(CodePointer valueExpressionPointer, Optional<Object> optionalValue) {
        final Description actualValueExpressionDescription = syntaxElementCodeGenerator.describe(valueExpressionPointer);

        if (optionalValue.isPresent()) {
            final Description actualValueDescription = new BasicDescription().appendValue(optionalValue.get());

            if (!areDescriptionsIdentical(actualValueExpressionDescription, actualValueDescription)) {
                return actualValueExpressionDescription.appendText(" => ").appendDescription(actualValueDescription);
            }
        }

        return actualValueExpressionDescription;
    }

    private boolean areDescriptionsIdentical(Description description1, Description description2) {
        final String formattedDescription1 = descriptionFormat.format(description1);
        final String formattedDescription2 = descriptionFormat.format(description2);

        return formattedDescription1.equals(formattedDescription2);
    }

    private <T> T forCaller(Caller caller, Function<CodePointer[], T> syntaxElementsHandler) {
        final CodePointer[] codePointers;

        try {
            codePointers = callerDecompiler.decompileCaller(caller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return syntaxElementsHandler.apply(codePointers);
    }

    public static final class Builder {

        private ClassFileReader classFileReader = new ClassFileReaderImpl();

        private Decompiler decompiler = new DecompilerImpl();

        private CodeGenerator<CodePointer> syntaxElementCodeGenerator = new CodePointerCodeGenerator();

        private DescriptionFormat descriptionFormat = new StandardDescriptionFormat();

        public void setClassFileReader(ClassFileReader classFileReader) {
            assert classFileReader != null : "Class file reader can't be null";

            this.classFileReader = classFileReader;
        }

        public void setDecompiler(Decompiler decompiler) {
            assert decompiler != null : "Byte code parser can't be null";

            this.decompiler = decompiler;
        }

        public void setSyntaxElementCodeGenerator(CodeGenerator<CodePointer> syntaxElementCodeGenerator) {
            assert syntaxElementCodeGenerator != null : "Syntax element describer can't be null";

            this.syntaxElementCodeGenerator = syntaxElementCodeGenerator;
        }

        public void setDescriptionFormat(DescriptionFormat descriptionFormat) {
            assert descriptionFormat != null : "Description format can't be null";
            this.descriptionFormat = descriptionFormat;
        }

        public DefaultExpectationFailureHandler build() {
            return new DefaultExpectationFailureHandler(classFileReader, decompiler, syntaxElementCodeGenerator, descriptionFormat);
        }
    }
}
