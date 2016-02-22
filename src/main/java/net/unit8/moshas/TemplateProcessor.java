package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.dom.Element;
import net.unit8.moshas.select.Elements;

import java.io.Serializable;

/**
 *
 * @author kawasima
 */
public class TemplateProcessor implements Serializable {
    private final Elements selectedElements;
    private final RenderFunction f;
    public TemplateProcessor(Elements selectedElements, RenderFunction f) {
        this.selectedElements = selectedElements;
        this.f = f;

    }

    public void process(Element el, Context ctx) {
        if (selectedElements == null) {
            f.render(el, ctx);
        } else {
            selectedElements.forEach((child) -> f.render(child, ctx));
        }
    }
}
