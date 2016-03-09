package net.unit8.moshas.context;

/**
 * @author kawasima
 */
public interface IContext {
    Object get(String... keys);
}
