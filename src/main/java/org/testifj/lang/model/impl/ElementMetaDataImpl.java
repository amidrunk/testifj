package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementMetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ElementMetaDataImpl implements ElementMetaData {

    private final Map<String, Object> attributes = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void setAttribute(String key, Object value) {
        assert key != null : "Key can't be null";

        lock.writeLock().lock();

        try {
            attributes.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Object getAttribute(String key) {
        assert key != null : "Key can't be null";

        lock.readLock().lock();

        try {
            return attributes.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, Object> getAttributes() {
        lock.readLock().lock();

        try {
            return new HashMap<>(attributes);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementMetaDataImpl that = (ElementMetaDataImpl) o;

        if (!getAttributes().equals(that.getAttributes())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        lock.readLock().lock();

        try {
            return attributes.hashCode();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();

        try {
            return "ElementMetaDataImpl{" +
                    "attributes=" + attributes +
                    '}';
        } finally {
            lock.readLock().unlock();
        }
    }
}
