package net.unit8.moshas.servlet;

import net.unit8.moshas.MoshasEngine;
import net.unit8.moshas.context.WebContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kawasima
 */
public class MoshasServlet extends HttpServlet {
    private transient MoshasEngine engine;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        engine = new MoshasEngine();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        WebContext context = new WebContext(getServletContext(), request);
    }
}
