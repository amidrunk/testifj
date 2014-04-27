package org.testifj;

/**
 * A <code>ServiceContext</code> is provided during execution to provide access to configurable
 * services/components.
 */
public interface ServiceContext {

    <T> T get(Class<T> type);

}
