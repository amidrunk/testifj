package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.ClassFileFormatException;
import org.testifj.lang.ConstantPoolEntry;
import org.testifj.lang.FieldDescriptor;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.lang.ConstantPoolEntry.*;

public class DefaultConstantPoolTest {

    @Test(expected = AssertionError.class)
    public void constantPoolBuilderShouldNotAcceptNullEntryWhenAdding() {
        new DefaultConstantPool.Builder()
                .addEntry(null);
    }

    @Test
    public void builderShouldCreateConstantPoolWithAddedEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new UTF8Entry("foobar")
        }, constantPool.getEntries().toArray());
    }

    @Test
    public void longEntryShouldOccupyTwoEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.LongEntry(1234L))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new ConstantPoolEntry.LongEntry(1234L), null
        }, constantPool.getEntries().toArray());
    }

    @Test
    public void doubleEntryShouldOccupyTwoEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.DoubleEntry(1234d))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new ConstantPoolEntry.DoubleEntry(1234d), null
        }, constantPool.getEntries().toArray());
    }

    @Test
    public void constantPoolsWithEqualEntriesShouldBeEqual() {
        final DefaultConstantPool pool1 = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        final DefaultConstantPool pool2 = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        assertEquals(pool1, pool2);
        assertEquals(pool1.hashCode(), pool2.hashCode());
    }

    @Test
    public void toStringValueShouldContainEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertThat(constantPool.toString(), containsString("foobar"));
    }

    @Test
    public void constantPoolShouldNotBeEqualToNullOrDifferentType() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertNotEquals(constantPool, null);
        assertNotEquals(constantPool, "foo");
    }

    @Test
    public void constantPoolShouldBeEqualToItSelf() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertEquals(constantPool, constantPool);
        assertEquals(constantPool.hashCode(), constantPool.hashCode());
    }

    @Test
    public void getClassNameShouldFailIfIndexIsInvalid() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        try {
            constantPool.getClassName(-1);
            fail();
        } catch (AssertionError e) {
        }

        try {
            constantPool.getClassName(2);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void getClassNameShouldFailIfEntryTypesAreNotCorrect() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foobar"));

        expect(() -> constantPool.getClassName(1)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void getClassNameShouldReturnNameOfClass() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .addEntry(new ClassEntry(1))
                .create();

        assertEquals("foobar", constantPool.getClassName(2));
    }

    @Test
    public void getStringShouldNotAcceptInvalidIndex() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        try {
            constantPool.getString(-1);
            fail();
        } catch (AssertionError e) {
        }

        try {
            constantPool.getString(2);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void getStringShouldReturnUTF8Value() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertEquals("foobar", constantPool.getString(1));
    }

    @Test
    public void getStringShouldNotAcceptInvalidEntryType() {
        final DefaultConstantPool constantPool = createConstantPool(new ClassEntry(1));

        expect(() -> constantPool.getString(1)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void getEntryShouldFailForNegativeOrZeroIndex() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        expect(() -> constantPool.getEntry(-1)).toThrow(AssertionError.class);
        expect(() -> constantPool.getEntry(0)).toThrow(AssertionError.class);
    }

    @Test
    public void getEntryShouldFailIfIndexIsOutOfBounds() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        expect(() -> constantPool.getEntry(2)).toThrow(IndexOutOfBoundsException.class);
    }

    @Test
    public void getEntryShouldReturnEntryAtIndex() {
        final UTF8Entry entry = new UTF8Entry("foo");
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(entry)
                .create();

        expect(constantPool.getEntry(1)).toBe(entry);
    }

    @Test
    public void getEntriesShouldNotAcceptNullArg() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .create();

        expect(() -> constantPool.getEntries(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getEntriesShouldFailIfAnyIndexIsInvalid() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        expect(() -> constantPool.getEntries(new int[]{1, 2})).toThrow(IndexOutOfBoundsException.class);
    }

    @Test
    public void getEntriesShouldReturnMatchingEntries() {
        final ConstantPoolEntry[] expectedEntries = {new UTF8Entry("foo"), new UTF8Entry("bar")};
        final DefaultConstantPool constantPool = createConstantPool(expectedEntries);

        expect(constantPool.getEntries(new int[]{1, 2})).toBe(expectedEntries);
    }

    @Test
    public void getEntryWithTypeShouldNotAcceptNullType() {
        expect(() -> createConstantPool(new UTF8Entry("foo")).getEntry(1, null)).toThrow(AssertionError.class);
    }

    @Test
    public void getEntryWithTypeShouldFailForIncorrectType() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> constantPool.<NameAndTypeEntry>getEntry(1, NameAndTypeEntry.class))
                .toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getEntryShouldReturnMatchingEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));
        final UTF8Entry entry = constantPool.getEntry(1, UTF8Entry.class);

        expect(entry).toBe(new UTF8Entry("foo"));
    }

    @Test
    public void getFieldDescriptorShouldFailIfEntryIsNotAFieldRef() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> constantPool.getFieldDescriptor(1)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getFieldDescriptorShouldResolveEntriesAndReturnDescriptor() {
        final DefaultConstantPool constantPool = createConstantPool(
                new FieldRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("MyClass"),
                new UTF8Entry("myField"),
                new UTF8Entry("I")
        );

        final FieldDescriptor fieldDescriptor = constantPool.getFieldDescriptor(1);

        expect(fieldDescriptor.getClassName()).toBe("MyClass");
        expect(fieldDescriptor.getName()).toBe("myField");
        expect(fieldDescriptor.getDescriptor()).toBe("I");
    }

    @Test
    public void getLongShouldFailIfEntryIsNotALongEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> constantPool.getLong(1)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getLongShouldReturnValueOfLongEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new LongEntry(1234L));

        expect(constantPool.getLong(1)).toBe(1234L);
    }

    private DefaultConstantPool createConstantPool(ConstantPoolEntry ... entries) {
        final DefaultConstantPool.Builder builder = new DefaultConstantPool.Builder();

        Arrays.stream(entries).forEach(builder::addEntry);

        return builder.create();
    }

}
