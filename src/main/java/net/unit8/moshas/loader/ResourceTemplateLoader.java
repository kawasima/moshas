package net.unit8.moshas.loader;

import java.io.InputStream;

/**
 *
 * @author kawasima
 */
public class ResourceTemplateLoader extends TemplateLoader {
    @Override
    public InputStream getTemplateStream(String templateSource) throws TemplateNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        InputStream is = cl.getResourceAsStream(templateSource);
        if (is == null) {
            throw new TemplateNotFoundException("Can't find template " + templateSource);
        }
        
        return is;
    }
}
