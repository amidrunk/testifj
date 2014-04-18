package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.ByteCode;
import org.testifj.lang.DecompilerConfiguration;
import org.testifj.lang.DecompilerExtension;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;

public class InvokeDynamicExtensionsTest {

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> InvokeDynamicExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldExtendBuilderWithDynamicInvokeSupport() {
        final DecompilerConfiguration.Builder builder = mock(DecompilerConfiguration.Builder.class);

        InvokeDynamicExtensions.configure(builder);

        verify(builder).extend(eq(ByteCode.invokedynamic), any(DecompilerExtension.class));
    }

}
