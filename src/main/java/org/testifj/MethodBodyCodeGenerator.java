package org.testifj;

import io.recode.classfile.Method;
import io.recode.codegeneration.CodeGenerator;
import io.recode.decompile.CodePointer;
import io.recode.decompile.CodeStream;
import io.recode.decompile.Decompiler;
import io.recode.codegeneration.impl.CodePointerCodeGenerator;
import io.recode.decompile.impl.CodePointerImpl;
import io.recode.decompile.impl.DecompilerImpl;
import io.recode.decompile.impl.InputStreamCodeStream;
import io.recode.model.Element;
import io.recode.model.ElementType;

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

        try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
            statements = decompiler.parse(method, code);
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

            methodElementCodeGenerator.generateCode(new CodePointerImpl<>(method, statement), out);
            out.append(";");
        }
    }

}
