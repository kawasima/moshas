package net.unit8.moshas;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author kawasima
 */
public class Snippet {
    private final List<TemplateProcessor> processors = new ArrayList<>();
    private Element targetElement;

    protected Snippet() {
        
    }
    protected Snippet(Element targetElement) {
        this.targetElement = targetElement;
    }
    
    public static Snippet define(Element el, TemplateDefinition def) {
        Snippet snippet = new Snippet(el);
        def.define(snippet);
        return snippet;
    }
    
    public Element select(String query) {
        return targetElement.select(query).first();
    }
        
    public void select(String selector, RenderFunction f) {
        processors.add(new TemplateProcessor(selector, f));
    }

    public void root(RenderFunction f) {
        processors.add(new TemplateProcessor(null, f));
    }
    
    public void selectSnippet(String selector, Snippet s) {
        
    }
    
    protected void setTargetElement(Element el) {
        this.targetElement = el;
    }
    
    public Element render(Context context) {
        Element cloneElement = targetElement.clone();
        if (cloneElement instanceof Document) {
            ((Document) cloneElement).outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        }
        processors.forEach((processor) -> {
            processor.process(cloneElement, context);
        });
        return cloneElement;
    }
    
    public void render(Context context, OutputStream out) {
        Element rendered = render(context);
        try {
            out.write(rendered.html().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void render(Context context, Writer writer) {
        Element rendered = render(context);
        try {
            writer.write(rendered.html());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
