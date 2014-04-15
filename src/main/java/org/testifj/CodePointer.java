package org.testifj;

import org.testifj.lang.Method;
import org.testifj.lang.model.Element;

/**
 * A <code>CodePointer</code> references a particular element in a method. It can be an node in the
 * syntax tree.
 */
public final class CodePointer {

    private final Method method;

    private final Element element;

    public CodePointer(Method method, Element element) {
        assert method != null : "Method can't be null";
        assert element != null : "Element can't be null";

        this.method = method;
        this.element = element;
    }

    public Method getMethod() {
        return method;
    }

    public Element getElement() {
        return element;
    }

    public CodePointer forElement(Element element) {
        return new CodePointer(method, element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodePointer that = (CodePointer) o;

        if (!element.equals(that.element)) return false;
        if (!method.equals(that.method)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + element.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CodePointer{" +
                "method=" + method +
                ", element=" + element +
                '}';
    }
}
