package net.unit8.moshas;

import net.unit8.moshas.dom.Element;

/**
 *
 * @author kawasima
 */
public class MoshasEngine {
    final TemplateManager manager;

    public MoshasEngine(TemplateManager manager) {
        this.manager = manager;
    }

    public MoshasEngine() {
        this(new StandardTemplateManager());
    }

    public Template defineTemplate(String source, TemplateDefinition def) {
        Template template = manager.getTemplate(source);
        def.define(template);
        Element root = template.getRootElement();
        root.children().forEach(Element::selected);
        return template;
    }

    public Snippet defineSnippet(String source, String selector, TemplateDefinition def) {
        Template template = manager.getTemplate(source);
        Element el = template.getRootElement().select(selector).first();
        Snippet snippet = new Snippet(el);
        def.define(snippet);
        return snippet;
    }

    public TemplateManager getTemplateManager() {
        return manager;
    }
}
