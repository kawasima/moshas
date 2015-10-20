package net.unit8.moshas;

import org.jsoup.nodes.Element;

/**
 *
 * @author kawasima
 */
public class TemplateProcessor {
    private final String selector;
    private final RenderFunction f;
    public TemplateProcessor(String selector, RenderFunction f) {
        this.selector = selector;
        this.f = f;
        
    }
    
    public void process(Element el, Context ctx) {
        if (selector == null) {
            f.render(el, ctx);
        } else {
            el.select(selector).forEach((child) -> {
                f.render(child, ctx);
            });
        }
    }
}
