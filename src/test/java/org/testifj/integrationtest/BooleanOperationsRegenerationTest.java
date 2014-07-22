package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Caller;

import static org.testifj.Expect.expect;

public class BooleanOperationsRegenerationTest extends TestOnDefaultConfiguration {

    private final ExampleClass exampleClass = new ExampleClass();

    @Test
    public void booleanTrueAssignmentCanBeRegenerated() {
        boolean b = true;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = true");
    }

    @Test
    public void booleanFalseAssignmentCanBeRegenerated() {
        boolean b = false;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = false");
    }

    @Test
    public void booleanAssignmentFromMethodCallCanBeRegenerated() {
        boolean b = "hello".isEmpty();

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = \"hello\".isEmpty()");
    }

    @Test
    public void methodCallWithConstantBooleanArgumentCanBeRegenerated() {
        exampleClass.accept(true);
        expect(regenerate(Caller.adjacent(-1))).toBe("exampleClass.accept(true)");

        exampleClass.accept(false);
        expect(regenerate(Caller.adjacent(-1))).toBe("exampleClass.accept(false)");
    }

    @Test
    public void invertedBooleanCanBeRegenerated() {
        boolean b1 = !Boolean.TRUE.booleanValue();
        expect(regenerate(Caller.adjacent(-1))).toBe("boolean b1 = !Boolean.TRUE.booleanValue()");

        boolean b2 = !Boolean.FALSE.booleanValue();
        expect(regenerate(Caller.adjacent(-1))).toBe("boolean b2 = !Boolean.FALSE.booleanValue()");
    }

    @Test
    public void intEqComparisonCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final int n2 = Integer.valueOf(2).intValue();
        final boolean b = n1 == n2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 == n2");
    }

    @Test
    public void intNeComparisonCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final int n2 = Integer.valueOf(2).intValue();
        final boolean b = n1 != n2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 != n2");
    }

    @Test
    public void intLtComparisonCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final int n2 = Integer.valueOf(2).intValue();
        final boolean b = n1 < n2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 < n2");
    }

    @Test
    public void intLeComparisonCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final int n2 = Integer.valueOf(2).intValue();
        final boolean b = n1 <= n2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 <= n2");
    }

    @Test
    public void intGtComparisonCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final int n2 = Integer.valueOf(2).intValue();
        final boolean b = n1 > n2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 > n2");
    }

    @Test
    public void intGeComparisonCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final int n2 = Integer.valueOf(2).intValue();
        final boolean b = n1 >= n2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 >= n2");
    }

    @Test
    public void objectEqComparisonCanBeRegenerated() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final boolean b = o1 == o2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = o1 == o2");
    }

    @Test
    public void objectNeComparisonCanBeRegenerated() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final boolean b = o1 != o2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = o1 != o2");
    }

    @Test
    public void integerComparisonToZeroCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final boolean b = n1 == 0;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 == 0");
    }

    @Test
    public void integerComparisonToNonZeroCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final boolean b = n1 != 0;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 != 0");
    }

    @Test
    public void integerGeComparisonToZeroCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final boolean b = n1 >= 0;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 >= 0");
    }

    @Test
    public void integerGtComparisonToZeroCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final boolean b = n1 > 0;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 > 0");
    }

    @Test
    public void integerLeComparisonToZeroCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final boolean b = n1 < 0;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 < 0");
    }

    @Test
    public void integerLtComparisonToZeroCanBeRegenerated() {
        final int n1 = Integer.valueOf(1).intValue();
        final boolean b = n1 <= 0;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = n1 <= 0");
    }

    @Test
    public void longEqComparisonCanBeRegenerated() {
        final long l1 = Long.valueOf(1L).longValue();
        final long l2 = Long.valueOf(2L).longValue();
        final boolean b = l1 == l2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = l1 == l2");
    }

    @Test
    public void longNeComparisonCanBeRegenerated() {
        final long l1 = Long.valueOf(1L).longValue();
        final long l2 = Long.valueOf(2L).longValue();
        final boolean b = l1 != l2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = l1 != l2");
    }

    @Test
    public void longLeComparisonCanBeRegenerated() {
        final long l1 = Long.valueOf(1L).longValue();
        final long l2 = Long.valueOf(2L).longValue();
        final boolean b = l1 <= l2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = l1 <= l2");
    }

    @Test
    public void longLtComparisonCanBeRegenerated() {
        final long l1 = Long.valueOf(1L).longValue();
        final long l2 = Long.valueOf(2L).longValue();
        final boolean b = l1 < l2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = l1 < l2");
    }

    @Test
    public void longGeComparisonCanBeRegenerated() {
        final long l1 = Long.valueOf(1L).longValue();
        final long l2 = Long.valueOf(2L).longValue();
        final boolean b = l1 >= l2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = l1 >= l2");
    }

    @Test
    public void longGtComparisonCanBeRegenerated() {
        final long l1 = Long.valueOf(1L).longValue();
        final long l2 = Long.valueOf(2L).longValue();
        final boolean b = l1 > l2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = l1 > l2");
    }

    @Test
    public void floatEqComparisonCanBeRegenerated() {
        final float f1 = Float.valueOf(1f).floatValue();
        final float f2 = Float.valueOf(2f).floatValue();
        final boolean b = f1 == f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = f1 == f2");
    }

    @Test
    public void floatNeComparisonCanBeRegenerated() {
        final float f1 = Float.valueOf(1f).floatValue();
        final float f2 = Float.valueOf(2f).floatValue();
        final boolean b = f1 != f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = f1 != f2");
    }

    @Test
    public void floatLeComparisonCanBeRegenerated() {
        final float f1 = Float.valueOf(1f).floatValue();
        final float f2 = Float.valueOf(2f).floatValue();
        final boolean b = f1 <= f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = f1 <= f2");
    }

    @Test
    public void floatLtComparisonCanBeRegenerated() {
        final float f1 = Float.valueOf(1f).floatValue();
        final float f2 = Float.valueOf(2f).floatValue();
        final boolean b = f1 < f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = f1 < f2");
    }

    @Test
    public void floatGeComparisonCanBeRegenerated() {
        final float f1 = Float.valueOf(1f).floatValue();
        final float f2 = Float.valueOf(2f).floatValue();
        final boolean b = f1 > f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = f1 > f2");
    }

    @Test
    public void floatGtComparisonCanBeRegenerated() {
        final float f1 = Float.valueOf(1f).floatValue();
        final float f2 = Float.valueOf(2f).floatValue();
        final boolean b = f1 >= f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = f1 >= f2");
    }

    @Test
    public void doubleEqComparisonCanBeRegenerated() {
        final double d1 = Double.valueOf(1d).doubleValue();
        final double d2 = Double.valueOf(2d).doubleValue();
        final boolean b = d1 == d2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = d1 == d2");
    }

    @Test
    public void doubleNeComparisonCanBeRegenerated() {
        final double d1 = Double.valueOf(1d).doubleValue();
        final double d2 = Double.valueOf(2d).doubleValue();
        final boolean b = d1 != d2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = d1 != d2");
    }

    @Test
    public void doubleLtComparisonCanBeRegenerated() {
        final double d1 = Double.valueOf(1d).doubleValue();
        final double d2 = Double.valueOf(2d).doubleValue();
        final boolean b = d1 < d2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = d1 < d2");
    }

    @Test
    public void doubleLeComparisonCanBeRegenerated() {
        final double d1 = Double.valueOf(1d).doubleValue();
        final double d2 = Double.valueOf(2d).doubleValue();
        final boolean b = d1 <= d2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = d1 <= d2");
    }

    @Test
    public void doubleGtComparisonCanBeRegenerated() {
        final double d1 = Double.valueOf(1d).doubleValue();
        final double d2 = Double.valueOf(2d).doubleValue();
        final boolean b = d1 > d2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = d1 > d2");
    }

    @Test
    public void doubleGeComparisonCanBeRegenerated() {
        final double d1 = Double.valueOf(1d).doubleValue();
        final double d2 = Double.valueOf(2d).doubleValue();
        final boolean b = d1 >= d2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = d1 >= d2");
    }

    @Test
    public void logicalAndOperationCanBeRegenerated() {
        final boolean b1 = Boolean.TRUE;
        final boolean b2 = Boolean.TRUE;
        final boolean b = b1 && b2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = b1 && b2");
    }

    @Test
    public void logicalOrOperationCanBeRegenerated() {
        final boolean b1 = Boolean.TRUE;
        final boolean b2 = Boolean.TRUE;
        final boolean b = b1 || b2;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = b1 || b2");
    }

    @Test
    public void multipleLogicalOperationsCanBeRegenerated() {
        final boolean b1 = Boolean.TRUE;
        final boolean b2 = Boolean.TRUE;
        final boolean b3 = Boolean.TRUE;

        boolean b4 = b1 || b2 && b3;
        expect(regenerate(Caller.adjacent(-1))).toBe("boolean b4 = b1 || b2 && b3");

        boolean b5 = b1 && b2 || b3;
        expect(regenerate(Caller.adjacent(-1))).toBe("boolean b5 = b1 && b2 || b3");

        boolean b6 = b1 && b2 || b3 && b4;
        expect(regenerate(Caller.adjacent(-1))).toBe("boolean b6 = b1 && b2 || b3 && b4");
    }

    public static final class ExampleClass {

        public void accept(boolean b) {}

    }
}
