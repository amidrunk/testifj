package org.testifj;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.testifj.lang.Procedure;

public class SpecificationRunner extends Runner {

    private final Class<?> testClass;

    public SpecificationRunner(Class<?> testClass) {
        assert testClass != null : "Test class can't be null";
        assert Specification.class.isAssignableFrom(testClass) : "Test class must be a sub-class of " + Specification.class.getName();

        this.testClass = testClass;
    }

    @Override
    public Description getDescription() {
        return Description.createSuiteDescription(testClass);
    }

    @Override
    public void run(RunNotifier notifier) {
        final Result result = new Result();
        notifier.addListener(result.createListener());

        notifier.fireTestRunStarted(getDescription());

        final Specification specification;

        try {
            specification = (Specification) testClass.newInstance();
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(getDescription(), e));
            return;
        }

        for (SpecificationDescription specificationDescription : specification.getSpecificationDescriptions()) {
            final Description testDescription = Description.createTestDescription(testClass.getName(), specificationDescription.getTargetName()); // Hierarchical

            notifier.fireTestStarted(testDescription);

            try {
                specificationDescription.getAction().execute((description, procedure) -> new Specifier() {
                    @Override
                    public void should(String description, Procedure procedure) {
                        System.out.println("it.should('" + description + "')");
                    }
                });
            } catch (Exception e) {
                throw new AssertionError(); // TODO FIX
            }

            notifier.fireTestFinished(testDescription);
        }

        notifier.fireTestRunFinished(result);
    }
}
