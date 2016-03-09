package net.unit8.moshas.dom;

/**
 *
 * @author kawasima
 */
public class Comment extends Node {
    private final String comment;

    /**
     Create a new comment node.
     @param comment The contents of the comment
     @param baseUri base URI
     */
    public Comment(String comment, String baseUri) {
        super(baseUri);
        this.comment = comment;
    }

    @Override
    public String nodeName() {
        return "#comment";
    }

    public String getData() {
        return comment;
    }
    
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (out.prettyPrint())
            indent(accum, depth, out);
        accum
                .append("<!--")
                .append(comment)
                .append("-->");
    }

    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {}
}
