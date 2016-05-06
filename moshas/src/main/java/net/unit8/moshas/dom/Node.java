package net.unit8.moshas.dom;

import net.unit8.moshas.helper.StringUtil;
import net.unit8.moshas.helper.Validate;
import net.unit8.moshas.select.NodeTraversor;
import net.unit8.moshas.select.NodeVisitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author kawasima
 */
public abstract class Node implements Serializable, Cloneable {
    private static final List<Node> EMPTY_NODES = Collections.emptyList();
    Node parentNode;
    Map<Integer, List<Node>> childNodes = new ConcurrentHashMap<>();
    Map<Integer, Attributes> attributes  = new ConcurrentHashMap<>();
    String baseUri;
    int siblingIndex;

    protected String outerHtmlHead;
    protected String outerHtmlTail;
    protected String renderedHtml;
    protected boolean needsRerender;

    protected Node(String baseUri, Attributes attributes) {
        Validate.notNull(baseUri);
        Validate.notNull(attributes);
        childNodes.put(0, EMPTY_NODES);
        baseUri = baseUri.trim();
        this.attributes.put(0, attributes);
        needsRerender = false;
    }

    protected Node(String baseUri) {
        this(baseUri, new Attributes());
    }

    protected Node() {
        this("");
    }

    public String baseUri() {
        return baseUri;
    }

    /**
     Get the node name of this node. Use for debugging purposes and not logic switching (for that, use instanceof).
     @return node name
     */
    public abstract String nodeName();


        /**
     * Get an attribute's value by its key.
     * <p>
     * To get an absolute URL from an attribute that may be a relative URL, prefix the key with <code><b>abs</b></code>,
     * which is a shortcut to the absUrl method.
     * </p>
     * E.g.:
     * <blockquote><code>String url = a.attr("abs:href");</code></blockquote>
     *
     * @param attributeKey The attribute key.
     * @return The attribute, or empty string if not present (to avoid nulls).
     * @see #attributes()
     * @see #hasAttr(String)
     */
    public String attr(String attributeKey) {
        Validate.notNull(attributeKey);

        Attributes attrs = attributes();
        if (attrs.hasKey(attributeKey))
            return attrs.get(attributeKey);
        else return "";
    }

    /**
     * Get all of the element's attributes.
     * @return attributes (which implements iterable, in same order as presented in original HTML).
     */
    public Attributes attributes() {
        return attributes.computeIfAbsent(RenderingId.get(), id -> attributes.get(0).clone());
    }

    /**
     * Set an attribute (key=value). If the attribute already exists, it is replaced.
     * @param attributeKey The attribute key.
     * @param attributeValue The attribute value.
     * @return this (for chaining)
     */
    public Node attr(String attributeKey, String attributeValue) {
        needsRerender = true;
        if (attributes.containsKey(RenderingId.get())) {
            attributes.get(RenderingId.get()).put(attributeKey, attributeValue);
        } else {
            Attributes newAttrs = attributes.get(0).clone();
            newAttrs.put(attributeKey, attributeValue);
            attributes.put(RenderingId.get(), newAttrs);
        }
        return this;
    }

    /**
     * Test if this element has an attribute.
     * @param attributeKey The attribute key to check.
     * @return true if the attribute exists, false if not.
     */
    public boolean hasAttr(String attributeKey) {
        return attributes().hasKey(attributeKey);
    }

    /**
     Get a child node by its 0-based index.
     @param index index of child node
     @return the child node at this index. Throws a {@code IndexOutOfBoundsException} if the index is out of bounds.
     */
    public Node childNode(int index) {
        List<Node> cs = childNodes.get(RenderingId.get());
        return (cs == null) ? childNodes.get(0).get(index) : cs.get(index);
    }

    /**
     Get this node's children. Presented as an unmodifiable list: new children can not be added, but the child nodes
     themselves can be manipulated.
     @return list of children. If no children, returns an empty list.
     */
    public List<Node> childNodes() {
        List<Node> cs = childNodes.get(RenderingId.get());
        return (cs == null) ? childNodes.get(0) : cs;
    }

    /**
     * Get the number of child nodes that this node holds.
     * @return the number of child nodes that this node holds.
     */
    public int childNodeSize() {
        List<Node> cs = childNodes.get(RenderingId.get());
        return (cs == null) ? childNodes.get(0).size() : cs.size();
    }

    /**
     Gets this node's parent node.
     @return parent node; or null if no parent.
     */
    public Node parent() {
        return parentNode;
    }

    public Node parentNode() {
        return parentNode;
    }

    protected void setParentNode(Node parentNode) {
        /*
        if (this.parentNode != null)
            this.parentNode.removeChild(this);
        */
        this.parentNode = parentNode;
    }

