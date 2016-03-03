package net.unit8.moshas.cache;

import net.unit8.moshas.Template;

public interface TemplateCache {
    Template getTemplate(String source);

    void putTemplate(String source, Template template);
}
