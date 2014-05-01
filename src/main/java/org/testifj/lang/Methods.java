package org.testifj.lang;

import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;

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

}
