package net.unit8.moshas;

import net.unit8.moshas.loader.TemplateLoader;

/**
 *
 * @author kawasima
 */
public interface TemplateManager {
    Template loadTemplate(String templateName);

    Template getTemplate(String source);
    void setTemplateLoaders(TemplateLoader... loaders);

    void cacheTemplate(String templateName, Template template);
}
