package org.testifj.lang.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.testifj.Action;
import org.testifj.Caller;
import org.testifj.Matcher;
import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.testifj.Caller.adjacent;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.ClassModelTestUtils.code;
import static org.testifj.matchers.core.CollectionThatIs.empty;

public class CallerDecompilerImplTest {

    private final CallerDecompiler callerDecompiler = new CallerDecompilerImpl();

    @Test
    public void decompileCallerShouldNotAcceptNullCaller() {
        expect(() -> callerDecompiler.decompileCaller(null)).toThrow(AssertionError.class);
    }

    @Test
    public void decompileCallerCanDecompileSimpleStatement() throws IOException {
        int n = 100;

        final Element[] elements = decompileCaller(adjacent(-2));

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(new ConstantImpl(100, int.class), 1, "n", int.class)
        });
    }

    @Test
    public void decompileCallerCanDecompileSimpleLambda() throws IOException {
        Supplier<String> s = () -> "foo";

        final Element[] elements = decompileCaller(adjacent(-2));
        final Lambda lambda = assignedLambda("s", elements);

        expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("()Ljava/lang/String;"));
        expect(lambda.getEnclosedVariables()).toBe(empty());
        expect(lambda.getReferenceKind()).toBe(ReferenceKind.INVOKE_STATIC);
    }

    @Test
    public void decompileCallerCanDecompileLambdaWithEnclosedVariable() throws IOException {
        final String str = new String("str");
        final Supplier<String> s = () -> str;

        final Element[] elements = decompileCaller(adjacent(-2));
        final Lambda lambda = assignedLambda("s", elements);

        expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("(Ljava/lang/String;)Ljava/lang/String;"));
        expect(lambda.getEnclosedVariables().size()).toBe(1);
        expect(lambda.getEnclosedVariables().get(0)).toBe(localVariableReference("str", String.class));
        expect(lambda.getReferenceKind()).toBe(ReferenceKind.INVOKE_STATIC);
    }

    @Test
    public void decompileCallerCanDecompileLambdaWithParametersAndEnclosedVariable() throws IOException {
        final List myList = Arrays.asList("foo");
        final Action<String> a = s -> myList.toString();

        final Element[] elements = decompileCaller(adjacent(-2));
        final Lambda lambda = assignedLambda("a", elements);

        expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("(Ljava/util/List;Ljava/lang/String;)V"));
        expect(lambda.getEnclosedVariables().size()).toBe(1);
        expect(lambda.getEnclosedVariables().get(0).getName()).toBe("myList");
    }

    @Test
    public void nestedLambdaWithEnclosedVariablesCanBeDecompiled() throws IOException {
        int expectedLength = 3;

        given("foo").then(foo -> {
            given("bar").then(bar -> {
                expect(bar.length()).toBe(expectedLength);
            });
        });

        final Caller caller = Caller.adjacent(-6);
        final Element[] elements = decompileCaller(caller);

        expect(elements[0].as(MethodCall.class).getMethodName()).toBe("then");
        expect(elements[0].as(MethodCall.class).getTargetInstance().as(MethodCall.class).getMethodName()).toBe("given");
    }

    @Test
    public void multiLineExpressionsCanBeHandled() throws IOException {
        String str = new String("foo")
                .toString();

        final Element[] elements = decompileCaller(Caller.adjacent(-3));

        expect(elements).toBe(new Element[]{
                AST.set(1, "str", String.class, AST.call(AST.newInstance(String.class, AST.constant("foo")), "toString", String.class))
        });
    }

    @Test
    public void intArrayElementReferenceCanBeDecompiled() throws Exception {
        final int[] array = {1, 2, 3, 4};
        final int n = array[0];

        final Element[] elements = decompileCaller(Caller.adjacent(-2));

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(AST.local("array", int[].class, 1), AST.constant(0), int.class),
                        2, "n", int.class
                )
        });
    }

    @Test
    @Ignore
    // TODO: If (1) iinc and (2) stacked expression is non-int variable reference (3) we're trying to escape
    public void expressionInLoopCanBeDecompiled() throws IOException {
        for (int i = 0; i < 1; i++) {
            expect(true).toBe(true);
        }

        final Element[] elements = decompileCaller(Caller.adjacent(-3));

        System.out.println(Arrays.asList(elements));
        expect(elements.length).toBe(1);

        given(elements[0].as(MethodCall.class)).then(it -> {
            expect(it.getMethodName()).toBe("toBe");
            expect(it.getParameters().toArray()).toBe(new Object[]{AST.constant(1)});
        });
    }

    @Test
    @Ignore("This must be fixed")
    public void multiLineStatementInForEachCanBeDecompiled() throws IOException {
        final Integer[] integers = {1, 2, 3, 4};

        for (Integer n : integers) {
            expect(n)
                    .toBe(n);
        }

        final Element[] elements = decompileCaller(Caller.adjacent(-5));

        expect(elements.length).toBe(1);

        System.out.println(Arrays.asList(elements));
    }

    public void nestedLambdaWithEnclosedVariablesCanBeDecompiled(String str) {

    }

    private static Matcher<LocalVariableReference> localVariableReference(String name, Type type) {
        return ref -> ref.getName().equals(name) && ref.getType().equals(type);
    }

    private Lambda assignedLambda(String variableName, Element[] elements) {
        expect(elements.length).toBe(1);
        expect(elements[0].getElementType()).toBe(ElementType.VARIABLE_ASSIGNMENT);

        final VariableAssignment variableAssignment = (VariableAssignment) elements[0];

        expect(variableAssignment.getVariableName()).toBe(variableName);
        expect(variableAssignment.getValue().getElementType()).toBe(ElementType.LAMBDA);

        return (Lambda) variableAssignment.getValue();
    }

    private Element[] decompileCaller(Caller caller) throws IOException {
        return Arrays.stream(callerDecompiler.decompileCaller(caller))
                .map(CodePointer::getElement)
                .toArray(Element[]::new);
    }

}
