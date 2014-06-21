package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.Caller;
import org.testifj.lang.decompile.CallerDecompiler;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.classfile.ReferenceKind;
import org.testifj.lang.decompile.impl.CallerDecompilerImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;
import org.testifj.lang.model.impl.MethodSignature;
import org.testifj.util.Arrays2;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.CollectionThatIs.empty;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class InvokeDynamicExtensionsDecompilationTest {

    private final CallerDecompiler callerDecompiler = new CallerDecompilerImpl();

    private final TestTarget testTarget = new TestTarget();

    @Test
    public void instanceMethodReferenceWithSameSignatureAsInterfaceCanBeDecompiled() throws IOException {
        final Runnable runnable = testTarget::run1;
        final Element element = Arrays2.single(callerDecompiler.decompileCaller(Caller.adjacent(-1)), CodePointer::getElement);

        given(element.as(VariableAssignment.class)).then(assignment -> {
            expect(assignment.getVariableType()).toBe(Runnable.class);
            expect(assignment.getVariableName()).toBe("runnable");

            given(assignment.getValue().as(Lambda.class)).then(lambda -> {
                expect(lambda.getFunctionalInterface()).toBe(Runnable.class);
                expect(lambda.getBackingMethodName()).toBe("run1");
                expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("()V"));
                expect(lambda.getDeclaringClass()).toBe(TestTarget.class);
                expect(lambda.getSelf()).toBe(optionalOf(AST.field(AST.local("this", getClass(), 0), TestTarget.class, "testTarget")));
                expect(lambda.getInterfaceMethodSignature()).toBe(MethodSignature.parse("()V"));
                expect(lambda.getEnclosedVariables()).toBe(empty());
                expect(lambda.getReferenceKind()).toBe(ReferenceKind.INVOKE_VIRTUAL);
            });
        });
    }

    @Test
    public void staticMethodReferenceWithSameSignatureAsInterfaceCanBeDecompiled() throws IOException {
        final Runnable runnable = TestTarget::run2;
        final Element element = Arrays2.single(callerDecompiler.decompileCaller(Caller.adjacent(-1)), CodePointer::getElement);

        given(element.as(VariableAssignment.class)).then(assignment -> {
            expect(assignment.getVariableType()).toBe(Runnable.class);
            expect(assignment.getVariableName()).toBe("runnable");

            given(assignment.getValue().as(Lambda.class)).then(lambda -> {
                expect(lambda.getDeclaringClass()).toBe(TestTarget.class);
                expect(lambda.getBackingMethodName()).toBe("run2");
                expect(lambda.getSelf()).not().toBe(present());
                expect(lambda.getFunctionalInterface()).toBe(Runnable.class);
                expect(lambda.getFunctionalMethodName()).toBe("run");
                expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("()V"));
                expect(lambda.getInterfaceMethodSignature()).toBe(MethodSignature.parse("()V"));
                expect(lambda.getEnclosedVariables()).toBe(empty());
                expect(lambda.getReferenceKind()).toBe(ReferenceKind.INVOKE_STATIC);
            });
        });
    }

    @Test
    public void staticMethodReferenceWithDifferentSignatureFromInterfaceCanBeDecompiled() throws IOException {
        final Function<Element, ElementType> function = Element::getElementType;

        final Element element = Arrays2.single(callerDecompiler.decompileCaller(Caller.adjacent(-2)), CodePointer::getElement);

        given(element.as(VariableAssignment.class)).then(assignment -> {
            expect(assignment.getVariableType()).toBe(Function.class);
            expect(assignment.getVariableName()).toBe("function");

            given(assignment.getValue().as(Lambda.class)).then(lambda -> {
                expect(lambda.getBackingMethodName()).toBe("getElementType");
                expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("()Lorg/testifj/lang/model/ElementType;"));
                expect(lambda.getDeclaringClass()).toBe(Element.class);
                expect(lambda.getSelf()).not().toBe(present());
                expect(lambda.getFunctionalInterface()).toBe(Function.class);
                expect(lambda.getFunctionalMethodName()).toBe("apply");
                expect(lambda.getInterfaceMethodSignature()).toBe(MethodSignature.parse("(Ljava/lang/Object;)Ljava/lang/Object;"));
                expect(lambda.getReferenceKind()).toBe(ReferenceKind.INVOKE_INTERFACE);
            });
        });
    }

    @Test
    public void lambdaWithVariableAndFieldReference() throws IOException {
        final String str1 = new String("_suffix");
        final Supplier<String> supplier = () -> testTarget.getClass().getName() + str1;
        final Element element = Arrays2.single(callerDecompiler.decompileCaller(Caller.adjacent(-1)), CodePointer::getElement);

        given(element.as(VariableAssignment.class)).then(assignment -> {
            expect(assignment.getVariableType()).toBe(Supplier.class);
            expect(assignment.getVariableName()).toBe("supplier");

            given(assignment.getValue().as(Lambda.class)).then(lambda -> {
                expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("(Ljava/lang/String;)Ljava/lang/String;"));
                expect(lambda.getReferenceKind()).toBe(ReferenceKind.INVOKE_SPECIAL);
                expect(lambda.getDeclaringClass()).toBe(getClass());
                expect(lambda.getSelf()).toBe(optionalOf(AST.local("this", getClass(), 0)));
                expect(lambda.getEnclosedVariables().toArray()).toBe(new Object[] { new LocalVariableReferenceImpl("str1", String.class, 1) });
            });
        });
    }

    public static final class TestTarget {

        public void run1() {
        }

        public static void run2() {
        }

    }

}
