package net.unit8.moshas.select;

import net.unit8.moshas.dom.Node;

/**
 *
 * @author kawasima
 */
public interface NodeVisitor {
    /**
     * Callback for when a node is first visited.
     *
     * @param node the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node
     * of that will have depth 1.
     */
    void head(Node node, int depth);

    /**
     * Callback for when a node is last visited, after all of its descendants have been visited.
     *
     * @param node the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node
     * of that will have depth 1.
     */
    void tail(Node node, int depth);
}
