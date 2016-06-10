package org.testifj.delegate;

import org.testifj.BasicDescription;
import org.testifj.Description;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.CodePointer;
import io.recode.codegeneration.impl.CodePointerCodeGenerator;
import io.recode.model.Expression;
import io.recode.model.MethodCall;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
            codePointers = context.get(CodeLocationDecompiler.class).decompileCodeLocation(context.getExpectation().getCaller());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final CodePointer codePointer = codePointers[0];
        final MethodCall thenMethodCall = (MethodCall) codePointer.getElement();
        final MethodCall givenMethodCall = (MethodCall) thenMethodCall.getTargetInstance();
        final Expression givenValueExpression = givenMethodCall.getParameters().get(0);
        final Expression specificationExpression = thenMethodCall.getParameters().get(0);

        final CodePointerCodeGenerator codeGenerator = context.get(CodePointerCodeGenerator.class);
        final String givenValueDescription = codeGenerator.generateCode(codePointer.forElement(givenValueExpression), StandardCharsets.UTF_8);
        final String specificationDescription = codeGenerator.generateCode(codePointer.forElement(specificationExpression), StandardCharsets.UTF_8);

        // TODO Should NOT be static text!
        return BasicDescription
                .from("Given ")
                .appendText(givenValueDescription)
                .appendText(" then ")
                .appendText(specificationDescription);
    }
}
