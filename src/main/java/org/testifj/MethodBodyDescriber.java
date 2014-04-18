package org.testifj;

import org.testifj.lang.Decompiler;
import org.testifj.lang.Method;
import org.testifj.lang.impl.DecompilerImpl;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;

import java.io.IOException;

public final class MethodBodyDescriber implements Describer<Method> {

    private final Decompiler decompiler;

    private final Describer<CodePointer> methodElementDescriber;

    public MethodBodyDescriber() {
        this(new DecompilerImpl(), new CodeDescriber());
    }

    public MethodBodyDescriber(Decompiler decompiler, Describer<CodePointer> methodElementDescriber) {
        this.decompiler = decompiler;
        this.methodElementDescriber = methodElementDescriber;
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

            buffer.append(methodElementDescriber.describe(new CodePointer(method, statement))).append(";");
        }

        return BasicDescription.from(buffer.toString());
    }
}
