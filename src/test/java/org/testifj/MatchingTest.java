package org.testifj;

import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Matching.match;
import static org.testifj.matchers.core.InstanceOf.instanceOf;

@SuppressWarnings("unchecked")
public class MatchingTest {

    @Test
    public void matchShouldExecuteMatchingCase() {
        final Action action = mock(Action.class);

        match("foo").when(str -> true).then(action);

        verify(action).execute(eq("foo"));
    }

    @Test
    public void matchShouldExecuteFirstMatchIfSeveralMatches() {
        final Action action1 = mock(Action.class);
        final Action action2 = mock(Action.class);

        match("foo")
                .when(str -> true).then(action1)
                .when(str -> true).then(action2);

        verify(action1).execute(eq("foo"));
        verifyZeroInteractions(action2);
    }

    @Test
    public void matchShouldExecuteNothingIfNoCaseExists() {
        final Action action = mock(Action.class);

        match("foo").when((str) -> false).then(action);

        verifyZeroInteractions(action);
    }

    @Test(expected = AssertionError.class)
    public void whenShouldNotAcceptNullPredicate() {
        match("foo").when(null);
    }

    @Test(expected = AssertionError.class)
    public void thenShouldNotAcceptNullAction() {
        match("foo").when(str -> true).then(null);
    }

    @Test
    public void instanceOfShouldPromoteMatchedType() {
        final Object object = "foo";
        final Action action = mock(Action.class);

        match(object).when(instanceOf(String.class)).then(str -> action.execute(str.toUpperCase()));

        verify(action).execute(eq("FOO"));
    }

}
