package net.unit8.moshas.select;

import net.unit8.moshas.dom.Node;

/**
 *
 * @author kawasima
 */
public class SkippableNodeTraversor {
    private final SkippableNodeVisitor visitor;
    
    public SkippableNodeTraversor(SkippableNodeVisitor visitor) {
        this.visitor = visitor;
    }
    /**
     * Start a depth-first traverse of the root and all of its descendants.
     * @param root the root node point to traverse.
     */
    public void traverse(Node root) {
        Node node = root;
        int depth = 0;
        
        while (node != null) {
            boolean skip = visitor.head(node, depth);
            if (!skip && node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    visitor.tail(node, depth);
                    node = node.parentNode();
                    depth--;
                }
                visitor.tail(node, depth);
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
    }    
    
}
