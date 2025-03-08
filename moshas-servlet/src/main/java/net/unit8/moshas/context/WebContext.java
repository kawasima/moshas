package net.unit8.moshas.context;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author kawasima
 */
public class WebContext extends AbstractContext {
    public WebContext(ServletContext servletContext, HttpServletRequest request) {
        setScope(new RequestScope(request),
                new SessionScope(request),
                new ApplicationScope(servletContext));
    }
}
