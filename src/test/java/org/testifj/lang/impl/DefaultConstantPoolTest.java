package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.ConstantPoolEntry;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

public class DefaultConstantPoolTest {

    @Test(expected = AssertionError.class)
    public void constantPoolBuilderShouldNotAcceptNullEntryWhenAdding() {
        new DefaultConstantPool.Builder()
                .addEntry(null);
    }

    @Test
    public void builderShouldCreateConstantPoolWithAddedEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new ConstantPoolEntry.UTF8Entry("foobar")
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
                .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                .create();

        final DefaultConstantPool pool2 = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                .create();

        assertEquals(pool1, pool2);
        assertEquals(pool1.hashCode(), pool2.hashCode());
    }

    @Test
    public void toStringValueShouldContainEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .create();

        assertThat(constantPool.toString(), containsString("foobar"));
    }

    @Test
    public void constantPoolShouldNotBeEqualToNullOrDifferentType() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .create();

        assertNotEquals(constantPool, null);
        assertNotEquals(constantPool, "foo");
    }

    @Test
    public void constantPoolShouldBeEqualToItSelf() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .create();

        assertEquals(constantPool, constantPool);
        assertEquals(constantPool.hashCode(), constantPool.hashCode());
    }

    @Test
    public void getClassNameShouldFailIfIndexIsInvalid() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
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
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .create();

        try {
            constantPool.getClassName(1);
            fail();
        } catch (ClassFormatError e) {
        }
    }

    @Test
    public void getClassNameShouldReturnNameOfClass() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .addEntry(new ConstantPoolEntry.ClassEntry(1))
                .create();

        assertEquals("foobar", constantPool.getClassName(2));
    }

    @Test
    public void getStringShouldNotAcceptInvalidIndex() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
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
                .addEntry(new ConstantPoolEntry.UTF8Entry("foobar"))
                .create();

        assertEquals("foobar", constantPool.getString(1));
    }

    @Test
    public void getStringShouldNotAcceptInvalidEntryType() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.ClassEntry(1))
                .create();

        try {
            constantPool.getString(1);
            fail();
        } catch (ClassFormatError e) {
        }
    }

    @Test
    public void getEntryShouldFailForNegativeOrZeroIndex() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                .create();

        expect(() -> constantPool.getEntry(-1)).toThrow(AssertionError.class);
        expect(() -> constantPool.getEntry(0)).toThrow(AssertionError.class);
    }

    @Test
    public void getEntryShouldFailIfIndexIsOutOfBounds() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                .create();

        expect(() -> constantPool.getEntry(2)).toThrow(IndexOutOfBoundsException.class);
    }

    @Test
    public void getEntryShouldReturnEntryAtIndex() {
        final ConstantPoolEntry.UTF8Entry entry = new ConstantPoolEntry.UTF8Entry("foo");
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
                .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                .create();

        expect(() -> constantPool.getEntries(new int[]{1, 2})).toThrow(IndexOutOfBoundsException.class);
    }

    @Test
    public void getEntriesShouldReturnMatchingEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                .addEntry(new ConstantPoolEntry.UTF8Entry("bar"))
                .create();

        expect(constantPool.getEntries(new int[]{1, 2})).toBe(new ConstantPoolEntry[]{
                new ConstantPoolEntry.UTF8Entry("foo"),
                new ConstantPoolEntry.UTF8Entry("bar")
        });
    }

}
