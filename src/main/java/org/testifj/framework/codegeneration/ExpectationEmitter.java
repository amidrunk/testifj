package org.testifj.framework.codegeneration;

import io.recode.decompile.CodePointer;
import io.recode.model.MethodCall;
import org.testifj.framework.Expectation;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface ExpectationEmitter {

    Expectation getExpectation();

    List<CodePointer<MethodCall>> getMethodCalls();

    PrintWriter out();

    ExpectationEmitter emit(String string);

    String collect(Charset charset);

    default String collect() {
        return collect(StandardCharsets.UTF_8);
    }

    default ExpectationEmitter emit(String ... strings) {
        for (final String string : strings) {
            emit(string);
        }

        return this;
    }

}
