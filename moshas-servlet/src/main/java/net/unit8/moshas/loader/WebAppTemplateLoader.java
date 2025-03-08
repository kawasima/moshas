package net.unit8.moshas.loader;

import jakarta.servlet.ServletContext;
import java.io.InputStream;

/**
 * @author kawasima
 */
public class WebAppTemplateLoader extends TemplateLoader {
    private String prefix;
    private String suffix;
    private ServletContext servletContext;

    public WebAppTemplateLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public InputStream getTemplateStream(String templateSource) throws TemplateNotFoundException {
        if (prefix != null) {
            templateSource = prefix + templateSource;
        }
        if (suffix != null) {
            templateSource = templateSource + suffix;
        }

        if (templateSource.startsWith("/")) {
            templateSource = templateSource.replaceAll("^/(.*)", "$1");
        }
        InputStream is = servletContext.getResourceAsStream(templateSource);
        if (is == null) {
            throw new TemplateNotFoundException("Can't find template " + templateSource);
        }
        return is;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
