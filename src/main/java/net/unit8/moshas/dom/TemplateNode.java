package net.unit8.moshas.dom;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

/**
 *
 * @author kawasima
 */
public abstract class TemplateNode implements Serializable {
    private static final List<TemplateNode> EMPTY_NODES = Collections.emptyList();
    protected TemplateNode parent;
    protected Node jsoupNode;
    
    protected Map<Integer, List<TemplateNode>> childNodes = new HashMap<>();
    protected Map<Integer, Attributes> attributes  = new HashMap<>();
    protected Map<Integer, String> outerHtmlHead = new HashMap<>();
    protected Map<Integer, String> outerHtmlTail = new HashMap<>();
    protected Map<Integer, String> renderedHtml = new HashMap<>();
    
    protected TemplateNode(Node jsoupNode, TemplateNode parent) {
        this.jsoupNode = jsoupNode;
        this.parent = parent;
        this.childNodes.put(0, EMPTY_NODES);
        this.attributes.put(0, jsoupNode.attributes());
    }
    
    public int childNodeSize() {
        List<TemplateNode> cs = childNodes.get(DocId.get());
        return (cs == null) ? childNodes.get(0).size() : cs.size();
    }
    
    public TemplateNode childNode(int index) {
        List<TemplateNode> cs = childNodes.get(DocId.get());
        return (cs == null) ? childNodes.get(0).get(index) : cs.get(index);
    }

    public List<TemplateNode> childNodes() {
        List<TemplateNode> cs = childNodes.get(DocId.get());
        return (cs == null) ? childNodes.get(0) : cs;
    }

    public Attributes attributes() {
        Attributes attrs = attributes.get(DocId.get());
        return (attrs == null) ? attributes.get(0) : attrs;
    }
    
    public void setAttr(String name, String value) {
        if (attributes.containsKey(DocId.get())) {
            attributes.get(DocId.get()).put(name, value);
        } else {
            Attributes newAttrs = attributes.get(0).clone();
            newAttrs.put(name, value);
            attributes.put(DocId.get(), newAttrs);
        }
    }

    public TemplateNode nextSibling() {
        if (parent == null)
            return null;
        
        List<TemplateNode> siblings = parent.childNodes();
        for (int i=0; i < siblings.size(); i++) {
            if (siblings.get(i) == this) {
                if (i < siblings.size() - 1) {
                    return siblings.get(i+1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    public TemplateNode parentNode() {
        return parent;
    }
    
    abstract void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out);
    abstract void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out);
    
    protected void indent(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append("\n").append(StringUtil.padding(depth * out.indentAmount()));
    }

    public Node getJsoupNode() {
        return jsoupNode;
    }
    
    @Override
    public String toString() {
        StringBuilder accum = new StringBuilder();
        Document.OutputSettings out = getOutputSettings();
        
        TemplateNode node = this;
        TemplateNode root = this;
        int depth = 0;
        
        while (node != null) {
            node.outerHtmlHead(accum, depth, out);
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    node.outerHtmlTail(accum, depth, out);
                    node = node.parentNode();
                    depth--;
                }
                node.outerHtmlTail(accum, depth, out);
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
        return accum.toString();
    }
    
    Document.OutputSettings getOutputSettings() {
        return jsoupNode.ownerDocument() != null ? jsoupNode.ownerDocument().outputSettings() : (new Document("")).outputSettings();
    }

    public void cleanThreadCache() {
        attributes.remove(DocId.get());
        childNodes.remove(DocId.get());
        outerHtmlHead.remove(DocId.get());
        outerHtmlTail.remove(DocId.get());
    }       
}
