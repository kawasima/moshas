package net.unit8.moshas.dom;

/**
 *
 * @author kawasima
 */
public class DataNode extends Node {
    private final String data;
    public DataNode(String data, String baseUri) {
        super(baseUri);
        this.data = data;
    }

    @Override
    public String nodeName() {
        return "#data";
    }
    
    @Override
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append(data);
    }

    @Override
    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {}    
}
