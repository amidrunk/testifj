package org.testifj.framework.codegeneration;

import io.recode.decompile.CodePointer;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import io.recode.model.MethodCall;
import io.recode.model.TypeCast;
import org.testifj.Expectations;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class CodeGenerationUtil {

    /**
     * Unpacks a call chain such that <code>a().b().c()</code> becomes a list of three method calls.
     *
     * @param codePointer The code pointer referencing the method call.
     * @return A list of references to the chained method calls.
     */
    public static List<CodePointer<MethodCall>> unpackCallChain(CodePointer<MethodCall> codePointer) {
        final LinkedList<CodePointer<MethodCall>> methodCalls = new LinkedList<>();

        for (; ; ) {
            final MethodCall currentMethodCall = codePointer.getElement();

            methodCalls.add(0, codePointer);

            if (currentMethodCall.getMethodName().equals("expect") && currentMethodCall.getTargetType().equals(Expectations.class)) {
                break;
            }

            final Expression targetInstance = unwrapInstance(currentMethodCall.getTargetInstance());

            if (targetInstance.getElementType() != ElementType.METHOD_CALL) {
                throw new IllegalArgumentException("Expectations must be chained method calls, but " + codePointer.getElement().getMethodName() + " is called on [" + targetInstance + "]:" + targetInstance.getElementType() + " which is not a method call");
            }

            codePointer = codePointer.forElement(targetInstance.as(MethodCall.class));
        }

        return methodCalls;
    }

    /**
     * Unwraps the provided instance. If the instance represents a cast, the target expression of the cast will be
     * returned since the cast is merely a type checking feature and adds no semantic value to the expectation.
     *
     * @param targetInstance The target instance that should be unwrapped.
     * @return An unwrapped instance, i.e. "((String) something).substring(...)" returns "something.substring(...)".
     */
    public static Expression unwrapInstance(Expression targetInstance) {
        while (true) {
            if (targetInstance.getElementType() == ElementType.CAST) {
                targetInstance = targetInstance.as(TypeCast.class).getValue();
            } else {
                return targetInstance;
            }
        }
    }

    /**
     * Strips the lambda prefix from the given syntax, so that <code>() -> foo()</code> becomes <code>foo()</code>.
     * This makes the generated text easier to read.
     *
     * @param lambda The lambda from which the prefix should be stripped.
     * @return The lambda without the prefix.
     */
    public static String stripLambdaPrefix(String lambda) {
        assert lambda != null : "lambda can't be null";

        final int arrowIndex = lambda.indexOf(" -> ");

        if (arrowIndex == -1) {
            return lambda;
        } else {
            return lambda.substring(arrowIndex + 4);
        }
    }
}
