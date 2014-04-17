package org.testifj.lang.impl;

import org.testifj.lang.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testifj.lang.ConstantPoolEntry.FieldRefEntry;
import static org.testifj.lang.ConstantPoolEntry.NameAndTypeEntry;

public final class DefaultConstantPool implements ConstantPool {

    private final ConstantPoolEntry[] entries;

    private DefaultConstantPool(ConstantPoolEntry[] entries) {
        this.entries = entries;
    }

    public List<ConstantPoolEntry> getEntries() {
        return Arrays.asList(entries);
    }

    @Override
    public String getClassName(int index) {
        final ConstantPoolEntry.ClassEntry classEntry = (ConstantPoolEntry.ClassEntry) getEntry(index, ConstantPoolEntryTag.CLASS);

        return getString(classEntry.getNameIndex());
    }

    @Override
    public String getString(int index) {
        return ((ConstantPoolEntry.UTF8Entry) getEntry(index, ConstantPoolEntryTag.UTF8)).getValue();
    }

    @Override
    public ConstantPoolEntry getEntry(int index) {
        assert index > 0 : "Index must be > 0";

        final int constantPoolIndex = index - 1;

        if (constantPoolIndex >= entries.length) {
            throw new IndexOutOfBoundsException("Index " + index + " is not a valid constant pool index; must be 1 >= index <= " + entries.length);
        }

        return entries[constantPoolIndex];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ConstantPoolEntry> T getEntry(int index, Class<T> type) {
        assert type != null : "Type can't be null";

        final ConstantPoolEntry entry = getEntry(index);

        if (!type.isInstance(entry)) {
            throw new IllegalArgumentException("Expected entry of type " + type.getName()
                    + " at constant pool index " + index + ", actually was " + entry);
        }

        return (T) entry;
    }

    @Override
    public ConstantPoolEntry[] getEntries(int[] indices) {
        assert indices != null : "Indices can't be null";

        final ConstantPoolEntry[] matchedEntries = new ConstantPoolEntry[indices.length];

        for (int i = 0; i < indices.length; i++) {
            matchedEntries[i] = getEntry(indices[i]);
        }

        return matchedEntries;
    }

    @Override
    public FieldDescriptor getFieldDescriptor(int index) {
        final FieldRefEntry fieldRefEntry = getEntry(index, FieldRefEntry.class);
        final NameAndTypeEntry nameAndType = getEntry(fieldRefEntry.getNameAndTypeIndex(), NameAndTypeEntry.class);
        final String className = getClassName(fieldRefEntry.getClassIndex());
        final String fieldDescriptor = getString(nameAndType.getDescriptorIndex());
        final String fieldName= getString(nameAndType.getNameIndex());

        return new FieldDescriptorImpl(className, fieldDescriptor, fieldName);
    }

    private ConstantPoolEntry getEntry(int index, ConstantPoolEntryTag expectedTag) {
        assert (index > 0 && index <= entries.length) : "Index must be in range [1, " + entries.length + "], was " + index;

        final ConstantPoolEntry entry = entries[index - 1];

        if (entry.getTag() != expectedTag) {
            throw new ClassFileFormatException("Invalid class pool entry at index " + index + "; expected " + expectedTag + ", was: " + entry);
        }

        return entry;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultConstantPool)) {
            return false;
        }

        final DefaultConstantPool other = (DefaultConstantPool) obj;

        return Arrays.equals(entries, other.entries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    @Override
    public String toString() {
        return "DefaultConstantPool{entries=" + getEntries() + "}";
    }

    public static final class Builder {

        private final ArrayList<ConstantPoolEntry> entries = new ArrayList<>();

        public Builder addEntry(ConstantPoolEntry entry) {
            assert entry != null : "entry can't be null";

            entries.add(entry);

            if (entry.getTag() == ConstantPoolEntryTag.LONG || entry.getTag() == ConstantPoolEntryTag.DOUBLE) {
                entries.add(null);
            }

            return this;
        }

        public DefaultConstantPool create() {
            return new DefaultConstantPool(entries.toArray(new ConstantPoolEntry[entries.size()]));
        }

    }

}
