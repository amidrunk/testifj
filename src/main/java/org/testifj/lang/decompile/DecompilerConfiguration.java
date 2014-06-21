package org.testifj.lang.decompile;

import org.testifj.util.Priority;

import java.util.Iterator;

public interface DecompilerConfiguration {

    DecompilerExtension getDecompilerExtension(DecompilationContext context, int byteCode);

    Iterator<DecompilerEnhancement> getAdvisoryDecompilerEnhancements(DecompilationContext context, int byteCode);

    Iterator<DecompilerEnhancement> getCorrectionalDecompilerEnhancements(DecompilationContext context, int byteCode);

    DecompilerConfiguration merge(DecompilerConfiguration other);

    public interface Builder {

        ExtendContinuation<DecompilerEnhancement> before(int byteCode);

        ExtendContinuation<DecompilerEnhancement> after(int byteCode);

        ExtendContinuation<DecompilerExtension> on(int byteCode);

        ExtendContinuation<DecompilerExtension> on(int ... byteCodes);

        ExtendContinuation<DecompilerExtension> on(int startByteCode, int endByteCode);

        interface ExtendContinuation<T> extends WithPriorityContinuation<T> {

            WithPriorityContinuation<T> withPriority(Priority priority);
        }

        interface WithPriorityContinuation<T> extends WhenContinuation<T> {

            WhenContinuation<T> when(DecompilationStateSelector selector);

        }

        interface WhenContinuation<T> {

            Builder then(T t);

        }

        DecompilerConfiguration build();

    }

}
