package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.impl.DecompilerConfigurationImpl;
import org.testifj.lang.decompile.impl.InvokeDynamicExtensions;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class InvokeDynamicExtensionsTest {

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> InvokeDynamicExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldExtendBuilderWithDynamicInvokeSupport() {
        final DecompilerConfiguration.Builder builder = new DecompilerConfigurationImpl.Builder();

        InvokeDynamicExtensions.configure(builder);

        expect(builder.build().getDecompilerExtension(mock(DecompilationContext.class), ByteCode.invokedynamic)).not().toBe(equalTo(null));
    }

}