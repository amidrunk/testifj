package org.testifj.delegate;

import org.testifj.BasicDescription;
import org.testifj.Description;
import org.testifj.lang.decompile.CallerDecompiler;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.codegeneration.impl.CodePointerCodeGenerator;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;

import java.io.IOException;

public final class GivenThenExpectationDelegateExtension implements ExpectationDelegateExtension<GivenThenExpectation> {

    @Override
    public ExpectationVerification verify(ExpectationVerificationContext<GivenThenExpectation> context) {
        assert context != null : "Expectation context can't be null";

        final GivenThenExpectation expectation = context.getExpectation();

        try {
            expectation.getVerificationAction().execute(expectation.getProvidedValue());
        } catch (AssertionError e) {
            return ExpectationVerificationImpl.notCompliant(context, describe(context), BasicDescription.from(e.getMessage()));
        } catch (Exception e) {
            // TODO: Handle
            throw new RuntimeException(e);
        }

        return ExpectationVerificationImpl.compliant(context, describe(context));
    }

    @Override
    public Description describe(ExpectationVerificationContext<GivenThenExpectation> context) {
        final CodePointer[] codePointers;

        try {
            codePointers = context.get(CallerDecompiler.class).decompileCaller(context.getExpectation().getCaller());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final CodePointer codePointer = codePointers[0];
        final MethodCall thenMethodCall = (MethodCall) codePointer.getElement();
        final MethodCall givenMethodCall = (MethodCall) thenMethodCall.getTargetInstance();
        final Expression givenValueExpression = givenMethodCall.getParameters().get(0);
        final Expression specificationExpression = thenMethodCall.getParameters().get(0);

        final CodePointerCodeGenerator codeGenerator = context.get(CodePointerCodeGenerator.class);
        final Description givenValueDescription = codeGenerator.describe(codePointer.forElement(givenValueExpression));
        final Description specificationDescription = codeGenerator.describe(codePointer.forElement(specificationExpression));

        return BasicDescription
                .from("Given ")
                .appendDescription(givenValueDescription)
                .appendText(" then ")
                .appendDescription(specificationDescription);
    }
}
