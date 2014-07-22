package org.testifj.lang.classfile;

public interface BootstrapMethodDescriptor extends ConstantPoolEntryDescriptor {

    MethodRefDescriptor getBootstrapMethodRefDescriptor();

    ConstantPoolEntryDescriptor[] getBootstrapArgumentsDescriptor();

}
