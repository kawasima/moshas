package net.unit8.moshas.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kawasima
 */
public class PageScope implements VariableScope, Serializable {
    private final Map<String, Object> variables;

    public PageScope() {
        variables = new HashMap<>();
    }

    @Override
    public Object get(String key) {
        return variables.get(key);
    }

    @Override
    public String name() {
        return "page";
    }

    public void set(String key, Object value) {
        variables.put(key, value);
    }

    public void remove(String key) {
        variables.remove(key);
    }
}
