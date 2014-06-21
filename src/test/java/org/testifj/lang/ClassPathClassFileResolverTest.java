package org.testifj.lang;

import org.junit.Test;
import org.testifj.lang.classfile.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

public class ClassPathClassFileResolverTest {

    private final ClassFileReader classFileReader = mock(ClassFileReader.class);

    private final ClassPathClassFileResolver resolver = new ClassPathClassFileResolver(classFileReader);

    @Test
    public void constructorShouldNotAcceptNullClassLoader() {
        expect(() -> new ClassPathClassFileResolver(null)).toThrow(AssertionError.class);
    }

    @Test
    public void resolveClassFileShouldNotAcceptNullType() {
        expect(() -> resolver.resolveClassFile(null)).toThrow(AssertionError.class);
    }

    @Test
    public void resolveClassFileShouldFailIfResourceCannotBeFound() {
        final Type unResolvableType = mock(Type.class);

        when(unResolvableType.getTypeName()).thenReturn("com.foo.bar.Invalid");

        expect(() -> resolver.resolveClassFile(unResolvableType)).toThrow(ClassFileNotFoundException.class);
    }

    @Test
    public void resolveClassShouldFailIfLoadingFailsWithIOException() throws Exception {
        final IOException cause = new IOException();

        when(classFileReader.read(any(InputStream.class))).thenThrow(cause);

        expect(() -> resolver.resolveClassFile(getClass()))
                .toThrow(ClassFileResolutionException.class)
                .where(e -> e.getCause().equals(cause));
    }

    @Test
    public void resolveClassFileShouldReturnClassFileFromClassFileReader() throws IOException {
        final ClassFile classFile = mock(ClassFile.class);

        when(classFileReader.read(any(InputStream.class))).thenReturn(classFile);

        expect(resolver.resolveClassFile(getClass())).toBe(classFile);
    }

}
