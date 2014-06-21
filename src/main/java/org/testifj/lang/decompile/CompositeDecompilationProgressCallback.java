package org.testifj.lang.decompile;

public final class CompositeDecompilationProgressCallback implements DecompilationProgressCallback {

    private final DecompilationProgressCallback[] callbacks;

    public CompositeDecompilationProgressCallback(DecompilationProgressCallback[] callbacks) {
        assert callbacks != null : "Callbacks can't be null";

        this.callbacks = new DecompilationProgressCallback[callbacks.length];

        for (int i = 0; i < callbacks.length; i++) {
            assert callbacks[i] != null : "No target callback can't be null";
            this.callbacks[i] = callbacks[i];
        }
    }

    @Override
    public void onDecompilationProgressed(DecompilationContext context) {
        for (DecompilationProgressCallback callback : callbacks) {
            callback.onDecompilationProgressed(context);
        }
    }
}
