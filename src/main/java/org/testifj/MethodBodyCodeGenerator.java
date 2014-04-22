package org.testifj;

import org.testifj.lang.Decompiler;
import org.testifj.lang.Method;
import org.testifj.lang.impl.DecompilerImpl;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;

import java.io.IOException;
import java.io.PrintWriter;

public final class MethodBodyCodeGenerator implements CodeGenerator<Method> {

    private final Decompiler decompiler;

    private final CodeGenerator<CodePointer> methodElementCodeGenerator;

    public MethodBodyCodeGenerator() {
        this(new DecompilerImpl(), new CodePointerCodeGenerator());
    }

    public MethodBodyCodeGenerator(Decompiler decompiler, CodeGenerator<CodePointer> methodElementCodeGenerator) {
        this.decompiler = decompiler;
        this.methodElementCodeGenerator = methodElementCodeGenerator;
    }

    @Override
    public void generateCode(Method method, PrintWriter out) {
        final Element[] statements;

        try {
            statements = decompiler.parse(method, method.getCode().getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < statements.length; i++) {
            final Element statement = statements[i];

            if (i == statements.length - 1 && statement.getElementType() == ElementType.RETURN) {
                // Ignore final void-return
                continue;
            }

            if (i > 0) {
                out.append("\n");
            }

            methodElementCodeGenerator.generateCode(new CodePointerImpl(method, statement), out);
            out.append(";");
        }
    }

    @Override
    public Description describe(Method method) {
        assert method != null : "Method can't be null";

        final StringBuilder buffer = new StringBuilder();
        final Element[] statements;

        try {
            statements = decompiler.parse(method, method.getCode().getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < statements.length; i++) {
            final Element statement = statements[i];

            if (i == statements.length - 1 && statement.getElementType() == ElementType.RETURN) {
                // Ignore final void-return
                continue;
            }

            if (i > 0) {
                buffer.append("\n");
            }

            buffer.append(methodElementCodeGenerator.describe(new CodePointerImpl(method, statement))).append(";");
        }

        return BasicDescription.from(buffer.toString());
    }
}