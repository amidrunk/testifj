package org.testifj.lang;

import org.testifj.lang.classfile.*;

import java.util.Optional;

public final class Methods {

    public static Optional<Method> findMethodForLineNumber(ClassFile classFile, int lineNumber) {
        assert classFile != null : "Class file can't be null";
        assert lineNumber >= 0 : "Line number must be positive";

        for (Method method : classFile.getMethods()) {
            final Optional<LineNumberTable> optionalLineNumberTable = method.getLineNumberTable();

            if (optionalLineNumberTable.isPresent()) {
                final LineNumberTable lineNumberTable = optionalLineNumberTable.get();
                final Range sourceFileRange = lineNumberTable.getSourceFileRange();

                if (lineNumber >= sourceFileRange.getFrom() && lineNumber <= sourceFileRange.getTo()) {
                    return Optional.of(method);
                }
            }
        }

        return Optional.empty();
    }

    public static Optional<ExceptionTableEntry> getExceptionTableEntryForCatchLocation(Method method, int pc) {
        assert method != null : "Method can't be null";
        assert pc >= 0 : "PC must be positive";

        for (ExceptionTableEntry entry : method.getCode().getExceptionTable()) {
            if (entry.getEndPC() == pc) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    public static Optional<LocalVariable> findLocalVariableForIndexAndPC(Method method, int index, int pc) {
        assert method != null : "Method can't be null";
        assert index >= 0 : "Index must be positive";
        assert pc >= 0 : "PC must be positive";

        final Optional<LocalVariableTable> optionalLocalVariableTable = method.getLocalVariableTable();

        if (!optionalLocalVariableTable.isPresent()) {
            return Optional.empty();
        }

        return optionalLocalVariableTable.get().getLocalVariables().stream()
                .filter(local -> local.getIndex() == index && (pc >= local.getStartPC() && pc < local.getStartPC() + local.getLength()) || (local.getStartPC() == -1))
                .findFirst();
    }

}
