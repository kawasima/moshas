package net.unit8.moshas;

import net.unit8.moshas.context.IContext;
import net.unit8.moshas.dom.Element;
import net.unit8.moshas.dom.RenderingId;
import net.unit8.moshas.dom.SlotManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kawasima
 */
public class DefaultSnippet implements Snippet, DescribableTemplate {
    private final List<TemplateProcessor> processors = new ArrayList<>();
    private Element rootElement;

    protected DefaultSnippet() {

    }
    protected DefaultSnippet(Element rootElement) {
        this.rootElement = rootElement;
    }

    protected void setRootElement(Element el) {
        this.rootElement = el;
    }

    @Override
    public Element getRootElement() {
        return rootElement;
    }

    @Override
    public Element render(IContext context) {
        final Element cloneElement = rootElement.clone();

        int id = RenderingId.push();
        try {
            processors.forEach((processor) -> processor.process(cloneElement, context));
            cloneElement.cachedHtml();
            cloneElement.renderedHtml();
            return cloneElement;
        } finally {
            int nowId = RenderingId.pop();
            SlotManager.clear(nowId);
        }
    }

    @Override
    public void setProcessors(List<TemplateProcessor> processors) {
        this.processors.clear();
        this.processors.addAll(processors);
    }
}
