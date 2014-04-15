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

    private final Describer<Element> syntaxElementDescriber;

    private DefaultExpectationFailureHandler(ClassFileReader classFileReader,
                                             ByteCodeParser byteCodeParser,
                                             Describer<Element> syntaxElementDescriber) {
        this.classFileReader = classFileReader;
        this.byteCodeParser = byteCodeParser;
        this.syntaxElementDescriber = syntaxElementDescriber;
    }

    @Override
    public void handleExpectationFailure(ExpectationFailure failure) {

        if (failure instanceof ValueMismatchFailure) {
            final ValueMismatchFailure valueMismatchFailure = (ValueMismatchFailure) failure;

            if (valueMismatchFailure.getExpectedValue().isPresent()) {
                final String expectationDescription = describe(failure.getCaller(), valueMismatchFailure.getValue());

                throw new AssertionError("Expected " + expectationDescription + " to be " + valueToString(valueMismatchFailure.getExpectedValue().get()));
            }

            throw new AssertionError("Was: '" + valueMismatchFailure.getValue() + "'");
        }
    }

    private String describe(Caller caller, Object actualValue) {
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

        return describe(elements[0], actualValue);
    }

    private String describe(Element element, Object actualValue) {
        if (element.getElementType() == ElementType.METHOD_CALL) {
            final MethodCall methodCall = (MethodCall) element;

            if (methodCall.getMethodName().equals("toBe")) {
                final Expression expectedValue = methodCall.getParameters().get(0);
                final MethodCall expectCall = (MethodCall) methodCall.getTargetInstance();
                final Expression actualValueExpression = expectCall.getParameters().get(0);
                final String actualValueExpressionDescription = syntaxElementDescriber.describe(actualValueExpression);
                final String actualValueDescription = valueToString(actualValue);

                if (actualValueDescription.equals(actualValueExpressionDescription)) {
                    return actualValueDescription;
                } else {
                    return actualValueExpressionDescription + " => " + actualValueDescription;
                }
            }
        }

        return syntaxElementDescriber.describe(element);
    }

    private String valueToString(Object actualValue) {
        // TODO Use some advisor to do this

        if (actualValue == null) {
            return "null";
        }

        if (actualValue instanceof String) {
            return "\"" + actualValue + "\"";
        }

        if (actualValue.getClass().isArray()) {
            final StringBuilder buffer = new StringBuilder();
            final int length = Array.getLength(actualValue);

            buffer.append("[");

            for (int i = 0; i < length; i++) {
                buffer.append(valueToString(Array.get(actualValue, i)));

                if (i != length - 1) {
                    buffer.append(", ");
                }
            }

            buffer.append("]");

            return buffer.toString();
        }

        return String.valueOf(actualValue);
    }

    public static final class Builder {

        private ClassFileReader classFileReader = new ClassFileReaderImpl();

        private ByteCodeParser byteCodeParser = new ByteCodeParserImpl();

        private Describer<Element> syntaxElementDescriber = new MethodElementDescriber();

        public void setClassFileReader(ClassFileReader classFileReader) {
            assert classFileReader != null : "Class file reader can't be null";

            this.classFileReader = classFileReader;
        }

        public void setByteCodeParser(ByteCodeParser byteCodeParser) {
            assert byteCodeParser != null : "Byte code parser can't be null";

            this.byteCodeParser = byteCodeParser;
        }

        public void setSyntaxElementDescriber(Describer<Element> syntaxElementDescriber) {
            assert syntaxElementDescriber != null : "Syntax element describer can't be null";

            this.syntaxElementDescriber = syntaxElementDescriber;
        }

        public DefaultExpectationFailureHandler build() {
            return new DefaultExpectationFailureHandler(classFileReader, byteCodeParser, syntaxElementDescriber);
        }

    }
}
