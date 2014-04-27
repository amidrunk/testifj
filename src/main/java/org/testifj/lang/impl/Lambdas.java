package org.testifj.lang.impl;

import org.testifj.Caller;
import org.testifj.Predicate;
import org.testifj.lang.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.LocalVariableReference;
import sun.jvm.hotspot.oops.LocalVariableTableElement;

import java.io.IOException;
import java.util.*;

public final class Lambdas {

    public static Optional<CodePointer<Lambda>> getLambdaDeclarationForMethod(Decompiler decompiler, Method lambdaBackingMethod) throws IOException {
        assert decompiler != null : "Decompiler can't be null";
        assert lambdaBackingMethod != null : "Lambda backing method can't be null";

        if (!lambdaBackingMethod.isLambdaBackingMethod()) {
            throw new IllegalArgumentException("The method " + lambdaBackingMethod.getClassFile().getName()
                    + "." + lambdaBackingMethod.getName() + " is not a lambda backing method");
        }

        final Method[] candidates = lambdaBackingMethod.getClassFile().getMethods().stream()
                .filter(m -> lambdaBackingMethod.getName().startsWith("lambda$" + m.getName()))
                .toArray(Method[]::new);

        for (Method candidate : candidates) {
            final Element[] methodElements = decompiler.parse(candidate, candidate.getCode().getCode());
            final Optional<Element> result = SyntaxTreeVisitor.search(methodElements, isDeclarationOf(lambdaBackingMethod));

            if (result.isPresent()) {
                return Optional.of(new CodePointerImpl<>(candidate, result.get().as(Lambda.class)));
            }
        }

        return Optional.empty();
    }

    public static Method withEnclosedVariables(Decompiler decompiler, Method method) throws IOException {
        final Optional<CodePointer<Lambda>> lambda = getLambdaDeclarationForMethod(decompiler, method);

        if (!lambda.isPresent()) {
            throw new ClassFileFormatException("Lambda declaration of backing method " + method.getName()
                    + " not found in class file " + method.getClassFile().getName());
        }

        final CodePointer<Lambda> lambdaCodePointer = lambda.get();
        final List<LocalVariableReference> enclosedVariables = lambdaCodePointer.getElement().getEnclosedVariables();

        if (enclosedVariables.isEmpty()) {
            return method;
        }

        final Optional<LocalVariableTable> localVariableTable = method.getLocalVariableTable();
        final List<LocalVariable> localVariables;

        if (!localVariableTable.isPresent()) {
            localVariables = new ArrayList<>(enclosedVariables.size());
        } else {
            final List<LocalVariable> existingLocals = localVariableTable.get().getLocalVariables();

            localVariables = new ArrayList<>(enclosedVariables.size() + existingLocals.size());

            existingLocals.forEach(localVariables::add);
        }

        for (int i = 0; i < enclosedVariables.size(); i++) {
            final LocalVariableReference localVariableReference = enclosedVariables.get(i);

            localVariables.add(new LocalVariableImpl(-1, -1, localVariableReference.getName(), localVariableReference.getType(), i));
        }

        Collections.sort(localVariables, (v1, v2) -> v1.getIndex() - v2.getIndex());

        return method.withLocalVariableTable(new LocalVariableTableImpl(localVariables.toArray(new LocalVariable[localVariables.size()])));
    }

    public static Predicate<Element> isDeclarationOf(Method lambdaMethod) {
        assert lambdaMethod != null : "Lambda method can't be null";

        return e -> {
            if (e.getElementType() != ElementType.LAMBDA) {
                return false;
            }

            final Lambda lambda = e.as(Lambda.class);

            if (!lambda.getBackingMethodName().equals(lambdaMethod.getName())) {
                return false;
            }

            return true;
        };
    }

}
