package org.testifj;

import org.testifj.lang.Method;
import org.testifj.lang.model.Element;

/**
 * A reference to a particular syntax element in the syntax tree.
 *
 * TOGO Add line number.
 */
public interface CodePointer {

    Method getMethod();

    Element getElement();

    CodePointer forElement(Element element);

}
