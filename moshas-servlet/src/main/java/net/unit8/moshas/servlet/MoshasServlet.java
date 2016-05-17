package net.unit8.moshas.servlet;

import net.unit8.moshas.MoshasEngine;
import net.unit8.moshas.ServletMoshasEngineProvier;
import net.unit8.moshas.Template;
import net.unit8.moshas.context.WebContext;
import net.unit8.moshas.loader.ResourceTemplateLoader;
import net.unit8.moshas.loader.WebAppTemplateLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author kawasima
 */
public class MoshasServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String prefix = Optional.ofNullable(config.getInitParameter("prefix")).orElse("templates");
        String suffix = Optional.ofNullable(config.getInitParameter("suffix")).orElse("templates");

        ServletMoshasEngineProvier.init(engine -> {
            WebAppTemplateLoader webAppTemplateLoader = new WebAppTemplateLoader(config.getServletContext());
            ResourceTemplateLoader resourceTemplateLoader = new ResourceTemplateLoader();
            resourceTemplateLoader.setPrefix(prefix);
            resourceTemplateLoader.setSuffix(suffix);
            engine.getTemplateManager().setTemplateLoaders(webAppTemplateLoader, resourceTemplateLoader);
        });
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebContext context = new WebContext(getServletContext(), request);
        MoshasEngine engine = ServletMoshasEngineProvier.get();
        engine.process(request.getServletPath(), context, response.getWriter());
    }

    protected String parseTemplatePath(String originalPath, HttpServletRequest request, HttpServletResponse response) {
        return originalPath;
    }
}
