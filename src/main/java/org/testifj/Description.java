package org.testifj;

import java.util.List;

public interface Description {

    Description appendText(String text);

    List<Part> getParts();

    String toString();

    public interface Part {
    }

    public interface TextPart extends Part {

        String getText();

    }

}
