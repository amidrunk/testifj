package org.testifj.util;

import java.util.List;
import java.util.Optional;

public final class Lists {

    public static <T> Optional<T> first(List<T> list) {
        assert list != null : "List can't be null";

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    public static <T> Optional<T> last(List<T> list) {
        assert list != null : "List can't be null";

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.get(list.size() - 1));
    }

}
