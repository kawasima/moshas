package net.unit8.moshas.context;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kawasima
 */
public abstract class AbstractContext implements IContext {
    private final List<VariableScope> scopes = new LinkedList<>();
    final PageScope defaultScope = new PageScope();

    protected AbstractContext() {
        setScope(defaultScope);
    }

    @Override
    public Object get(String... keys) {
        if (keys == null || keys.length == 0)
            return null;

        Object firstValue = null;
        for (VariableScope scope : scopes) {
            firstValue = scope.get(keys[0]);
            if (firstValue != null) break;
        }
        if (firstValue == null)
            return null;

        if (keys.length > 1) {
            Object current = firstValue;
            for (int i=1; i< keys.length; i++) {
                String key = keys[i];
                if (current instanceof Map) {
                    current = ((Map) current).get(key);
                } else {
                    try {
                        current = PropertyUtils.getProperty(current, key);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            return current;
        } else {
            return firstValue;
        }
    }

    public int getInt(String... keys) {
        return (int) ConvertUtils.convert(get(keys), int.class);
    }

    public Double getDouble(String... keys) {
        return (double)ConvertUtils.convert(get(keys), double.class);
    }

    public String getString(String... keys) {
        return (String)ConvertUtils.convert(get(keys), String.class);
    }

    public <E> Collection<E> getCollection(String... keys) {
        Object val = get(keys);
        if (val instanceof Collection) {
            return (Collection) val;
        } else {
            return new ArrayList<>();
        }
    }

    protected void setScope(VariableScope... scopes) {
        for (VariableScope scope : scopes) {
            this.scopes.add(scope);
        }
    }

    public void setVariable(String key, Object value) {
        defaultScope.set(key, value);
    }
    public void localScope(String n1, Object v1, String n2, Object v2, WithLocalScope f) {
        defaultScope.set(n1, v1);
        defaultScope.set(n2, v2);

        try {
            f.evaluate();
        } finally {
            defaultScope.remove(n1);
            defaultScope.remove(n2);
        }
    }

    public void localScope(String name, Object value, WithLocalScope f) {
        defaultScope.set(name, value);
        try {
            f.evaluate();
        } finally {
            defaultScope.remove(name);
        }
    }

}
