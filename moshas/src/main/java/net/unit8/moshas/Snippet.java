package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.dom.Element;
import net.unit8.moshas.dom.RenderingId;
import net.unit8.moshas.dom.SlotManager;
import net.unit8.moshas.select.Elements;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kawasima
 */
public class Snippet implements Serializable {
    private final List<TemplateProcessor> processors = new ArrayList<>();
    private Element rootElement;

    protected Snippet() {

    }
    protected Snippet(Element rootElement) {
        this.rootElement = rootElement;
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

    protected void setRootElement(Element el) {
        this.rootElement = el;
    }

    protected Element getRootElement() {
        return rootElement;
    }

    public Element render(Context context) {
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

    public void render(Context context, OutputStream out) {
        try {
            out.write(render(context).cachedHtml().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(Context context, Writer writer) {
        try {
            writer.write(render(context).cachedHtml());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
