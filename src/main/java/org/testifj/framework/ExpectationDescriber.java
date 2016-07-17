package org.testifj.framework;

import io.recode.codegeneration.impl.CodePointerCodeGenerator;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.CodePointer;
import io.recode.model.*;
import io.recode.util.Methods;
import org.testifj.framework.codegeneration.CodeGenerationUtil;
import org.testifj.framework.codegeneration.DefaultExpectationEmitterFactory;
import org.testifj.framework.codegeneration.ExpectationEmitter;
import org.testifj.framework.codegeneration.ExpectationEmitterFactory;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Really should create a model that a view is applied on instead... possible?
public class ExpectationDescriber implements Describer {

    private final CodeLocationDecompiler decompiler;

    private final CodePointerCodeGenerator codeGenerator;

    private final ValueDescriber valueDescriber;

    private final ExpectationEmitterFactory expectationEmitterFactory;

    public ExpectationDescriber(CodeLocationDecompiler decompiler,
                                CodePointerCodeGenerator codeGenerator,
                                ValueDescriber valueDescriber) {
        assert decompiler != null : "decompiler can't be null";
        assert codeGenerator != null : "codeGenerator can't be null";
        assert valueDescriber != null : "valueDescriber can't be null";

        this.decompiler = decompiler;
        this.codeGenerator = codeGenerator;
        this.valueDescriber = valueDescriber;
        this.expectationEmitterFactory = new DefaultExpectationEmitterFactory(decompiler);
    }

    @Override
    public Optional<String> describe(Object value) {
        if (!(value instanceof Expectation) || !(((Expectation) value).getExpectationReference() instanceof InlineExpectationReference)) {
            return Optional.empty();
        }

        final ExpectationEmitter emitter = expectationEmitterFactory.createContext((Expectation) value);

        if (value instanceof ValueExpectation) {
            emitValueExpectation((ValueExpectation) value, emitter);
        } else if (value instanceof BehaviouralExpectation) {
            emitBehavioralExpectation((BehaviouralExpectation) value, emitter);
        } else {
            return Optional.empty();
        }

        return Optional.of(emitter.collect());
    }

    protected void emitBehavioralExpectation(BehaviouralExpectation expectation, ExpectationEmitter emitter) {
        final Iterator<CodePointer<MethodCall>> iterator = emitter.getMethodCalls().iterator();
        final CodePointer<MethodCall> expect = iterator.next();
        final Lambda procedure = expect.getElement().getParameters().get(0).as(Lambda.class);
        final String procedureDescription = codeGenerator.generateCode(expect.forElement(procedure), StandardCharsets.UTF_8);

        emitter.emit("expected [", CodeGenerationUtil.stripLambdaPrefix(procedureDescription), "]");

        while (iterator.hasNext()) {
            final CodePointer<MethodCall> conditionMethodCall = iterator.next();

            emitter.emit(" ", Descriptions.methodNameToNaturalLanguage(conditionMethodCall.getElement().getMethodName()));

            for (final Iterator<Expression> i = conditionMethodCall.getElement().getParameters().iterator(); i.hasNext(); ) {
                final Expression parameter = i.next();

                emitter.emit(" ");

                if (parameter.getElementType() == ElementType.CONSTANT && parameter.getType().equals(Class.class)) {
                    emitter.emit(((Class) parameter.as(Constant.class).getConstant()).getSimpleName());
                } else {
                    emitter.emit(codeGenerator.generateCode(conditionMethodCall.forElement(parameter), StandardCharsets.UTF_8));
                }
            }
        }

        if (expectation.getOutcome().isExceptional()) {
            final Throwable throwable = (Throwable) expectation.getOutcome().getResult().get();

            emitter.emit(", actually threw ", throwable.getClass().getSimpleName(), "(");

            if (throwable.getMessage() != null && !throwable.getMessage().isEmpty()) {
                emitter.emit("\"", throwable.getMessage(), "\"");
            }

            emitter.emit(")");
        }
    }

    protected void emitValueExpectation(ValueExpectation expectation, ExpectationEmitter emitter) {
        final Iterator<CodePointer<MethodCall>> iterator = emitter.getMethodCalls().iterator();

        // expectation start
        final CodePointer<MethodCall> expect = iterator.next();

        final String actualValueExpression = codeGenerator.generateCode(expect.forElement(expect.getElement().getParameters().get(0)), StandardCharsets.UTF_8);
        final String actualValueDescription = valueDescriber.describe(expectation.getSubject()).get();

        if (Objects.equals(actualValueExpression, actualValueDescription)) {
            emitter.emit("expected ", actualValueDescription);
        } else if (expectation.getSubject() != null
                && Objects.equals(
                actualValueDescription,
                expectation.getSubject().getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(expectation.getSubject())))) {
            emitter.emit("expected [", actualValueExpression, "]");
        } else {
            emitter.emit("expected [", actualValueExpression, "]", " => ", actualValueDescription);
        }

