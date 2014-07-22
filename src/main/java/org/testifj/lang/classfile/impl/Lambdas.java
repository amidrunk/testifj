package org.testifj.lang.classfile.impl;

import org.testifj.Predicate;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.classfile.LocalVariableTable;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.model.SyntaxTreeVisitor;
import org.testifj.lang.decompile.impl.CodePointerImpl;
import org.testifj.lang.decompile.impl.InputStreamCodeStream;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Lambda;
import org.testifj.lang.model.LocalVariableReference;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public final class Lambdas {

    public static Optional<CodePointer<Lambda>> getLambdaDeclarationForMethod(Decompiler decompiler, Method lambdaBackingMethod) throws IOException {
        assert decompiler != null : "Decompiler can't be null";
        assert lambdaBackingMethod != null : "Lambda backing method can't be null";

        if (!lambdaBackingMethod.isLambdaBackingMethod()) {
            throw new IllegalArgumentException("The method " + lambdaBackingMethod.getClassFile().getName()
                    + "." + lambdaBackingMethod.getName() + " is not a lambda backing method");
        }

        Stream<Method> methods = lambdaBackingMethod.getClassFile().getMethods().stream();

        if (lambdaBackingMethod.getName().startsWith("lambda$null$")) {
            // Lambda in lambda; can't use line number table nor filter on method name; scan all lambda methods
            methods = methods.filter(m -> m.getName().startsWith("lambda$") && !m.getName().equals(lambdaBackingMethod.getName()))
                    .map(m -> {
                        try {
                            return Lambdas.withEnclosedVariables(decompiler, m);
                        } catch (IOException e) {
                            return m;
                        }
                    });
        } else {
            if (lambdaBackingMethod.getLineNumberTable().isPresent()) {
                final Range backingMethodSourceFileRange = lambdaBackingMethod.getLineNumberTable().get().getSourceFileRange();

                methods = methods.filter(m -> {
                    if (!m.getLineNumberTable().isPresent()) {
                        return true;
                    }

                    final Range candidateSourceFileRange = m.getLineNumberTable().get().getSourceFileRange();

                    return candidateSourceFileRange.getFrom() <= backingMethodSourceFileRange.getFrom()
                            && candidateSourceFileRange.getTo() >= backingMethodSourceFileRange.getTo();
                });
            }

            methods = methods.filter(m -> lambdaBackingMethod.getName().startsWith("lambda$" + m.getName()));
        }

        for (Iterator<Method> iterator = methods.iterator(); iterator.hasNext(); ) {
            final Method candidate = iterator.next();
            final Element[] methodElements;

            try (CodeStream code = new InputStreamCodeStream(candidate.getCode().getCode())) {
                methodElements = decompiler.parse(candidate, code);
            }

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

        final int localVariableOffset;

        if (lambdaCodePointer.getElement().getSelf().isPresent()) {
            localVariableOffset = 1;
        } else {
            localVariableOffset = 0;
        }

        for (int i = 0; i < enclosedVariables.size(); i++) {
            final LocalVariableReference localVariableReference = enclosedVariables.get(i);

            localVariables.add(new LocalVariableImpl(-1, -1, localVariableReference.getName(), localVariableReference.getType(), localVariableOffset + i));
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
