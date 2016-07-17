package org.testifj.framework;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CastSuffixTest {

    @Test
    public void instanceCanBeCastToCorrectType() {
        final BaseClass baseClass = new SubClass1();

        assertEquals(Optional.of(baseClass), baseClass.as(SubClass1.class));
    }

    @Test
    public void instanceCannotBeCastToIncorrectType() {
        final BaseClass baseClass = new SubClass1();

        assertFalse(baseClass.as(SubClass2.class).isPresent());
    }

    public static class BaseClass implements CastSuffix<BaseClass> {
    }

    public static class SubClass1 extends BaseClass {}

    public static class SubClass2 extends BaseClass {}

}