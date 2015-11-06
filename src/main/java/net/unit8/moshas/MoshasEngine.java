package net.unit8.moshas;

import net.unit8.moshas.dom.Element;
import net.unit8.moshas.dom.Node;
import net.unit8.moshas.select.NodeTraversor;

/**
 *
 * @author kawasima
 */
public class MoshasEngine {
    TemplateManager manager = new StandardTemplateManager();
    
    public Template defineTemplate(String source, TemplateDefinition def) {
        Template template = manager.getTemplate(source);
        def.define(template);
        // TODO normalize
        Element root = template.getRootElement();
        for (Element child : root.children()) {
            child.selected();
        }
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
