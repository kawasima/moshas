package net.unit8.moshas.dom;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

/**
 *
 * @author kawasima
 */
public class TemplateElement extends TemplateNode implements Cloneable {
    public TemplateElement(Element element, TemplateNode parent) {
        super(element, parent);
        this.childNodes.put(0, new ArrayList<>(jsoupNode.childNodeSize()));
    }
    
    public void appendChild(TemplateNode childNode, int docId) {
        List<TemplateNode> cns = childNodes.get(docId);
        if (cns == null) {
            cns = new ArrayList<>(childNodes.get(0));
            childNodes.put(docId, cns);
        }
        cns.add(childNode);
        if (docId != 0) {
            System.out.println("appendChild=" + cns);
        }
    }
    
    public void appendChild(TemplateNode childNode) {
        appendChild(childNode, DocId.get());
    }

    public void text(String text) {
        List<TemplateNode> cns = new ArrayList<>();
        childNodes.put(DocId.get(), cns);
        TextNode textNode = new TextNode(text, jsoupNode.baseUri());
        cns.add(new TemplateTextNode(textNode, this));
    }

    public void empty() {
        List<TemplateNode> cns = childNodes.get(DocId.get());
        if (cns == null) {
            cns = new ArrayList<>(childNodes.get(0));
            childNodes.put(DocId.get(), cns);
        }
        cns.clear();
    }
    
    @Override
    void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (accum.length() > 0 && out.prettyPrint() && (((Element) jsoupNode).tag().formatAsBlock() || (jsoupNode.parent() != null && ((Element) jsoupNode).parent().tag().formatAsBlock()) || out.outline()) )
            indent(accum, depth, out);
        accum.append("<").append(((Element) jsoupNode).tagName());
                if (attributes == null)
            return;
        
        accum.append(((Element) jsoupNode).attributes().toString());

        // selfclosing includes unknown tags, isEmpty defines tags that are always empty
        if (childNodes().isEmpty() && ((Element) jsoupNode).tag().isSelfClosing()) {
            if (out.syntax() == Document.OutputSettings.Syntax.html && ((Element) jsoupNode).tag().isEmpty())
                accum.append('>');
            else
                accum.append(" />"); // <img> in html, <img /> in xml
        }
        else
            accum.append(">");
    }

    @Override
    void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (!(childNodes().isEmpty() && ((Element) jsoupNode).tag().isSelfClosing())) {
            if (out.prettyPrint() && (!childNodes().isEmpty() && (
                    ((Element) jsoupNode).tag().formatAsBlock() || (out.outline() && (childNodes().size()>1 || (childNodes().size()==1 && !(childNodes().get(0) instanceof TemplateTextNode))))
            )))
                indent(accum, depth, out);
            accum.append("</").append(((Element) jsoupNode).tagName()).append(">");
        }
    }
 
    @Override
    public TemplateElement clone() {
        try {
            TemplateElement templateElement = (TemplateElement) super.clone();
            return templateElement;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
