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

    protected void describe(String targetName, Action<Specifier> procedure) {
        descriptions.add(new SpecificationDescription(targetName, procedure));
    }

}
