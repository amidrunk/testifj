package org.testifj;

import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpecificationRunner.class)
public abstract class Specification {

    private final List<SpecificationDescription> descriptions = new LinkedList<>();

    public List<SpecificationDescription> getSpecificationDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

    protected void describe(String targetName, Action<Specifier> action) {
        assert targetName != null && !targetName.isEmpty() : "Target name can't be null or empty";
        assert action != null : "Action can't be null";

        descriptions.add(new SpecificationDescription(targetName, action));
    }

}
