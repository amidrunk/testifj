package org.testifj;

import java.util.List;

public interface Description {

    Description appendText(String text);

    Description appendDescription(Description description);

    Description appendValue(Object value);

    List<Part> getParts();

    String toString();

    public interface Part {
    }

    public interface TextPart extends Part {
        String getText();
    }

    public interface ValuePart extends Part {
        Object getValue();
    }

    public interface DescriptionPart extends Part {
        Description getDescription();
    }

}
