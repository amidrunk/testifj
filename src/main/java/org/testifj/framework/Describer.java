package org.testifj.framework;

import java.util.Optional;

public interface Describer {

    Optional<String> describe(Object value);

}
