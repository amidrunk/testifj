package org.testifj;

import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.ClassFileReader;
import org.testifj.lang.classfile.impl.ClassFileReaderImpl;

import java.io.InputStream;

public final class LambdaToString {

    public static String toString(Object lambda) throws Exception {
        assert lambda != null;
        assert lambda.getClass().getInterfaces().length == 1;
        assert lambda.getClass().getInterfaces()[0].getAnnotation(FunctionalInterface.class) != null;

        final Class<?> lambdaClass = lambda.getClass();
        final String ownerClassName = lambdaClass.getName().substring(0, lambdaClass.getName().lastIndexOf("$$"));
        final Class<?> ownerClass = Class.forName(ownerClassName);
        final String lambdaName = lambdaClass.getName().substring(lambdaClass.getName().lastIndexOf("$$") + 2);
        final ClassFileReader classFileReader = new ClassFileReaderImpl();

        try (InputStream in = ownerClass.getResourceAsStream(ownerClass.getSimpleName() + ".class")) {
            final ClassFile classFile = classFileReader.read(in);

            System.out.println(classFile);
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        final Runnable runnable1 = () -> System.out.println("Hello World!");
        final Runnable runnable2 = () -> System.out.println("Hello universe!");

        toString(runnable1);
    }

}
