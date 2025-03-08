package net.unit8.moshas.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author kawasima
 */
public class SessionScope implements VariableScope {
    private final HttpSession session;

    public SessionScope(HttpServletRequest request) {
        this.session = request.getSession(false);
    }

    @Override
    public Object get(String key) {
        if (session == null)
            return null;
        return session.getAttribute(key);
    }

    @Override
    public String name() {
        return "session";
    }
}
