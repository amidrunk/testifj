package org.testifj.util;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.Given;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.IteratorThatIs.iteratorOf;

@SuppressWarnings("unchecked")
public class SuppliedIteratorTest {

    @Test
    public void constructorShouldNotAcceptNullSupplier() {
        expect(() -> new SuppliedIterator<>(null)).toThrow(AssertionError.class);
    }

    @Test
    public void suppliedElementsShouldBeIterated() {
        final Supplier supplier = mock(Supplier.class);

        when(supplier.get()).thenReturn(Optional.of("foo"), Optional.of("bar"), Optional.empty());

        given(new SuppliedIterator(supplier)).then(iterator -> {
            expect(iterator).toBe(iteratorOf("foo", "bar"));
        });
    }

}