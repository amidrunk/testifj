package org.testifj.lang.decompile;

import org.testifj.lang.model.*;
import org.testifj.util.Priority;

import java.util.Iterator;
import java.util.function.Function;

public interface DecompilerConfiguration {

    DecompilerDelegate getDecompilerDelegate(DecompilationContext context, int byteCode);

    Iterator<DecompilerDelegate> getAdvisoryDecompilerEnhancements(DecompilationContext context, int byteCode);

    Iterator<DecompilerDelegate> getCorrectionalDecompilerEnhancements(DecompilationContext context, int byteCode);

    ModelTransformation<Element, Element>[] getTransformations(ElementType elementType);

    DecompilerConfiguration merge(DecompilerConfiguration other);

    public interface Builder {

        ExtendContinuation<DecompilerDelegate> before(int byteCode);

        ExtendContinuation<DecompilerDelegate> before(int ... byteCodes);

        ExtendContinuation<DecompilerDelegate> after(int byteCode);

        ExtendContinuation<DecompilerDelegate> after(int ... byteCodes);

        ExtendContinuation<DecompilerDelegate> on(int byteCode);

        ExtendContinuation<DecompilerDelegate> on(int ... byteCodes);

        ExtendContinuation<DecompilerDelegate> on(int startByteCode, int endByteCode);

        OnElementTypeContinuation map(ElementType elementType);

        interface OnElementTypeContinuation {

            <R extends Element> ForQueryContinuationWithPriority<R> forQuery(ModelQuery<Element, R> query);
        }

        interface ForQueryContinuationWithoutPriority<R extends Element> {

            Builder to(ModelTransformation<R, ? extends Element> transformation);

        }

        interface ForQueryContinuationWithPriority<R extends Element> extends ForQueryContinuationWithoutPriority<R> {

            ForQueryContinuationWithoutPriority<R> withPriority(Priority priority);

        }

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
