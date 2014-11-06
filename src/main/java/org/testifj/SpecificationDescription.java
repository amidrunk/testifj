package org.testifj;

import java.io.Serializable;

public final class SpecificationDescription implements Serializable {

    private final String targetName;

    private final Action<Specifier> action;

    public SpecificationDescription(String targetName, Action<Specifier> action) {
        assert targetName != null && !targetName.isEmpty() : "Name can't be null or empty";
        assert action != null : "Action can't be null";

        this.targetName = targetName;
        this.action = action;
    }

    public String getTargetName() {
        return targetName;
    }

    public Action<Specifier> getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecificationDescription that = (SpecificationDescription) o;

        if (!action.equals(that.action)) return false;
        if (!targetName.equals(that.targetName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetName.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SpecificationDescription{" +
                "targetName='" + targetName + '\'' +
                ", action=" + action +
                '}';
    }
}
