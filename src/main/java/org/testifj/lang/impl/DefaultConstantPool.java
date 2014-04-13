package org.testifj.lang.impl;

import org.testifj.lang.ConstantPool;
import org.testifj.lang.ConstantPoolEntry;
import org.testifj.lang.ConstantPoolEntryTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    private ConstantPoolEntry getEntry(int index, ConstantPoolEntryTag expectedTag) {
        assert (index >= 0 && index <= entries.length) : "Index must be in range [1, " + entries.length + "], was " + index;

        final ConstantPoolEntry entry = entries[index - 1];

        if (entry.getTag() != expectedTag) {
            throw new ClassFormatError("Invalid class pool entry at index " + index + "; expected " + expectedTag + ", was: " + entry);
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
