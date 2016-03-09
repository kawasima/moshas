package net.unit8.moshas.loader;

import java.io.InputStream;

/**
 *
 * @author kawasima
 */
public class ResourceTemplateLoader extends TemplateLoader {
    private String prefix;
    private String suffix;

    @Override
    public InputStream getTemplateStream(String templateSource) throws TemplateNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }

        if (prefix != null) {
            templateSource = prefix + templateSource;
        }
        if (suffix != null) {
            templateSource = templateSource + suffix;
        }

        if (templateSource.startsWith("/")) {
            templateSource = templateSource.replaceAll("^/(.*)", "$1");
        }
        InputStream is = cl.getResourceAsStream(templateSource);
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
