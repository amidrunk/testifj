package org.testifj.lang.decompile;

import org.testifj.lang.classfile.Member;
import org.testifj.lang.model.Signature;

public interface Constructor extends Member {

    Signature getSignature();

}
