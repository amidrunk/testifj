package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;

public interface BootstrapMethodDescriptor extends ConstantPoolEntryDescriptor {

    MethodRefDescriptor getBootstrapMethodRefDescriptor();

    ConstantPoolEntryDescriptor[] getBootstrapArgumentsDescriptor();

}
