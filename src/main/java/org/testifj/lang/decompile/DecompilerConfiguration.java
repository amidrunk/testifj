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

}
