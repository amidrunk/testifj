package org.testifj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ServiceContextImpl implements ServiceContext {

    private final List<Object> components;

    private ServiceContextImpl(List<Object> components) {
        this.components = new ArrayList<>(components);
    }

    @Override
    public <T> T get(Class<T> type) {
        assert type != null : "Type can't be null";

        final Object[] matchingComponents = components.stream()
                .filter(c -> type.isInstance(c))
                .toArray(Object[]::new);

        if (matchingComponents.length == 0) {
            throw new DependencyResolutionException("No component exists for type: " + type.getName());
        } else if (matchingComponents.length > 1) {
            throw new DependencyResolutionException("Multiple matching components exists for type: "
                    + type.getName() + ", " + Arrays.asList(matchingComponents));
        }

        return (T) matchingComponents[0];
    }

    public static ServiceContextBuilder newBuilder() {
        return new ServiceContextBuilder();
    }

    public static final class ServiceContextBuilder {

        private final List<Object> components = new LinkedList<>();

        public ServiceContextBuilder registerComponent(Object component) {
            assert component != null : "Component can't be null";
            components.add(component);
            return this;
        }

        public ServiceContext build() {
            return new ServiceContextImpl(components);
        }

    }

}
