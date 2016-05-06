package net.unit8.moshas;

import net.unit8.moshas.dom.Element;
import net.unit8.moshas.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kawasima
 */
public class ViewLogicDescriptor {
    private Element rootElement;
    private List<TemplateProcessor> processors;

    public ViewLogicDescriptor(Element rootElement) {
        this.rootElement = rootElement;
        processors = new ArrayList<>();
    }

    public void select(String selector, RenderFunction f) {
        Elements elements = rootElement.select(selector);
        elements.forEach(Element::selected);
        processors.add(new TemplateProcessor(elements, f));
    }

    public void root(RenderFunction f) {
        rootElement.selected();
        processors.add(new TemplateProcessor(null, f));
    }

    protected List<TemplateProcessor> getProcessors() {
        return processors;
    }
}
