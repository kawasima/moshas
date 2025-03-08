package net.unit8.moshas.context;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author kawasima
 */
public class RequestScope implements VariableScope {
    private final HttpServletRequest request;

    public RequestScope(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Object get(String key) {
        return request.getAttribute(key);
    }

    @Override
    public String name() {
        return "request";
    }

}
