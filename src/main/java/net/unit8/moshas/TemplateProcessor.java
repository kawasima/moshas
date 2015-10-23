package net.unit8.moshas;

import java.util.List;
import net.unit8.moshas.dom.TemplateElement;

/**
 *
 * @author kawasima
 */
public class TemplateProcessor {
    private final List<TemplateElement> selectedElements;
    private final RenderFunction f;
    public TemplateProcessor(List<TemplateElement> selectedElements, RenderFunction f) {
        this.selectedElements = selectedElements;
        this.f = f;
        
    }
    
    public void process(TemplateElement el, Context ctx) {
        if (selectedElements == null) {
            f.render(el, ctx);
        } else {
            selectedElements.forEach((child) -> {
                f.render(child, ctx);
            });
        }
    }
}
