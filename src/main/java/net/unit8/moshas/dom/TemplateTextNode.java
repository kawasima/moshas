package net.unit8.moshas.dom;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

/**
 *
 * @author kawasima
 */
public class TemplateTextNode extends TemplateNode {
    public TemplateTextNode(TextNode textNode, TemplateNode parent) {
        super(textNode, parent);
    }

    @Override
    void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append(((TextNode) jsoupNode).getWholeText());
    }

    @Override
    void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
    }
}
