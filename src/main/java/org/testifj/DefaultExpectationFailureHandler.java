package org.testifj;

import org.testifj.lang.*;
import org.testifj.lang.impl.ClassFileReaderImpl;
import org.testifj.lang.impl.CodePointerCodeGenerator;
import org.testifj.lang.impl.CodePointerImpl;
import org.testifj.lang.impl.DecompilerImpl;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiFunction;

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

    private DefaultExpectationFailureHandler(ClassFileReader classFileReader,
                                             Decompiler decompiler,
                                             CodeGenerator<CodePointer> syntaxElementCodeGenerator,
                                             DescriptionFormat descriptionFormat) {
        this.classFileReader = classFileReader;
        this.decompiler = decompiler;
        this.syntaxElementCodeGenerator = syntaxElementCodeGenerator;
        this.descriptionFormat = descriptionFormat;
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
        final Description description = forCaller(failure.getCaller(), (method,elements)
                -> describeValueMismatch(new CodePointerImpl(method, elements[0]), failure.getValue(), failure.getExpectedValue()));

        throw new AssertionError(descriptionFormat.format(description));
    }

    private void handleExpectedExceptionNotThrownException(ExpectedExceptionNotThrown failure) {
        final Description description = forCaller(failure.getCaller(), (method, elements) -> {
            Description procedureDescription = null;
            boolean inverted = false;

            if (elements[0].getElementType() == ElementType.METHOD_CALL) {
                final MethodCall methodCall = (MethodCall) elements[0];

                if (methodCall.getMethodName().equals("toThrow")) {
                    MethodCall expectCall = (MethodCall) methodCall.getTargetInstance();

                    if (expectCall.getMethodName().equals("not")) {
                        expectCall = (MethodCall) expectCall.getTargetInstance();
                        inverted = true;
                    }

                    procedureDescription = syntaxElementCodeGenerator.describe(new CodePointerImpl(method, expectCall.getParameters().get(0)));
                }
            }

            if (procedureDescription == null) {
                procedureDescription = syntaxElementCodeGenerator.describe(new CodePointerImpl(method, elements[0]));
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

    private <T> T forCaller(Caller caller, BiFunction<Method, Element[], T> syntaxElementsHandler) {
        final Method callerMethod = loadMethod(caller, true);
        final Element[] elements = elementsForLine(caller, callerMethod);

        return syntaxElementsHandler.apply(callerMethod, elements);
    }

    private Element[] elementsForLine(Caller caller, Method callerMethod) {
        final Element[] elements;

        // TODO This can fail... need to parse the entire method and pick out the matching element(s)
        try (InputStream codeForLineNumber = callerMethod.getCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber())) {
            elements = decompiler.parse(callerMethod, codeForLineNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return elements;
    }

    private Method loadMethod(Caller caller, boolean resolveLocalVariableTable) {
        final ClassFile classFile;

        try (InputStream in = getClass().getResourceAsStream("/" + caller.getCallerStackTraceElement().getClassName().replace('.', '/') + ".class")) {
            if (in == null) {
                return loadMethod(new Caller(caller.getCallStack(), caller.getCallerStackTraceIndex() + 1), true);
            }

            classFile = classFileReader.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Method method = classFile.getMethods().stream()
                .filter(m -> m.getName().equals(caller.getCallerStackTraceElement().getMethodName()))
                .filter(m -> m.hasCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber()))
                .findFirst().get();

        if (!method.getLocalVariableTable().isPresent()) {
            if (resolveLocalVariableTable) {
                final Caller rootCaller = new Caller(caller.getCallStack(), caller.getCallerStackTraceIndex() + 1);
                final Method enclosure = loadMethod(rootCaller, false);

                int n = 100;
            }
        }

        return method;
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