    /**
     * Gets the Document associated with this Node.
     * @return the Document associated with this Node, or null if there is no such Document.
     */
    public Document ownerDocument() {
        if (this instanceof Document)
            return (Document) this;
        else if (parentNode == null)
            return null;
        else
            return parentNode.ownerDocument();
    }

    /**
     * Remove (delete) this node from the DOM tree. If this node has children, they are also removed.
     */
    public void remove() {
        Validate.notNull(parentNode);
        parentNode.removeChild(this);
    }

    /**
     * Insert the specified node into the DOM before this node (i.e. as a preceding sibling).
     * @param node to add before this node
     * @return this node, for chaining
     */
    public Node before(Node node) {
        Validate.notNull(node);
        Validate.notNull(parentNode);

        parentNode.addChildren(siblingIndex, node);
        return this;
    }

    protected void addChildren(int index, Node... children) {
        Validate.noNullElements(children);
        for (int i = children.length - 1; i >= 0; i--) {
            Node in = children[i];
            reparentChild(in);
            ensureChildNodes();
            childNodes().add(index, in);
        }
        reindexChildren(index);
    }

    protected void removeChild(Node out) {
        Validate.isTrue(out.parentNode == this);

        ensureChildNodes();
        final int index = out.siblingIndex;
        childNodes().remove(index);
        reindexChildren(index);
    }

    protected void ensureChildNodes() {
        Integer id = RenderingId.get();
        List<Node> cns = childNodes.get(id);
        if (cns == EMPTY_NODES || cns == null) {
            childNodes.put(id, new ArrayList<>(childNodes.get(0)));
        }
    }

    protected void reparentChild(Node child) {
        /*
        if (child.parentNode != null)
            child.parentNode.removeChild(child);
        */
        child.setParentNode(this);
    }

    private void reindexChildren(int start) {
        for (int i = start; i < childNodes().size(); i++) {
            childNodes().get(i).setSiblingIndex(i);
        }
    }

    /**
     Get this node's next sibling.
     @return next sibling, or null if this is the last sibling
     */
    public Node nextSibling() {
        if (parentNode == null)
            return null; // root

        final List<Node> siblings = parentNode.childNodes();
        final int index = siblingIndex+1;
        if (siblings.size() > index)
            return siblings.get(index);
        else
            return null;

        /*
        List<Node> siblings = parentNode.childNodes();
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
        */
    }

    /**
     * Get the list index of this node in its node sibling list. I.e. if this is the first node
     * sibling, returns 0.
     * @return position in node sibling list
     */
    public int siblingIndex() {
        return siblingIndex;
    }

    protected void setSiblingIndex(int siblingIndex) {
        this.siblingIndex = siblingIndex;
    }

   /**
     * Perform a depth-first traversal through this node and its descendants.
     * @param nodeVisitor the visitor callbacks to perform on each node
     * @return this node, for chaining
     */
    public Node traverse(NodeVisitor nodeVisitor) {
        Validate.notNull(nodeVisitor);
        NodeTraversor traversor = new NodeTraversor(nodeVisitor);
        traversor.traverse(this);
        return this;
    }

    /**
     Get the outer HTML of this node.
     @return HTML
     */
    public String outerHtml() {
        StringBuilder accum = new StringBuilder(128);
        outerHtml(accum);
        return accum.toString();
    }

    protected void outerHtml(StringBuilder accum) {
        new NodeTraversor(new OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
    }

    // if this node has no document (or parent), retrieve the default output settings
    Document.OutputSettings getOutputSettings() {
        return ownerDocument() != null ? ownerDocument().outputSettings() : (new Document("")).outputSettings();
    }

   @Override
    public String toString() {
        return outerHtml();
    }

    public abstract void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out);
    public abstract void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out);

    protected void indent(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append("\n").append(StringUtil.padding(depth * out.indentAmount()));
    }

    public void cleanThreadCache(int renderingId) {
        attributes.remove(renderingId);
        childNodes.remove(renderingId);
    }

    private static class OuterHtmlVisitor implements NodeVisitor {
        private final StringBuilder accum;
        private final Document.OutputSettings out;

        OuterHtmlVisitor(StringBuilder accum, Document.OutputSettings out) {
            this.accum = accum;
            this.out = out;
        }

        @Override
        public void head(Node node, int depth) {
            node.outerHtmlHead(accum, depth, out);
        }

        @Override
        public void tail(Node node, int depth) {
            if (!node.nodeName().equals("#text")) // saves a void hit.
                node.outerHtmlTail(accum, depth, out);
        }
    }

    @Override
    public Node clone() {
        Node clone;
        try {
            clone = (Node) super.clone();
            clone.renderedHtml = null;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return clone;
    }
}
