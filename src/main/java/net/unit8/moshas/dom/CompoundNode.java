package net.unit8.moshas.dom;

/**
 *
 * @author kawasima
 */
public class CompoundNode extends Node {
    private final String encodedHtmlHead;
    private final String encodedHtmlTail;

    public CompoundNode(String encodedHtmlHead, String encodedHtmlTail) {
        super("");
        this.encodedHtmlHead = encodedHtmlHead;
        this.encodedHtmlTail = encodedHtmlTail;
    }
    
    @Override
    public String nodeName() {
        return "#compound";
    }

    @Override
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append(encodedHtmlHead);
    }

    @Override
    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append(encodedHtmlTail);
    }
    
}
