package net.unit8.moshas.dom;

import net.unit8.moshas.helper.StringUtil;

/**
 *
 * @author kawasima
 */
public class DocumentType extends Node {
    private static final String NAME = "name";
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";


   /**
     * Create a new doctype element.
     * @param name the doctype's name
     * @param publicId the doctype's public ID
     * @param systemId the doctype's system ID
     * @param baseUri the doctype's base URI
     */
    public DocumentType(String name, String publicId, String systemId, String baseUri) {
        super(baseUri);

        attr(NAME, name);
        attr(PUBLIC_ID, publicId);
        attr(SYSTEM_ID, systemId);
    }

    @Override
    public String nodeName() {
        return "#doctype";
    }

    @Override
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (!has(PUBLIC_ID) && !has(SYSTEM_ID)) {
            // looks like a html5 doctype, go lowercase for aesthetics
            accum.append("<!doctype");
        } else {
            accum.append("<!DOCTYPE");
        }
        if (has(NAME))
            accum.append(" ").append(attributes().get(NAME));
        if (has(PUBLIC_ID))
            accum.append(" PUBLIC \"").append(attributes().get(PUBLIC_ID)).append('"');
        if (has(SYSTEM_ID))
            accum.append(" \"").append(attributes().get(SYSTEM_ID)).append('"');
        accum.append('>');
    }

    @Override
    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {}
    
    private boolean has(final String attribute) {
        return !StringUtil.isBlank(attributes().get(attribute));
    }
}
