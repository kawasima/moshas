package net.unit8.moshas.dom;

import net.unit8.moshas.select.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.unit8.moshas.helper.StringUtil;
import net.unit8.moshas.helper.Validate;
import net.unit8.moshas.parser.Tag;
import net.unit8.moshas.select.Collector;
import net.unit8.moshas.select.Evaluator;
import net.unit8.moshas.select.NodeTraversor;
import net.unit8.moshas.select.NodeVisitor;
import net.unit8.moshas.select.Selector;
import net.unit8.moshas.select.SkippableNodeTraversor;
import net.unit8.moshas.select.SkippableNodeVisitor;

/**
 *
 * @author kawasima
 */
public class Element extends Node implements Cloneable {
    private Tag tag;
    private boolean selectedForRendering = false;
    
    private static final Pattern classSplit = Pattern.compile("\\s+");

        /**
     * Create a new, standalone Element. (Standalone in that is has no parent.)
     * 
     * @param tag tag of this element
     * @param baseUri the base URI
     * @param attributes initial attributes
     * @see #appendChild(Node)
     * @see #appendElement(String)
     */
    public Element(Tag tag, String baseUri, Attributes attributes) {
        super(baseUri, attributes);
        
        Validate.notNull(tag);    
        this.tag = tag;
    }

    /**
     * Create a new Element from a tag and a base URI.
     * 
     * @param tag element tag
     * @param baseUri the base URI of this element. It is acceptable for the base URI to be an empty
     *            string, but not null.
     * @see Tag#valueOf(String)
     */
    public Element(Tag tag, String baseUri) {
        this(tag, baseUri, new Attributes());
    }

    @Override
    public String nodeName() {
        return tag.getName();
    }

    /**
     * Get the Tag for this element.
     * 
     * @return the tag object
     */
    public Tag tag() {
        return tag;
    }
    
    public String tagName() {
        return tag.getName();
    }
    
    /**
     * Test if this element is a block-level element. (E.g. {@code <div> == true} or an inline element
     * {@code <p> == false}).
     * 
     * @return true if block, false if not (and thus inline)
     */
    public boolean isBlock() {
        return tag.isBlock();
    }

    /**
     * Get the {@code id} attribute of this element.
     * 
     * @return The id attribute, if present, or an empty string if not.
     */
    public String id() {
        return attributes().get("id");
    }

    @Override
    public Element parent() {
        return (Element) parentNode;
    }
    /**
     * Get a child element of this element, by its 0-based index number.
     * <p>
     * Note that an element can have both mixed Nodes and Elements as children. This method inspects
     * a filtered list of children that are elements, and the index is based on that filtered list.
     * </p>
     * 
     * @param index the index number of the element to retrieve
     * @return the child element, if it exists, otherwise throws an {@code IndexOutOfBoundsException}
     * @see #childNode(int)
     */
    public Element child(int index) {
        return children().get(index);
    }
   
    /**
     * Get this element's child elements.
     * <p>
     * This is effectively a filter on {@link #childNodes()} to get Element nodes.
     * </p>
     * @return child elements. If this element has no children, returns an
     * empty list.
     * @see #childNodes()
     */
    public Elements children() {
        // create on the fly rather than maintaining two lists. if gets slow, memoize, and mark dirty on change
        List<Element> elements = new ArrayList<Element>(childNodes().size());
        for (Node node : childNodes()) {
            if (node instanceof Element)
                elements.add((Element) node);
        }
        return new Elements(elements);
    }

    /**
     * Find elements that match the {@link Selector} CSS query, with this element as the starting context. Matched elements
     * may include this element, or any of its children.
     * <p>
     * This method is generally more powerful to use than the DOM-type {@code getElementBy*} methods, because
     * multiple filters can be combined, e.g.:
     * </p>
     * <ul>
     * <li>{@code el.select("a[href]")} - finds links ({@code a} tags with {@code href} attributes)
     * <li>{@code el.select("a[href*=example.com]")} - finds links pointing to example.com (loosely)
     * </ul>
     * <p>
     * See the query syntax documentation in {@link org.jsoup.select.Selector}.
     * </p>
     * 
     * @param cssQuery a {@link Selector} CSS-like query
     * @return elements that match the query (empty if none match)
     * @see org.jsoup.select.Selector
     * @throws Selector.SelectorParseException (unchecked) on an invalid CSS query.
     */
    public Elements select(String cssQuery) {
        return Selector.select(cssQuery, this);
    }

    /**
     * Find all elements under this element (including self, and children of children).
     * 
     * @return all elements
     */
    public Elements getAllElements() {
        return Collector.collect(new Evaluator.AllElements(), this);
    }

    /**
     * Get sibling elements. If the element has no sibling elements, returns an empty list. An element is not a sibling
     * of itself, so will not be included in the returned list.
     * @return sibling elements
     */
    public Elements siblingElements() {
        if (parentNode == null)
            return new Elements(0);

        List<Element> elements = parent().children();
        Elements siblings = new Elements(elements.size() - 1);
        for (Element el: elements)
            if (el != this)
                siblings.add(el);
        return siblings;
    }

