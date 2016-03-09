package net.unit8.moshas.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author kawasima
 */
public class WebContext extends AbstractContext {
    public WebContext(ServletContext servletContext, HttpServletRequest request) {
        super();

    }
}
