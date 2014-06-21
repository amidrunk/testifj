package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.*;
import org.testifj.lang.classfile.impl.DefaultConstantPool;
import org.testifj.lang.decompile.ConstantPoolEntry;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.decompile.ConstantPoolEntry.*;

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

        expect(() -> constantPool.getEntry(1, NameAndTypeEntry.class))
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

        expect(() -> constantPool.getFieldRefDescriptor(1)).toThrow(IllegalArgumentException.class);
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

        final FieldRefDescriptor fieldRefDescriptor = constantPool.getFieldRefDescriptor(1);

        expect(fieldRefDescriptor.getClassName()).toBe("MyClass");
        expect(fieldRefDescriptor.getName()).toBe("myField");
        expect(fieldRefDescriptor.getDescriptor()).toBe("I");
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

    @Test
    public void getNameAndTypeDescriptorShouldFailIfIndexIsOtherEntry() {
        final DefaultConstantPool pool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> pool.getNameAndTypeDescriptor(1)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getNameAndTypeDescriptorShouldReturnNameAndTypeValues() {
        final DefaultConstantPool pool = createConstantPool(
                new NameAndTypeEntry(2, 3),
                new UTF8Entry("foo"), new UTF8Entry("()V"));

        given(pool.getNameAndTypeDescriptor(1)).then(d -> {
            expect(d.getName()).toBe("foo");
            expect(d.getDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getInterfaceMethodRefDescriptorShouldFailIfEntryIsOfOtherType() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> constantPool.getInterfaceMethodRefDescriptor(1)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getInterfaceMethodRefShouldReturnDescriptorForValidEntry() {
        final DefaultConstantPool constantPool = createConstantPool(
                new InterfaceMethodRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("ExampleClass"),
                new UTF8Entry("exampleMethod"),
                new UTF8Entry("()V")
        );

        given(constantPool.getInterfaceMethodRefDescriptor(1)).then(descriptor -> {
            expect(descriptor.getClassName()).toBe("ExampleClass");
            expect(descriptor.getMethodName()).toBe("exampleMethod");
            expect(descriptor.getDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getInvokeDynamicDescriptorShouldReturnValidEntry() {
        final DefaultConstantPool constantPool = createConstantPool(
                new InvokeDynamicEntry(1234, 2),
                new NameAndTypeEntry(3, 4),
                new UTF8Entry("call"),
                new UTF8Entry("()V")
        );

        given(constantPool.getInvokeDynamicDescriptor(1)).then(descriptor -> {
            expect(descriptor.getBootstrapMethodAttributeIndex()).toBe(1234);
            expect(descriptor.getMethodName()).toBe("call");
            expect(descriptor.getMethodDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getInvokeDynamicEntryShouldFailIfEntryIsOtherType() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> constantPool.getInvokeDynamicDescriptor(1)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getMethodHandleDescriptorShouldFailIfEntryIsOfIncorrectType() {
        given(createConstantPool(new UTF8Entry("foo"))).then(pool -> {
            expect(() -> pool.getMethodHandleDescriptor(1)).toThrow(IllegalArgumentException.class);
        });
    }

    @Test
    public void getMethodHandleDescriptorShouldReturnDescriptor() {
        final DefaultConstantPool constantPool = createConstantPool(
                new MethodHandleEntry(ReferenceKind.GET_FIELD, 2),
                new MethodRefEntry(3, 4),
                new ClassEntry(5),
                new NameAndTypeEntry(6, 7),
                new UTF8Entry("Foo"),
                new UTF8Entry("bar"),
                new UTF8Entry("()V")
        );

        given(constantPool.getMethodHandleDescriptor(1)).then(descriptor -> {
            expect(descriptor.getReferenceKind()).toBe(ReferenceKind.GET_FIELD);
            expect(descriptor.getClassName()).toBe("Foo");
            expect(descriptor.getMethodName()).toBe("bar");
            expect(descriptor.getMethodDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getMethodTypeDescriptorShouldFailIfEntryIsIncorrect() {
        given(createConstantPool(new UTF8Entry("foo"))).then(pool -> {
            expect(() -> pool.getMethodTypeDescriptor(1)).toThrow(IllegalArgumentException.class);
        });
    }

    @Test
    public void getMethodTypeDescriptorShouldCreateDescriptorFromEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new MethodTypeEntry(2), new UTF8Entry("()V"));

        given(constantPool.getMethodTypeDescriptor(1)).then(descriptor -> {
            expect(descriptor.getDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getDescriptorsShouldNotAcceptNullIndices() {
        given(createConstantPool()).then(pool -> {
            expect(() -> pool.getDescriptors(null)).toThrow(AssertionError.class);
        });
    }

    @Test
    public void getDescriptorsCanCreateSupportedDescriptors() {
        final DefaultConstantPool pool = createConstantPool(
                new MethodHandleEntry(ReferenceKind.INVOKE_STATIC, 2),
                new MethodRefEntry(3, 4),
                new ClassEntry(5),
                new NameAndTypeEntry(6, 7),
                new UTF8Entry("ExampleClass"),
                new UTF8Entry("exampleMethod"),
                new UTF8Entry("()V"),
                new MethodTypeEntry(7)
        );

        given(pool.getDescriptors(new int[]{1})).then(descriptors -> {
            expect(descriptors[0].getTag()).toBe(ConstantPoolEntryTag.METHOD_HANDLE);

            final MethodHandleDescriptor methodHandle = (MethodHandleDescriptor) descriptors[0];

            expect(methodHandle.getClassName()).toBe("ExampleClass");
            expect(methodHandle.getReferenceKind()).toBe(ReferenceKind.INVOKE_STATIC);
            expect(methodHandle.getMethodName()).toBe("exampleMethod");
            expect(methodHandle.getMethodDescriptor()).toBe("()V");
        });

        given(pool.getDescriptors(new int[]{8})).then(descriptors -> {
            expect(descriptors[0].getTag()).toBe(ConstantPoolEntryTag.METHOD_TYPE);

            final MethodTypeDescriptor methodType = (MethodTypeDescriptor) descriptors[0];

            expect(methodType.getDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getMethodRefDescriptorShouldFailIfEntryIsOfIncorrectType() {
        given(createConstantPool(new UTF8Entry("foo"))).then(it -> {
            expect(() -> it.getMethodRefDescriptor(1)).toThrow(IllegalArgumentException.class);
        });
    }

    @Test
    public void getMethodRefDescriptorShouldReturnDescriptorForMethodRefEntry() {
        final DefaultConstantPool constantPool = createConstantPool(
                new MethodRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("Foo"),
                new UTF8Entry("bar"),
                new UTF8Entry("()V")
        );

        given(constantPool.getMethodRefDescriptor(1)).then(it -> {
            expect(it.getClassName()).toBe("Foo");
            expect(it.getMethodName()).toBe("bar");
            expect(it.getDescriptor()).toBe("()V");
        });
    }

    @Test
    public void getDescriptorShouldNotAcceptInvalidArguments() {
        final DefaultConstantPool cp = createConstantPool(new UTF8Entry("foo"));

        expect(() -> cp.getDescriptor(0, MethodRefDescriptor.class)).toThrow(AssertionError.class);
        expect(() -> cp.getDescriptor(1, null)).toThrow(AssertionError.class);
    }

    @Test
    public void getDescriptorShouldFailIfEntryTypeIsNotCorrect() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        expect(() -> constantPool.getDescriptor(1, MethodRefDescriptor.class)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getDescriptorShouldReturnMatchingDescriptor() {
        final DefaultConstantPool constantPool = createConstantPool(
                new FieldRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("Foo"),
                new UTF8Entry("bar"),
                new UTF8Entry("I")
        );

        final FieldRefDescriptor descriptor = constantPool.getDescriptor(1, FieldRefDescriptor.class);

        expect(descriptor.getClassName()).toBe("Foo");
        expect(descriptor.getName()).toBe("bar");
        expect(descriptor.getDescriptor()).toBe("I");
    }

    private DefaultConstantPool createConstantPool(ConstantPoolEntry ... entries) {
        final DefaultConstantPool.Builder builder = new DefaultConstantPool.Builder();

        Arrays.stream(entries).forEach(builder::addEntry);

        return builder.create();
    }

}
