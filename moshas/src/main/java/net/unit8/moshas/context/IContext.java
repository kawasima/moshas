package net.unit8.moshas.context;

import java.util.Collection;

/**
 * @author kawasima
 */
public interface IContext {
    Object get(String... keys);

    int getInt(String... keys);

    Double getDouble(String... keys);

    String getString(String... keys);

    <E> Collection<E> getCollection(String... keys);

    void localScope(String n1, Object v1, String n2, Object v2, WithLocalScope f);

    void localScope(String name, Object value, WithLocalScope f);
}
