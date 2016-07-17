package org.testifj.framework.codegeneration;

import org.testifj.framework.Expectation;

public interface ExpectationEmitterFactory {

    ExpectationEmitter createContext(Expectation expectation);


}
