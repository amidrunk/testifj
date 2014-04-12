package org.testifj.lang.impl;

import org.testifj.lang.ClassFile;
import org.testifj.lang.ClassFileReader;
import org.testifj.lang.ConstantPool;
import org.testifj.lang.ConstantPoolEntry;
import org.junit.Test;
import org.testifj.lang.impl.ClassFileReaderImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class ClassFileReaderImplTest {

    private final ClassFileReader classFileReader = new ClassFileReaderImpl();

    @Test(expected = AssertionError.class)
    public void readShouldNotAcceptNullInputStream() throws IOException {
        classFileReader.read(null);
    }

    @Test
    public void readerShouldResolveVersion() {
        final ClassFile classFile = classFileOf(getClass());

        assertEquals(0, classFile.getMinorVersion());
        assertTrue(classFile.getMajorVersion() >= 52);
    }

    @Test
    public void readShouldFailIfStreamDoesNotStartWithMagicNumber() throws IOException {
        try {
            classFileReader.read(new ByteArrayInputStream("foobar".getBytes()));
            fail();
        } catch (ClassFormatError classFormatError) {
            assertThat(classFormatError.getMessage(), containsString("0xCAFEBABE"));
        }
    }

    @Test
    public void constantPoolShouldContainConstantsInClass() throws Exception {
        final ConstantPool constantPool = classFileOf(getClass()).getConstantPool();

        assertThat(constantPool.getEntries(), hasItem(new ConstantPoolEntry.UTF8Entry("foobar")));
        assertThat(constantPool.getEntries(), hasItem(new ConstantPoolEntry.UTF8Entry("constantPoolShouldContainConstantsInClass")));
    }

    @Test
    public void classNameAndSuperClassAndInterfacesShouldBeResolved() throws Exception {
        final ClassFile classFile = classFileOf(String.class);

        assertTrue(Modifier.isPublic(classFile.getAccessFlags()));
        assertTrue(Modifier.isFinal(classFile.getAccessFlags()));
        assertEquals(String.class.getName(), classFile.getName());
        assertEquals(Object.class.getName(), classFile.getSuperClassName());
        assertArrayEquals(new String[]{
                "java.io.Serializable",
                "java.lang.Comparable",
                "java.lang.CharSequence"
        }, classFile.getInterfaceNames().toArray());
    }

    @Test
    public void fieldsShouldBeRead() {
        final ClassFile classFile = classFileOf(String.class);

        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("value")).findFirst().isPresent());
        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("hash")).findFirst().isPresent());
        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("serialVersionUID")).findFirst().isPresent());
        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("serialPersistentFields")).findFirst().isPresent());
    }

    @Test
    public void methodsShouldBeRead() {
        final ClassFile classFile = classFileOf(String.class);

        assertTrue(classFile.getMethods().stream().filter(m -> m.getName().equals("substring")).findFirst().isPresent());
        assertTrue(classFile.getMethods().stream().filter(m -> m.getName().equals("toString")).findFirst().isPresent());
        assertTrue(classFile.getMethods().stream().filter(m -> m.getName().equals("length")).findFirst().isPresent());
    }

    @Test
    public void constructorsShouldBeRead() {
        assertTrue(classFileOf(getClass()).getConstructors().stream().filter((c) -> c.getSignature().equals("()V")).findFirst().isPresent());
    }

    @Test
    public void attributesShouldBeRead() {
        final ClassFile classFile = classFileOf(getClass());

        System.out.println(classFile.getAttributes());

    }

    protected ClassFile classFileOf(Class<?> clazz) {
        try {
            return classFileReader.read(clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
