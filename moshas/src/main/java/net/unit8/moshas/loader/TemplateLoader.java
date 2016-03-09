package net.unit8.moshas.loader;

import java.io.InputStream;

/**
 *
 * @author kawasima
 */
public abstract class TemplateLoader {
    public abstract InputStream getTemplateStream(String templateSource) throws TemplateNotFoundException;
}
