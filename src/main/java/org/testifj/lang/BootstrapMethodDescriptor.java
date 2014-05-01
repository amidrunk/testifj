package org.testifj.lang;

public interface BootstrapMethodDescriptor extends ConstantPoolEntryDescriptor {

    MethodRefDescriptor getBootstrapMethodRefDescriptor();

    ConstantPoolEntryDescriptor[] getBootstrapArgumentsDescriptor();

}
