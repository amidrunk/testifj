package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementMetaData;

import java.util.Map;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class ElementMetaDataImplTest {

    private final ElementMetaDataImpl elementMetaData = new ElementMetaDataImpl();

    @Test
    public void setAttributeShouldNotAcceptNullKey() {
        expect(() -> elementMetaData.setAttribute(null, "foo")).toThrow(AssertionError.class);
    }

    @Test
    public void getAttributeShouldNotAcceptNullAttributeKey() {
        expect(() -> elementMetaData.getAttribute(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getAttributeShouldReturnNullIfAttributeDoesNotExists() {
        expect(elementMetaData.getAttribute("foo")).toBe(equalTo(null));
    }

    @Test
    public void getAttributeShouldReturnAttributeValue() {
        given(elementMetaData).
        when(it -> it.setAttribute("foo", "bar")).
        then(it -> expect(it.getAttribute("foo")).toBe("bar"));
    }

    @Test
    public void getAttributesShouldReturnAllAttributes() {
        elementMetaData.setAttribute("foo", "bar");

        final Map<String,Object> attributes = elementMetaData.getAttributes();

        expect(attributes.size()).toBe(1);
        expect(attributes.get("foo")).toBe("bar");
    }

    @Test
    public void getAttributesShouldReturnCopyOfOriginalAttributes() {
        final Map<String, Object> attributes = elementMetaData.getAttributes();

        elementMetaData.setAttribute("foo", "bar");

        expect(attributes.isEmpty()).toBe(true);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(elementMetaData).toBe(equalTo(elementMetaData));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(elementMetaData).not().toBe(equalTo(null));
        expect((Object) elementMetaData).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualAttributesShouldBeEqual() {
        final ElementMetaDataImpl other = new ElementMetaDataImpl();

        other.setAttribute("foo", "bar");
        elementMetaData.setAttribute("foo", "bar");

        expect(elementMetaData).toBe(equalTo(other));
        expect(elementMetaData.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainAttributes() {
        elementMetaData.setAttribute("foo", "bar");

        expect(elementMetaData.toString()).to(containString("foo"));
        expect(elementMetaData.toString()).to(containString("bar"));
    }

}
