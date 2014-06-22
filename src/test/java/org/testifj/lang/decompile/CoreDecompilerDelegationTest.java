package org.testifj.lang.decompile;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class CoreDecompilerDelegationTest {

    @Test
    public void createShouldCreateCoreConfiguration() {
        final DecompilerConfiguration configuration = CoreDecompilerDelegation.configuration();

        expect(configuration.getDecompilerExtension(mock(DecompilationContext.class), ByteCode.iconst_0)).not().toBe(equalTo(null));
    }

}