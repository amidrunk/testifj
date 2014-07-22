package org.testifj.lang.codegeneration;

import org.testifj.BasicDescription;
import org.testifj.Description;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@FunctionalInterface
public interface CodeGenerator<T> {

    void generateCode(T instance, PrintWriter out);

    default Description describe(T instance) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter out = new PrintWriter(baos);
        
        generateCode(instance, out);

        out.flush();

        if (out.checkError()) {
            throw new CodeGenerationException("Code generation failed due to underlying I/O exception");
        }

        final String string;

        try {
            string = new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CodeGenerationException("Code generation failed unexpectedly (UTF-8 encoding not found?)", e);
        }

        return BasicDescription.from(string);
    }

}
