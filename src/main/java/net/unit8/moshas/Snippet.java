package net.unit8.moshas;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import net.unit8.moshas.dom.TemplateElement;
import net.unit8.moshas.dom.TemplateNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author kawasima
 */
public class Snippet {
    private final List<TemplateProcessor> processors = new ArrayList<>();
    private TemplateElement targetElement;

    protected Snippet() {
        
    }
    protected Snippet(TemplateElement targetElement) {
        this.targetElement = targetElement;
    }
    
    public static Snippet define(TemplateElement el, TemplateDefinition def) {
        Snippet snippet = new Snippet(el);
        def.define(snippet);
        return snippet;
    }

    private List<TemplateElement> selectAll(String query) {
        Elements jels = ((Element) targetElement.getJsoupNode()).select(query);
        List<TemplateElement> selectedElements = new ArrayList<>();
        
        TemplateNode root = targetElement;
        TemplateNode node = targetElement;
        int depth = 0;
        
        while (node != null) {
            if (node instanceof TemplateElement && jels.contains(((TemplateElement) node).getJsoupNode())) {
                selectedElements.add((TemplateElement) node);
            }
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    node = node.parentNode();
                    depth--;
                }
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
        return selectedElements;
    }
    
    public TemplateElement select(String query) {
        return selectAll(query).get(0);
    }
        
    public void select(String selector, RenderFunction f) {
        processors.add(new TemplateProcessor(selectAll(selector), f));
    }

    public void root(RenderFunction f) {
        processors.add(new TemplateProcessor(null, f));
    }
    
    protected void setTargetElement(TemplateElement el) {
        this.targetElement = el;
    }
    
    public TemplateElement render(Context context) {
        final TemplateElement cloneElement = targetElement.clone();
        
        processors.forEach((processor) -> {
            processor.process(cloneElement, context);
        });
        return cloneElement;
    }
    
    public void render(Context context, OutputStream out) {
        try {
            out.write(render(context).toString().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void render(Context context, Writer writer) {
        try {
            writer.write(render(context).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
