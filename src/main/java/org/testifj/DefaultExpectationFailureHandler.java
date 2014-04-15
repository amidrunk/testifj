package org.testifj;

import org.testifj.lang.ByteCodeParser;
import org.testifj.lang.ClassFile;
import org.testifj.lang.ClassFileReader;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ByteCodeParserImpl;
import org.testifj.lang.impl.ClassFileReaderImpl;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Optional;

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

    private final ByteCodeParser byteCodeParser;

    private final Describer<CodePointer> syntaxElementDescriber;

    private final DescriptionFormat descriptionFormat;

    private DefaultExpectationFailureHandler(ClassFileReader classFileReader,
                                             ByteCodeParser byteCodeParser,
                                             Describer<CodePointer> syntaxElementDescriber,
                                             DescriptionFormat descriptionFormat) {
        this.classFileReader = classFileReader;
        this.byteCodeParser = byteCodeParser;
        this.syntaxElementDescriber = syntaxElementDescriber;
        this.descriptionFormat = descriptionFormat;
    }

    @Override
    public void handleExpectationFailure(ExpectationFailure failure) {
        if (failure instanceof ValueMismatchFailure) {
            final ValueMismatchFailure valueMismatchFailure = (ValueMismatchFailure) failure;
            final Description description = describe(valueMismatchFailure.getCaller(), valueMismatchFailure.getValue(), valueMismatchFailure.getExpectedValue());

            throw new AssertionError(descriptionFormat.format(description));
        }
    }

    private Description describe(Caller caller, Object actualValue, Optional<Object> expectedValue) {
        final ClassFile classFile;

        try (InputStream in = getClass().getResourceAsStream("/" + caller.getCallerStackTraceElement().getClassName().replace('.', '/') + ".class")) {
            classFile = classFileReader.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Method callerMethod = classFile.getMethods().stream()
                .filter(m -> m.getName().equals(caller.getCallerStackTraceElement().getMethodName()))
                .findFirst().get();

        final Element[] elements;

        try (InputStream codeForLineNumber = callerMethod.getCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber())) {
            elements = byteCodeParser.parse(callerMethod, codeForLineNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return describe(new CodePointer(callerMethod, elements[0]), actualValue, expectedValue);
    }

    private Description describe(CodePointer codePointer, Object actualValue, Optional<Object> expectedValue) {
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

                final Description actualValueDescription = getValueDescription(codePointer.forElement(actualValueExpression), Optional.of(actualValue));
                final Description expectedValueDescription = getValueDescription(codePointer.forElement(expectedValueExpression), expectedValue);

                return BasicDescription.from("Expected ")
                        .appendDescription(actualValueDescription)
                        .appendDescription(BasicDescription.from((inverted ? " not " : " ") + "to be "))
                        .appendDescription(expectedValueDescription);
            }
        }

        return syntaxElementDescriber.describe(codePointer);
    }

    private Description getValueDescription(CodePointer valueExpressionPointer, Optional<Object> optionalValue) {
        final Description actualValueExpressionDescription = syntaxElementDescriber.describe(valueExpressionPointer);

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

    public static final class Builder {

        private ClassFileReader classFileReader = new ClassFileReaderImpl();

        private ByteCodeParser byteCodeParser = new ByteCodeParserImpl();

        private Describer<CodePointer> syntaxElementDescriber = new MethodElementDescriber();

        private DescriptionFormat descriptionFormat = new StandardDescriptionFormat();

        public void setClassFileReader(ClassFileReader classFileReader) {
            assert classFileReader != null : "Class file reader can't be null";

            this.classFileReader = classFileReader;
        }

        public void setByteCodeParser(ByteCodeParser byteCodeParser) {
            assert byteCodeParser != null : "Byte code parser can't be null";

            this.byteCodeParser = byteCodeParser;
        }

        public void setSyntaxElementDescriber(Describer<CodePointer> syntaxElementDescriber) {
            assert syntaxElementDescriber != null : "Syntax element describer can't be null";

            this.syntaxElementDescriber = syntaxElementDescriber;
        }

        public void setDescriptionFormat(DescriptionFormat descriptionFormat) {
            assert descriptionFormat != null : "Description format can't be null";
            this.descriptionFormat = descriptionFormat;
        }

        public DefaultExpectationFailureHandler build() {
            return new DefaultExpectationFailureHandler(classFileReader, byteCodeParser, syntaxElementDescriber, descriptionFormat);
        }
    }
}
