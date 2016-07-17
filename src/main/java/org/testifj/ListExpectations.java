package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.CollectionThatIs;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ListExpectations<E> extends ExpectationsBase<List<E>, ListExpectations<E>> {

    public ListExpectations(ExpectationContext<List<E>> context) {
        super(context);
    }

    public void toBeEmpty() {
        reportValueExpectation(CollectionThatIs.empty());
    }

    public void toHaveSize(int size) {
        reportValueExpectation(CollectionThatIs.ofSize(size));
    }

    @SafeVarargs
    public final void toContain(Matcher<E> ... matchers) {
        assert matchers != null;
        reportValueExpectation(CollectionThatIs.containing(matchers));
    }

    public final void toContainExactly(Object ... values) {
        assert values != null;
        final Optional expected = Optional.of(values);
        reportValueExpectation(CollectionThatIs.collectionOf(values), expected);
    }

    @SafeVarargs
    public final void toContainExactly(Matcher<E> ... matchers) {
        assert matchers != null;
        reportValueExpectation(CollectionThatIs.collectionWithElements(matchers));
    }

    @Override
    protected ListExpectations<E> newInstance(ExpectationContext<List<E>> context) {
        return new ListExpectations<E>(context);
    }
}
