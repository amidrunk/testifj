package org.testifj.integrationtest;

import org.testifj.Caller;
import org.testifj.Procedure;
import org.testifj.lang.codegeneration.impl.JavaSyntaxCodeGeneration;
import org.testifj.lang.decompile.CallerDecompiler;
import org.testifj.lang.classfile.ClassFileReader;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.impl.CallerDecompilerImpl;
import org.testifj.lang.classfile.impl.ClassFileReaderImpl;
import org.testifj.lang.codegeneration.impl.CodePointerCodeGenerator;
import org.testifj.lang.decompile.impl.DecompilerImpl;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ArrayThatIs.ofLength;

public abstract class TestOnDefaultConfiguration {

    private final Decompiler decompiler = new DecompilerImpl();

    private final ClassFileReader classFileReader = new ClassFileReaderImpl();

    private final CallerDecompiler callerDecompiler = new CallerDecompilerImpl(classFileReader, decompiler);

    private final CodePointerCodeGenerator codeGenerator = new CodePointerCodeGenerator(decompiler, JavaSyntaxCodeGeneration.configuration());

    protected String regenerate(Caller caller) {
        final CodePointer[] codePointers;

        try {
            codePointers = callerDecompiler.decompileCaller(caller);
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

                buffer.append(codeGenerator.describe(codePointer));
            }

            fail("One code pointer expected, was: \n" + buffer.toString());
        }

        expect(codePointers).toBe(ofLength(1));

        return codeGenerator.describe(codePointers[0]).toString();
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
