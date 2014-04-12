package org.testifj.lang.impl;

import org.testifj.lang.Attribute;
import org.testifj.lang.CodeAttribute;
import org.testifj.lang.Method;

import java.util.Arrays;
import java.util.List;

public final class DefaultMethod implements Method {

    private final int accessFlags;

    private final String name;

    private final String signature;

    private final Attribute[] attributes;

    public DefaultMethod(int accessFlags, String name, String signature, Attribute[] attributes) {
        assert name != null : "name can't be null";
        assert signature != null : "signature can't be null";
        assert attributes != null : "attributes can't be null";

        this.accessFlags = accessFlags;
        this.name = name;
        this.signature = signature;
        this.attributes = attributes;
    }

    @Override
    public int getAccessFlags() {
        return accessFlags;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Arrays.asList(attributes);
    }

    @Override
    public CodeAttribute getCode() {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals("Code")) {
                return (CodeAttribute) attribute;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultMethod)) {
            return false;
        }

        final DefaultMethod other = (DefaultMethod) obj;

        return other.accessFlags == accessFlags
                && other.name.equals(name)
                && other.signature.equals(signature)
                && Arrays.equals(other.attributes, attributes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{
                accessFlags,
                name.hashCode(),
                signature.hashCode(),
                Arrays.hashCode(attributes)
        });
    }

    @Override
    public String toString() {
        return "DefaultMethod{" +
                "accessFlags=" + accessFlags + ", " +
                "name=\"" + name + "\", " +
                "signature=\"" + signature + "\", " +
                "attributes=" + Arrays.asList(attributes) +
            "}";
    }
}