        // conditions
        while (iterator.hasNext()) {
            final CodePointer<MethodCall> conditionMethodCall = iterator.next();

            emitter.emit(" ", Descriptions.methodNameToNaturalLanguage(conditionMethodCall.getElement().getMethodName()));

            for (final Iterator<Expression> parameterIterator = conditionMethodCall.getElement().getParameters().iterator(); parameterIterator.hasNext(); ) {
                final Expression parameter = parameterIterator.next();

                if (parameter.getElementType() == ElementType.NEW_ARRAY && !parameterIterator.hasNext() && Methods.isDefinitelyVarArgsMethodCall(conditionMethodCall.getElement())) {
                    final NewArray newArray = parameter.as(NewArray.class);

                    int index = 0;

                    for (final Iterator<ArrayInitializer> varArgsIterator = newArray.getInitializers().iterator();
                         varArgsIterator.hasNext(); ) {
                        final ArrayInitializer initializer = varArgsIterator.next();
                        final Expression varArgsValueExpression = initializer.getValue();
                        final int currentIndex = index++;
                        final Optional<Object> varArgsValue = expectation.getExpectedValue().map(array -> Array.get(array, currentIndex));

                        emitter.emit(" ");
                        emitConditionParameter(
                                conditionMethodCall.forElement(varArgsValueExpression),
                                varArgsValue,
                                emitter);

                        if (varArgsIterator.hasNext()) {
                            emitter.emit(",");
                        }
                    }
                } else {
                    emitter.emit(" ");
                    emitConditionParameter(
                            conditionMethodCall.forElement(parameter),
                            expectation.getExpectedValue(),
                            emitter);
                }
            }
        }
    }

    /**
     * Emits a condition parameter to the print stream. The condition can be e.g. <code>IntegerThatIs.lessThan(10)</code>
     * in which case the generated value should be <code>less than 10</code>.
     *
     * @param codePointer The code pointer referencing the parameter.
     * @param value       Optionally, the value of the parameter.
     * @param emitter     The emitter to which the code should be emitted.
     */
    protected void emitConditionParameter(CodePointer<Expression> codePointer,
                                          Optional<Object> value,
                                          ExpectationEmitter emitter) {
        final Optional<String> actualValueDescription = value.flatMap(valueDescriber::describe);

        boolean skipEmissionOfExpectedValue = false;

        if (codePointer.getElement().getElementType() == ElementType.METHOD_CALL) {
            final MethodCall methodCall = codePointer.getElement().as(MethodCall.class);
            final Expression targetInstance = methodCall.getTargetInstance();

            if (targetInstance != null) {
                emitConditionParameter(codePointer.forElement(targetInstance), Optional.empty(), emitter);
                emitter.emit(" ");
            }

            if (Descriptions.isDSLMethodCall(methodCall)) {
                emitMethodCallAsNaturalLanguage(codePointer.forElement(methodCall), emitter);
            } else {
                emitter.emit(codeGenerator.generateCode(codePointer, StandardCharsets.UTF_8));
            }
        } else {
            final String parameterDescription = codeGenerator.generateCode(codePointer, StandardCharsets.UTF_8);

            skipEmissionOfExpectedValue = (actualValueDescription.isPresent() && Objects.equals(actualValueDescription.get(), parameterDescription));

            if (skipEmissionOfExpectedValue) {
                emitter.emit(parameterDescription);
            } else {
                emitter.emit("[").emit(parameterDescription).emit("]");
            }
        }

        if (actualValueDescription.isPresent() && !skipEmissionOfExpectedValue) {
            emitter.emit(" => ", actualValueDescription.get());
        }
    }

    /**
     * Emits a method call as natural language. A method call such as <code>toBe(equalTo("foo"))</code>
     * would result in <code>to be equal to "foo"</code>.
     *
     * @param codePointer The code pointer referencing the method call.
     * @param emitter     The emitter to which the code should be emitted.
     */
    protected void emitMethodCallAsNaturalLanguage(CodePointer<MethodCall> codePointer, ExpectationEmitter emitter) {
        final MethodCall methodCall = codePointer.getElement().as(MethodCall.class);

        emitter.emit(Descriptions.methodNameToNaturalLanguage(codePointer.getElement().getMethodName()));

        for (final Iterator<Expression> i = methodCall.getParameters().iterator(); i.hasNext(); ) {
            emitter.emit(" ");
            emitConditionParameter(codePointer.forElement(i.next()), Optional.empty(), emitter);

            if (i.hasNext()) {
                emitter.emit(" and ");
            }
        }
    }
}
