package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.Caller;
import org.testifj.Predicate;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.LocalVariableTable;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.ReferenceKind;
import org.testifj.lang.classfile.impl.LambdaImpl;
import org.testifj.lang.classfile.impl.Lambdas;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.SyntaxTreeVisitor;
import org.testifj.lang.decompile.impl.DecompilerImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.MethodSignature;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Caller.adjacent;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.ClassModelTestUtils.code;
import static org.testifj.matchers.core.CollectionThatIs.empty;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.ObjectThatIs.sameAs;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class LambdasTest {

    private final Decompiler decompiler = new DecompilerImpl();

    @Test
    public void getLambdaDeclarationForMethodShouldNotAcceptNullBackingMethod() {
        expect(() -> Lambdas.getLambdaDeclarationForMethod(null, mock(Method.class))).toThrow(AssertionError.class);
        expect(() -> Lambdas.getLambdaDeclarationForMethod(decompiler, null)).toThrow(AssertionError.class);
    }

    @Test
    public void getLambdaDeclarationForMethodShouldNotAcceptNonLambdaBackingMethod() {
        final Method method = mock(Method.class);
        final ClassFile classFile = mock(ClassFile.class);

        when(method.getClassFile()).thenReturn(classFile);
        when(method.isLambdaBackingMethod()).thenReturn(false);

        expect(() -> Lambdas.getLambdaDeclarationForMethod(decompiler, method)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getLambdaDeclarationForMethodShouldResolveDeclaringLambdaMethod() throws IOException {
        final Runnable runnable = () -> {};

        final CodePointer codePointer = code(adjacent(-2))[0];
        final Lambda expectedLambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = getBackingMethod(codePointer, expectedLambda);

        final CodePointer<Lambda> actualLambdaPointer = Lambdas.getLambdaDeclarationForMethod(decompiler, backingMethod)
                .orElseThrow(() -> new AssertionError("Lambda not found"));

        expect(actualLambdaPointer.getElement()).toBe(equalTo(expectedLambda));
        expect(actualLambdaPointer.getMethod().getName()).toBe("getLambdaDeclarationForMethodShouldResolveDeclaringLambdaMethod");
    }

    @Test
    public void getLambdaDeclarationForMethodShouldResolveLambdaInLambda() throws IOException {
        final Supplier<Runnable> supplier = () -> {
            return () -> {
                System.out.println("Example statement");
            };
        };

        final Caller caller = Caller.adjacent(-4);
        final Method backingMethod = ClassModelTestUtils.classFileOf(getClass()).getMethods().stream()
                .filter(m -> m.hasCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber()))
                .findFirst().get();

        final CodePointer<Lambda> declarationCodePointer = Lambdas.getLambdaDeclarationForMethod(decompiler, backingMethod).get();
        final Lambda lambda = declarationCodePointer.getElement().as(Lambda.class);

        expect(lambda.getFunctionalInterface()).toBe(Runnable.class);
        expect(lambda.getFunctionalMethodName()).toBe("run");
        expect(lambda.getDeclaringClass()).toBe(getClass());
        expect(lambda.getSelf()).not().toBe(present());
        expect(lambda.getEnclosedVariables()).toBe(empty());
    }

    @Test
    public void isDeclarationOfShouldNotAcceptNullArg() {
        expect(() -> Lambdas.isDeclarationOf(null)).toThrow(AssertionError.class);
    }

    @Test
    public void isDeclarationOfPredicateShouldMatchCorrespondingLambda() {
        final Method method = mock(Method.class);
        final String lambdaMethodName = "lambda$myMethod";
        final Lambda lambda = new LambdaImpl(Optional.<Expression>empty(), ReferenceKind.INVOKE_STATIC, Runnable.class, "run", MethodSignature.parse("()V"), getClass(), lambdaMethodName, MethodSignature.parse("()V"), Collections.emptyList());

        when(method.getName()).thenReturn(lambdaMethodName);

        final Predicate<Element> predicate = Lambdas.isDeclarationOf(method);

        expect(predicate.test(lambda)).toBe(true);
    }

    @Test
    public void isDeclarationOfPredicateShouldNotMatchDifferentLambda() {
        final Method method = mock(Method.class);
        final String lambdaMethodName = "lambda$myMethod";
        final Lambda lambda = new LambdaImpl(Optional.<Expression>empty(), ReferenceKind.INVOKE_STATIC, Runnable.class, "run", MethodSignature.parse("()V"), getClass(), "lambda$otherLambda", MethodSignature.parse("()V"), Collections.emptyList());

        when(method.getName()).thenReturn(lambdaMethodName);

        final Predicate<Element> predicate = Lambdas.isDeclarationOf(method);

        expect(predicate.test(lambda)).toBe(false);
    }

    @Test
    public void isDeclarationOfPredicateShouldNotMatchNonLambdaElementType() {
        given(Lambdas.isDeclarationOf(mock(Method.class))).then(it -> {
            expect(it.test(AST.constant(1))).toBe(false);
        });
    }

    @Test
    public void withEnclosedVariablesShouldNotAcceptInvalidParameters() {
        expect(() -> Lambdas.withEnclosedVariables(null, mock(Method.class))).toThrow(AssertionError.class);
        expect(() -> Lambdas.withEnclosedVariables(mock(Decompiler.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void withEnclosedVariablesShouldReturnSameLambdaIfNoVariablesAreEnclosed() throws IOException {
        final Runnable runnable = () -> {};

        final CodePointer codePointer = code(adjacent(-2))[0];
        final Method backingMethod = getBackingMethodInClosestLambda(codePointer);
        final Method complementedMethod = Lambdas.withEnclosedVariables(decompiler, backingMethod);

        expect(complementedMethod).toBe(sameAs(backingMethod));
    }

    @Test
    public void withEnclosedVariablesAndNoParametersShouldReturnMethodWithClosuresInVariableTable() throws IOException {
        final String variable = new String("foo");
        final Runnable runnable = () -> { System.out.println(variable); };

        final CodePointer codePointer = ClassModelTestUtils.code(Caller.adjacent(-2))[0];
        final Method complementedMethod = complementWithEnclosedVariables(codePointer);
        final Optional<LocalVariableTable> localVariableTable = complementedMethod.getLocalVariableTable();

        given(localVariableTable.get().getLocalVariables()).then(vars -> {
            expect(vars.size()).toBe(1);
            expect(vars.get(0).getName()).toBe("variable");
            expect(vars.get(0).getIndex()).toBe(0);
            expect(vars.get(0).getType()).toBe(String.class);
        });
    }

    @Test
    public void withEnclosedVariablesShouldReturnMethodWithClosuresAndParameters() throws IOException {
        final String other = new String("foo");
        final Function<String, Integer> f = string -> string.length() + other.length();

        final CodePointer codePointer = ClassModelTestUtils.code(Caller.adjacent(-2))[0];
        final Method complementedMethod = complementWithEnclosedVariables(codePointer);
        final Optional<LocalVariableTable> localVariableTable = complementedMethod.getLocalVariableTable();

        given(localVariableTable.get().getLocalVariables()).then(vars -> {
            expect(vars.size()).toBe(2);
            expect(vars.get(0).getName()).toBe("other");
            expect(vars.get(0).getIndex()).toBe(0);
            expect(vars.get(0).getType()).toBe(String.class);
            expect(vars.get(1).getName()).toBe("string");
            expect(vars.get(1).getIndex()).toBe(1);
            expect(vars.get(1).getType()).toBe(String.class);
        });
    }

    private Method complementWithEnclosedVariables(CodePointer codePointer) throws IOException {
        final Method backingMethod = getBackingMethodInClosestLambda(codePointer);
        return Lambdas.withEnclosedVariables(decompiler, backingMethod);
    }

    private Method getBackingMethodInClosestLambda(CodePointer codePointer) {
        final Lambda lambda = SyntaxTreeVisitor.search(codePointer.getElement(), e -> e.getElementType() == ElementType.LAMBDA).get().as(Lambda.class);
        return getBackingMethod(codePointer, lambda);
    }

    private Method getBackingMethod(CodePointer codePointer, Lambda expectedLambda) {
        return codePointer.getMethod().getClassFile().getMethods().stream()
                .filter(m -> m.getName().equals(expectedLambda.getBackingMethodName()))
                .findFirst()
                .get();
    }

}
