package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Element;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;

public class CoreCodeGenerationExtensionsTest {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(baos);
    private final CodeGeneratorConfiguration configuration = coreConfiguration();

    private final CodeGenerationDelegate codeGenerationDelegate = (context, codePointer) -> {
        configuration.getExtension(context, codePointer).generateCode(context, codePointer, out);
    };

    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(codeGenerationDelegate);
    private final Method method = mock(Method.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> CoreCodeGenerationExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForReturnElement() {
        expect(codeFor(AST.$return())).toBe("return");
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForConstantElements() {
        expect(codeFor(AST.constant(1.234d))).toBe("1.234");
        expect(codeFor(AST.constant(1.234f))).toBe("1.234f");
        expect(codeFor(AST.constant(1234L))).toBe("1234L");
        expect(codeFor(AST.constant(1234))).toBe("1234");
        expect(codeFor(AST.constant("foo"))).toBe("\"foo\"");
    }

    @Test
    public void coreConfigurationShouldSupportGenerationOfReturnValue() {
        expect(codeFor(AST.$return(constant(1)))).toBe("return 1");
    }

    @Test
    public void coreConfigurationShouldSupportGenerationOfVariableReference() {
        expect(codeFor(AST.local("foo", String.class, 1))).toBe("foo");
    }

    @Test
    public void coreConfigurationShouldSupportInstanceMethodCall() {
        expect(codeFor(AST.call(constant("foo"), "toString", String.class))).toBe("\"foo\".toString()");
        expect(codeFor(AST.call(constant("foo"), "length", int.class))).toBe("\"foo\".length()");
        expect(codeFor(AST.call(constant("foo"), "substring", String.class, constant(1), constant(2)))).toBe("\"foo\".substring(1, 2)");
    }

    @Test
    public void codeConfigurationShouldSupportStaticMethodCall() {
        expect(codeFor(AST.call(String.class, "valueOf", String.class, constant(1)))).toBe("java.lang.String.valueOf(1)");
    }

    private String codeFor(Element element) {
        return codeFor(new CodePointerImpl(method, element));
    }

    private String codeFor(CodePointerImpl codePointer) {
        final CodeGeneratorExtension extension = coreConfiguration().getExtension(context, codePointer);

        baos.reset();
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
