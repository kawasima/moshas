package net.unit8.moshas.context;

import java.util.Map;

/**
 *
 * @author kawasima
 */
public class Context extends AbstractContext {

    public Context() {
        super();
    }

    public Context(Map<String, Object> variables) {
        super();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            setVariable(entry.getKey(), entry.getValue());
        }
    }
}