    /**
     * Gets the next sibling element of this element. E.g., if a {@code div} contains two {@code p}s, 
     * the {@code nextElementSibling} of the first {@code p} is the second {@code p}.
     * <p>
     * This is similar to {@link #nextSibling()}, but specifically finds only Elements
     * </p>
     * @return the next element, or null if there is no next element
     * @see #previousElementSibling()
     */
    public Element nextElementSibling() {
        if (parentNode == null) return null;
        List<Element> siblings = parent().children();
        Integer index = indexInList(this, siblings);
        Validate.notNull(index);
        if (siblings.size() > index+1)
            return siblings.get(index+1);
        else
            return null;
    }

    /**
     * Gets the previous element sibling of this element.
     * @return the previous element, or null if there is no previous element
     * @see #nextElementSibling()
     */
    public Element previousElementSibling() {
        if (parentNode == null) return null;
        List<Element> siblings = parent().children();
        Integer index = indexInList(this, siblings);
        Validate.notNull(index);
        if (index > 0)
            return siblings.get(index-1);
        else
            return null;
    }

        
    /**
     * Get the list index of this element in its element sibling list. I.e. if this is the first element
     * sibling, returns 0.
     * @return position in element sibling list
     */
    public Integer elementSiblingIndex() {
       if (parent() == null) return 0;
       return indexInList(this, parent().children()); 
    }

    private static <E extends Element> Integer indexInList(Element search, List<E> elements) {
        Validate.notNull(search);
        Validate.notNull(elements);

        for (int i = 0; i < elements.size(); i++) {
            E element = elements.get(i);
            if (element == search)
                return i;
        }
        return null;
    }

    /**
     * Add a node child node to this element.
     * 
     * @param child node to add.
     * @return this element, so that you can add more child nodes or elements.
     */
    public Element appendChild(Node child) {
        reparentChild(child);
        ensureChildNodes();
        childNodes().add(child);
        child.setSiblingIndex(childNodes().size() - 1);

        return this;
    }
    
    /**
     * Gets the literal value of this element's "class" attribute, which may include multiple class names, space
     * separated. (E.g. on <code>&lt;div class="header gray"&gt;</code> returns, "<code>header gray</code>")
     * @return The literal class attribute, or <b>empty string</b> if no class attribute set.
     */
    public String className() {
        return attr("class").trim();
    }

    /**
     * Get all of the element's class names. E.g. on element {@code <div class="header gray">},
     * returns a set of two elements {@code "header", "gray"}. Note that modifications to this set are not pushed to
     * the backing {@code class} attribute; use the {@link #classNames(java.util.Set)} method to persist them.
     * @return set of classnames, empty if no class attribute
     */
    public Set<String> classNames() {
    	String[] names = classSplit.split(className());
    	Set<String> classNames = new LinkedHashSet<String>(Arrays.asList(names));
    	classNames.remove(""); // if classNames() was empty, would include an empty class

        return classNames;
    }

    /**
     Set the element's {@code class} attribute to the supplied class names.
     @param classNames set of classes
     @return this element, for chaining
     */
    public Element classNames(Set<String> classNames) {
        Validate.notNull(classNames);
        attributes().put("class", StringUtil.join(classNames, " "));
        return this;
    }
    
