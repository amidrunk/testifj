package org.testifj.lang;

@FunctionalInterface
public interface DecompilationStateSelector {

    DecompilationStateSelector ALL = (context, byteCode) -> true;

    boolean select(DecompilationContext context, int byteCode);

}
