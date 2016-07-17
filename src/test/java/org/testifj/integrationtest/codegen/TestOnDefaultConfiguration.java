package org.testifj.integrationtest.codegen;

import io.recode.Caller;
import org.testifj.Procedure;
import io.recode.codegeneration.impl.JavaSyntaxCodeGeneration;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.classfile.ClassFileReader;
import io.recode.decompile.CodePointer;
import io.recode.decompile.Decompiler;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;
import io.recode.classfile.impl.ClassFileReaderImpl;
import io.recode.codegeneration.impl.CodePointerCodeGenerator;
import io.recode.decompile.impl.DecompilerImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.fail;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ArrayThatIs.ofLength;

public abstract class TestOnDefaultConfiguration {

    private final Decompiler decompiler = new DecompilerImpl();

    private final ClassFileReader classFileReader = new ClassFileReaderImpl();

    private final CodeLocationDecompiler codeLocationDecompiler = new CodeLocationDecompilerImpl(classFileReader, decompiler);

    private final CodePointerCodeGenerator codeGenerator = new CodePointerCodeGenerator(decompiler, JavaSyntaxCodeGeneration.configuration());

    protected String regenerate(Caller caller) {
        final CodePointer[] codePointers;

        try {
            codePointers = codeLocationDecompiler.decompileCodeLocation(caller);
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompile caller", e);
        }

        if (codePointers.length > 1) {
            final StringBuilder buffer = new StringBuilder();

            boolean first = true;

            for (CodePointer codePointer : codePointers) {
                if (first) {
                    first = false;
                } else {
                    buffer.append("\n");
                }

                buffer.append(codeGenerator.generateCode(codePointer, StandardCharsets.UTF_8));
            }

            fail("One code pointer expected, was: \n" + buffer.toString());
        }

        expect(codePointers).toBe(ofLength(1));

        return codeGenerator.generateCode(codePointers[0], StandardCharsets.UTF_8);
    }

    protected String messageOfFailure(Procedure.WithoutException procedure) {
        try {
            procedure.call();
        } catch (AssertionError e) {
            return e.getMessage();
        }

        throw new AssertionError("Procedure did not fail");
    }

}