    /**
     * Tests if this element has a class. Case insensitive.
     * @param className name of class to check for
     * @return true if it does, false if not
     */
    /*
    Used by common .class selector, so perf tweaked to reduce object creation vs hitting classnames().

    Wiki: 71, 13 (5.4x)
    CNN: 227, 91 (2.5x)
    Alterslash: 59, 4 (14.8x)
    Jsoup: 14, 1 (14x)
    */
    public boolean hasClass(String className) {
        String classAttr = attributes().get("class");
        if (classAttr.equals("") || classAttr.length() < className.length())
            return false;

        final String[] classes = classSplit.split(classAttr);
        for (String name : classes) {
            if (className.equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    /**
     Add a class name to this element's {@code class} attribute.
     @param className class name to add
     @return this element
     */
    public Element addClass(String className) {
        Validate.notNull(className);

        Set<String> classes = classNames();
        classes.add(className);
        classNames(classes);

        return this;
    }

    /**
     Remove a class name from this element's {@code class} attribute.
     @param className class name to remove
     @return this element
     */
    public Element removeClass(String className) {
        Validate.notNull(className);

        Set<String> classes = classNames();
        classes.remove(className);
        classNames(classes);

        return this;
    }
    
    /*--------------------------------------Text-------------------------------*/
    
    /**
     * Gets the combined text of this element and all its children. Whitespace is normalized and trimmed.
     * <p>
     * For example, given HTML {@code <p>Hello  <b>there</b> now! </p>}, {@code p.text()} returns {@code "Hello there now!"}
     *
     * @return unencoded text, or empty string if none.
     * @see #ownText()
     * @see #textNodes()
     */
    public String text() {
        final StringBuilder accum = new StringBuilder();
        new NodeTraversor(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode) node;
                    appendNormalisedText(accum, textNode);
                } else if (node instanceof Element) {
                    Element element = (Element) node;
                    if (accum.length() > 0 &&
                        (element.isBlock() || element.tag.getName().equals("br")) &&
                        !TextNode.lastCharIsWhitespace(accum))
                        accum.append(" ");
                }
            }

            public void tail(Node node, int depth) {
            }
        }).traverse(this);
        return accum.toString().trim();
    }

    /**
     * Gets the text owned by this element only; does not get the combined text of all children.
     * <p>
     * For example, given HTML {@code <p>Hello <b>there</b> now!</p>}, {@code p.ownText()} returns {@code "Hello now!"},
     * whereas {@code p.text()} returns {@code "Hello there now!"}.
     * Note that the text within the {@code b} element is not returned, as it is not a direct child of the {@code p} element.
     *
     * @return unencoded text, or empty string if none.
     * @see #text()
     * @see #textNodes()
     */
    public String ownText() {
        StringBuilder sb = new StringBuilder();
        ownText(sb);
        return sb.toString().trim();
    }

    private void ownText(StringBuilder accum) {
        for (Node child : childNodes()) {
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode) child;
                appendNormalisedText(accum, textNode);
            } else if (child instanceof Element) {
                appendWhitespaceIfBr((Element) child, accum);
            }
        }
    }

    private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
        String text = textNode.getWholeText();

        if (preserveWhitespace(textNode.parentNode))
            accum.append(text);
        else
            StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
    }

    private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
        if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum))
            accum.append(" ");
    }

    static boolean preserveWhitespace(Node node) {
        // looks only at this element and one level up, to prevent recursion & needless stack searches
        if (node != null && node instanceof Element) {
            Element element = (Element) node;
            return element.tag.preserveWhitespace() ||
                element.parent() != null && element.parent().tag.preserveWhitespace();
        }
        return false;
    }

    
    public Element text(String text) {
        empty();
        TextNode textNode = new TextNode(text, "");
        appendChild(textNode);
        SlotManager.add(this);
        return this;
    }

    public Element empty() {
        ensureChildNodes();
        childNodes().clear();
        SlotManager.add(this);
        return this;
    }
    
    @Override
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (accum.length() > 0 && out.prettyPrint() && tag.formatAsBlock() || (parent() != null && (parent().tag().formatAsBlock())))
            indent(accum, depth, out);
        
        accum.append("<")
                .append(tagName());
        attributes().html(accum, out);
        // selfclosing includes unknown tags, isEmpty defines tags that are always empty
        if (childNodes().isEmpty() && tag.isSelfClosing()) {
            accum.append(" />"); // <img> in html, <img /> in xml
        }
        else
            accum.append(">");
    }

    @Override
    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
        if (!(childNodes().isEmpty() && tag.isSelfClosing())) {
            /*
            if (out.prettyPrint() && (!childNodes().isEmpty() && (
                    ((Element) jsoupNode).tag().formatAsBlock() || (out.outline() && (childNodes().size()>1 || (childNodes().size()==1 && !(childNodes().get(0) instanceof TemplateTextNode))))
                    )))
                accum.append(indent(depth, out));
            */
            accum.append("</").append(tagName()).append(">");
        }
    }
    
    public String cachedHtml() {
        if (renderedHtml == null) {
            final Document.OutputSettings out = getOutputSettings();
            final StringBuilder accum = new StringBuilder(4096);
            for (Node node : childNodes()) {
                new SkippableNodeTraversor(new SkippableNodeVisitor() {
                    @Override
                    public boolean head(Node node, int depth) {
                        if (node.renderedHtml == null) {
                            node.outerHtmlHead(accum, depth, out);
                        } else {
                            accum.append(node.renderedHtml);
                        }
                        return node.renderedHtml != null;
                    }
                    
                    @Override
                    public boolean tail(Node node, int depth) {
                        if (!node.nodeName().equals("#text") && node.renderedHtml == null) // saves a void hit.
                            node.outerHtmlTail(accum, depth, out);
                        return true;
                    }
                }).traverse(node);
            }
            renderedHtml = accum.toString();
        }
        return renderedHtml;
    }
    
    public String renderedHtml() {
        return renderedHtml;
    }
    /**
     * Retrieves the element's inner HTML. E.g. on a {@code <div>} with one empty {@code <p>}, would return
     * {@code <p></p>}. (Whereas {@link #outerHtml()} would return {@code <div><p></p></div>}.)
     * 
     * @return String of HTML.
     * @see #outerHtml()
     */
    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum);
        return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
    }

    private void html(StringBuilder accum) {
        for (Node node : childNodes())
            node.outerHtml(accum);
    }

    public void selected() {
        selectedForRendering = true;
    }

    @Override
    public Element clone() {
        return (Element) super.clone();
    }
}
