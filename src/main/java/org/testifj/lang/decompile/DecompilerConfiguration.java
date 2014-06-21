package org.testifj.lang.decompile;

import org.testifj.util.Priority;

import java.util.Iterator;

public interface DecompilerConfiguration {

    DecompilerDelegate getDecompilerExtension(DecompilationContext context, int byteCode);

    Iterator<DecompilerDelegate> getAdvisoryDecompilerEnhancements(DecompilationContext context, int byteCode);

    Iterator<DecompilerDelegate> getCorrectionalDecompilerEnhancements(DecompilationContext context, int byteCode);

    DecompilerConfiguration merge(DecompilerConfiguration other);

    public interface Builder {

        ExtendContinuation<DecompilerDelegate> before(int byteCode);

        ExtendContinuation<DecompilerDelegate> after(int byteCode);

        ExtendContinuation<DecompilerDelegate> on(int byteCode);

        ExtendContinuation<DecompilerDelegate> on(int ... byteCodes);

        ExtendContinuation<DecompilerDelegate> on(int startByteCode, int endByteCode);

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
