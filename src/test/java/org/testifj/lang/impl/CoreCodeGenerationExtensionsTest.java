package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.CodeGenerationDelegate;
import org.testifj.lang.CodeGeneratorConfiguration;
import org.testifj.lang.CodeGeneratorExtension;
import org.testifj.lang.Method;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Element;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class CoreCodeGenerationExtensionsTest {

    private final CodeGenerationDelegate codeGenerationDelegate = mock(CodeGenerationDelegate.class);
    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(codeGenerationDelegate);
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(baos);
    private final Method method = mock(Method.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> CoreCodeGenerationExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForReturnElement() {
        expect(codeFor(AST.$return())).toBe("return");
    }

    private String codeFor(Element element) {
        return codeFor(new CodePointerImpl(method, element));
    }

    private String codeFor(CodePointerImpl codePointer) {
        final CodeGeneratorExtension extension = coreConfiguration().getExtension(context, codePointer);

        extension.generateCode(context, codePointer, out);
        out.flush();

        return baos.toString();
    }

    private CodeGeneratorConfiguration coreConfiguration() {
        final SimpleCodeGeneratorConfiguration.Builder builder = new SimpleCodeGeneratorConfiguration.Builder();
        CoreCodeGenerationExtensions.configure(builder);

        return builder.build();
    }

}
