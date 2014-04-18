package org.testifj.lang;

public interface ConstantPoolEntryDescriptor {

    ConstantPoolEntryTag getTag();

    default <T extends ConstantPoolEntryDescriptor> T as(Class<T> type) {
        return (T) this;
    }

}
