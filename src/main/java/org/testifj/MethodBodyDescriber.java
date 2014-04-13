package org.testifj;

import org.testifj.lang.ByteCodeParser;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ByteCodeParserImpl;
import org.testifj.lang.model.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public final class MethodBodyDescriber implements Describer<Method> {

    private final ByteCodeParser byteCodeParser;

    private final Describer<Element> methodElementDescriber;

    public MethodBodyDescriber() {
        this(new ByteCodeParserImpl(), new MethodElementDescriber());
    }

    public MethodBodyDescriber(ByteCodeParser byteCodeParser, Describer<Element> methodElementDescriber) {
        this.byteCodeParser = byteCodeParser;
        this.methodElementDescriber = methodElementDescriber;
    }

    @Override
    public String describe(Method method) {
        assert method != null : "Method can't be null";

        final StringBuilder buffer = new StringBuilder();
        final Element[] statements;

        try {
            statements = byteCodeParser.parse(method, method.getCode().getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < statements.length; i++) {
            final Element statement = statements[i];

            if (i == statements.length - 1 && statement.getElementType() == ElementType.RETURN) {
                // Ignore final void-return
                continue;
            }

            buffer.append(methodElementDescriber.describe(statement)).append(";");
        }

        return buffer.toString();
    }
}
