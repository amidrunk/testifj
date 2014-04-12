package org.testifj;

import java.io.Serializable;

public final class SpecificationDescription implements Serializable {

    private final String targetName;

    private final Action<Specifier> action;

    public SpecificationDescription(String targetName, Action<Specifier> action) {
        this.targetName = targetName;
        this.action = action;
    }

    public String getTargetName() {
        return targetName;
    }

    public Action<Specifier> getAction() {
        return action;
    }
}
