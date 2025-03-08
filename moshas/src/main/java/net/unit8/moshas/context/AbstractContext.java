package net.unit8.moshas.context;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author kawasima
 */
public abstract class AbstractContext implements IContext {
    private final List<VariableScope> scopes = new LinkedList<>();
    final PageScope defaultScope = new PageScope();
    private boolean throwableException = false;

    protected AbstractContext() {
        setScope(defaultScope);
    }

    private Object getProperty(Object bean, String propertyName) {
        try {
            String methodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            Method method = bean.getClass().getMethod(methodName);
            return method.invoke(bean);
        } catch (Exception ex) {
            if (throwableException) {
                throw new RuntimeException(ex);
            } else {
                return null;
            }
        }
    }

    private Object convert(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType.isInstance(value)) {
            return value;
        }

        String stringValue = String.valueOf(value);
        
        if (targetType == int.class || targetType == Integer.class) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (targetType == double.class || targetType == Double.class) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else if (targetType == String.class) {
            return stringValue;
        }
        
        return value;
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
                    current = getProperty(current, key);
                }
                if (current == null) {
                    return null;
                }
            }
            return current;
        } else {
            return firstValue;
        }
    }

    @Override
    public int getInt(String... keys) {
        return (int) convert(get(keys), int.class);
    }

    @Override
    public Double getDouble(String... keys) {
        return (Double) convert(get(keys), Double.class);
    }

    @Override
    public String getString(String... keys) {
        Object value = get(keys);
        return value == null ? "" : (String) convert(value, String.class);
    }

    @Override
    public <E> Collection<E> getCollection(String... keys) {
        Object val = get(keys);
        if (val == null) {
            return new ArrayList<>();
        }
        if (val instanceof Collection) {
            return (Collection<E>) val;
        } else if (val.getClass().isArray()) {
            return Arrays.asList((E[]) val);
        } else {
            List<E> list = new ArrayList<>();
            list.add((E) val);
            return list;
        }
    }

    protected void setScope(VariableScope... scopes) {
        Collections.addAll(this.scopes, scopes);
    }

    public void setVariable(String key, Object value) {
        defaultScope.set(key, value);
    }

    @Override
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

    @Override
    public void localScope(String name, Object value, WithLocalScope f) {
        defaultScope.set(name, value);
        try {
            f.evaluate();
        } finally {
            defaultScope.remove(name);
        }
    }

    public void setThrowableException(boolean throwableException) {
        this.throwableException = throwableException;
    }
}
