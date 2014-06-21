package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.LineNumberTable;
import org.testifj.lang.classfile.LineNumberTableEntry;
import org.testifj.lang.Range;
import org.testifj.lang.classfile.impl.LineNumberTableImpl;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class LineNumberTableImplTest {

    private final LineNumberTableEntry exampleEntry = mock(LineNumberTableEntry.class);

    private final LineNumberTable exampleTable = new LineNumberTableImpl(new LineNumberTableEntry[]{exampleEntry}, new Range(1, 2));

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LineNumberTableImpl(null, new Range(0, 1))).toThrow(AssertionError.class);
        expect(() -> new LineNumberTableImpl(new LineNumberTableEntry[]{mock(LineNumberTableEntry.class)}, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() throws IOException {
        expect(exampleTable.getEntries().toArray()).toBe(new Object[]{exampleEntry});
        expect(exampleTable.getSourceFileRange()).toBe(new Range(1, 2));
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleTable).toBe(equalTo(exampleTable));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleTable).not().toBe(equalTo(null));
        expect((Object) exampleTable).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final LineNumberTable other = new LineNumberTableImpl(new LineNumberTableEntry[]{exampleEntry}, new Range(1, 2));

        expect(exampleTable).toBe(equalTo(other));
        expect(exampleTable.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        expect(exampleTable.toString()).to(containString(exampleEntry.toString()));
        expect(exampleTable.toString()).to(containString(new Range(1, 2).toString()));
    }
}
