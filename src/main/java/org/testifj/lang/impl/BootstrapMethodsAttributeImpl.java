package org.testifj.lang.impl;

import org.testifj.lang.BootstrapMethod;
import org.testifj.lang.BootstrapMethodsAttribute;

import java.util.Collections;
import java.util.List;

public final class BootstrapMethodsAttributeImpl implements BootstrapMethodsAttribute {

    private final List<BootstrapMethod> bootstrapMethods;

    public BootstrapMethodsAttributeImpl(List<BootstrapMethod> bootstrapMethods) {
        assert bootstrapMethods != null : "Bootstrap methods can't be null";
        this.bootstrapMethods = bootstrapMethods;
    }

    @Override
    public List<BootstrapMethod> getBootstrapMethods() {
        return Collections.unmodifiableList(bootstrapMethods);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BootstrapMethodsAttributeImpl that = (BootstrapMethodsAttributeImpl) o;

        if (!bootstrapMethods.equals(that.bootstrapMethods)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return bootstrapMethods.hashCode();
    }

    @Override
    public String toString() {
        return "BootstrapMethodsAttributeImpl{" +
                "bootstrapMethods=" + bootstrapMethods +
                '}';
    }
}
