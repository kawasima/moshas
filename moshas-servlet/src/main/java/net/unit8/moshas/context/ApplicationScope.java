package net.unit8.moshas.context;

import jakarta.servlet.ServletContext;

/**
 * @author kawasima
 */
public class ApplicationScope implements VariableScope {
    private final ServletContext servletContext;

    public ApplicationScope(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Object get(String key) {
        return servletContext.getAttribute(key);
    }

    @Override
    public String name() {
        return "application";
    }
}
