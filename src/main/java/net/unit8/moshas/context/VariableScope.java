package net.unit8.moshas.context;

/**
 * @author kawasima
 */
public interface VariableScope {
    String name();
    Object get(String key);
}
