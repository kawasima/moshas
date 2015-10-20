package net.unit8.moshas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author kawasima
 */
public class Context {
    private final Map<String, Object> variables;
    
    public Context() {
        variables = new HashMap<>();
    }
    
    public Context(Map<String, Object> initialVariables) {
        variables = new HashMap<>(initialVariables);
    }
    
    public Object get(String... keys) {
        Object current = variables;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map)current).get(key);
            } else {
                try {
                    current = PropertyUtils.getProperty(current, key);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return current;
    }
    
    public int getInt(String... keys) {
        return (int)ConvertUtils.convert(get(keys), int.class);
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
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public void localScope(String n1, Object v1, String n2, Object v2, WithLocalScope f) {
        variables.put(n1, v1);
        variables.put(n2, v2);
        try {
            f.evaluate();
        } finally {
            variables.remove(n1);
            variables.remove(n2);
        }
    }
    
    public void localScope(String name, Object value, WithLocalScope f) {
        variables.put(name, value);
        try {
            f.evaluate();
        } finally {
            variables.remove(name);
        }
    }
}
