package org.testifj.framework;

/**
 * <p>
 * An <code>Expectation</code> encapsulates a statement that something is expected. This may be both that a
 * procedure might have a particular behaviour or it may be that a value complies to a set of criteria.
 * </p>
 *
 * <p>
 * Expectations comply to a tree structure such that and/or junctions constitute branches within the criteria
 * tree. The reason for this - rather than bundling criteria - is to increase the insight into what has actually
 * gone wrong when validating a criterion.
 * </p>
 */
public interface Expectation<T> extends CastSuffix<Expectation<T>> {

    /**
     * Returns the subject of the expectation. When performing an expectation on a value, this is typically
     * a value (an object). When doing behavioural expectations, this would typically be a (non-invoked)
     * procedure.
     *
     * @return The subject of the expectation.
     */
    T getSubject();

    ExpectationReference getExpectationReference();

    Criterion getCriterion();

}
