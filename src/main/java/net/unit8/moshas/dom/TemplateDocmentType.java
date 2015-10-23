package net.unit8.moshas.dom;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;

/**
 *
 * @author kawasima
 */
public class TemplateDocmentType extends TemplateNode {
    private static final String NAME = "name";
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";

    public TemplateDocmentType(DocumentType doctype, TemplateNode node) {
        super(doctype, node);
    }

    @Override
    void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (out.syntax() == Document.OutputSettings.Syntax.html && !has(PUBLIC_ID) && !has(SYSTEM_ID)) {
            // looks like a html5 doctype, go lowercase for aesthetics
            accum.append("<!doctype");
        } else {
            accum.append("<!DOCTYPE");
        }
        if (has(NAME))
            accum.append(" ").append(jsoupNode.attr(NAME));
        if (has(PUBLIC_ID))
            accum.append(" PUBLIC \"").append(jsoupNode.attr(PUBLIC_ID)).append('"');
        if (has(SYSTEM_ID))
            accum.append(" \"").append(jsoupNode.attr(SYSTEM_ID)).append('"');
        accum.append('>');
    }

    @Override
    void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {}
    
    private boolean has(final String attribute) {
        return !StringUtil.isBlank(jsoupNode.attr(attribute));
    }
}
