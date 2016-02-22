package net.unit8.moshas.dom;

import net.unit8.moshas.helper.Validate;
import net.unit8.moshas.parser.Tag;
import net.unit8.moshas.select.Collector;
import net.unit8.moshas.select.Elements;
import net.unit8.moshas.select.Evaluator;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kawasima
 */
public class Document extends Element{
    private OutputSettings outputSettings = new OutputSettings();
    private QuirksMode quirksMode = QuirksMode.noQuirks;
    private final String location;
    private boolean updateMetaCharset = false;

    /**
     Create a new, empty Document.
     @param baseUri base URI of document
     @see org.jsoup.Jsoup#parse
     @see #createShell
     */
    public Document(String baseUri) {
        super(Tag.valueOf("#root"), baseUri);
        this.location = baseUri;
    }

    /**
     Create a new Element, with this document's base uri. Does not make the new element a child of this document.
     @param tagName element tag name (e.g. {@code a})
     @return new element
     */
    public Element createElement(String tagName) {
        return new Element(Tag.valueOf(tagName), this.baseUri());
    }

    /**
     Normalise the document. This happens after the parse phase so generally does not need to be called.
     Moves any text content that is not in the body element into the body.
     @return this document after normalisation
     */
    public Document normalise() {
        Element htmlEl = findFirstElementByTagName("html", this);
        normaliseStructure("head", htmlEl);
        normaliseStructure("body", htmlEl);

        return this;
    }

    // merge multiple <head> or <body> contents into one, delete the remainder, and ensure they are owned by <html>
    private void normaliseStructure(String tag, Element htmlEl) {
        Elements elements = Collector.collect(new Evaluator.Tag(tag.toLowerCase().trim()), this);

        Element master = elements.first(); // will always be available as created above if not existent
        if (elements.size() > 1) { // dupes, move contents to master
            List<Node> toMove = new ArrayList<Node>();
            for (int i = 1; i < elements.size(); i++) {
                Node dupe = elements.get(i);
                for (Node node : dupe.childNodes())
                    toMove.add(node);
                dupe.remove();
            }

            for (Node dupe : toMove)
                master.appendChild(dupe);
        }
        // ensure parented by <html>
        if (!master.parent().equals(htmlEl)) {
            htmlEl.appendChild(master); // includes remove()
        }
    }

    // fast method to get first by tag name, used for html, head, body finders
    private Element findFirstElementByTagName(String tag, Node node) {
        if (node.nodeName().equals(tag))
            return (Element) node;
        else {
            for (Node child: node.childNodes()) {
                Element found = findFirstElementByTagName(tag, child);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    @Override
    public String outerHtml() {
        return super.html(); // no outer wrapper tag
    }

    @Override
    public String nodeName() {
        return "#document";
    }

    /**
     * Sets the charset used in this document. This method is equivalent
     * to {@link OutputSettings#charset(java.nio.charset.Charset)
     * OutputSettings.charset(Charset)} but in addition it updates the
     * charset / encoding element within the document.
     *
     * <p>This enables
     * {@link #updateMetaCharsetElement(boolean) meta charset update}.</p>
     *
     * <p>If there's no element with charset / encoding information yet it will
     * be created. Obsolete charset / encoding definitions are removed!</p>
     *
     * <p><b>Elements used:</b></p>
     *
     * <ul>
     * <li><b>Html:</b> <i>&lt;meta charset="CHARSET"&gt;</i></li>
     * <li><b>Xml:</b> <i>&lt;?xml version="1.0" encoding="CHARSET"&gt;</i></li>
     * </ul>
     *
     * @param charset Charset
     *
     * @see #updateMetaCharsetElement(boolean)
     * @see OutputSettings#charset(java.nio.charset.Charset)
     */
    public void charset(Charset charset) {
        updateMetaCharsetElement(true);
        outputSettings.charset(charset);
    }

    /**
     * Returns the charset used in this document. This method is equivalent
     * to {@link OutputSettings#charset()}.
     *
     * @return Current Charset
     *
     * @see OutputSettings#charset()
     */
    public Charset charset() {
        return outputSettings.charset();
    }

    /**
     * Sets whether the element with charset information in this document is
     * updated on changes through {@link #charset(java.nio.charset.Charset)
     * Document.charset(Charset)} or not.
     *
     * <p>If set to <tt>false</tt> <i>(default)</i> there are no elements
     * modified.</p>
     *
     * @param update If <tt>true</tt> the element updated on charset
     * changes, <tt>false</tt> if not
     *
     * @see #charset(java.nio.charset.Charset)
     */
    public void updateMetaCharsetElement(boolean update) {
        this.updateMetaCharset = update;
    }

    /**
     * Returns whether the element with charset information in this document is
     * updated on changes through {@link #charset(java.nio.charset.Charset)
     * Document.charset(Charset)} or not.
     *
     * @return Returns <tt>true</tt> if the element is updated on charset
     * changes, <tt>false</tt> if not
     */
    public boolean updateMetaCharsetElement() {
        return updateMetaCharset;
    }

    @Override
    public Document clone() {
        Document clone = (Document) super.clone();
        clone.outputSettings = this.outputSettings.clone();
        return clone;
    }

    /**
     * A Document's output settings control the form of the text() and html() methods.
     */
    public static class OutputSettings implements Serializable, Cloneable {
        /**
         * The output serialization syntax.
         */
        public enum Syntax {html, xml}

        private Entities.EscapeMode escapeMode = Entities.EscapeMode.base;
        private String charsetName = "UTF-8";
        private boolean prettyPrint = true;
        private boolean outline = false;
        private int indentAmount = 1;
        private Syntax syntax = Syntax.html;

        public OutputSettings() {}

        /**
         * Get the document's current HTML escape mode: <code>base</code>, which provides a limited set of named HTML
         * entities and escapes other characters as numbered entities for maximum compatibility; or <code>extended</code>,
         * which uses the complete set of HTML named entities.
         * <p>
         * The default escape mode is <code>base</code>.
         * @return the document's current escape mode
         */
        public Entities.EscapeMode escapeMode() {
            return escapeMode;
        }

        /**
         * Set the document's escape mode, which determines how characters are escaped when the output character set
         * does not support a given character:- using either a named or a numbered escape.
         * @param escapeMode the new escape mode to use
         * @return the document's output settings, for chaining
         */
        public OutputSettings escapeMode(Entities.EscapeMode escapeMode) {
            this.escapeMode = escapeMode;
            return this;
        }

        /**
         * Get the document's current output charset, which is used to control which characters are escaped when
         * generating HTML (via the <code>html()</code> methods), and which are kept intact.
         * <p>
         * Where possible (when parsing from a URL or File), the document's output charset is automatically set to the
         * input charset. Otherwise, it defaults to UTF-8.
         * @return the document's current charset.
         */
        public Charset charset() {
            return Charset.forName(charsetName);
        }

        /**
         * Update the document's output charset.
         * @param charset the new charset to use.
         * @return the document's output settings, for chaining
         */
        public OutputSettings charset(Charset charset) {
            charsetName = charset.name();
            return this;
        }

        /**
         * Update the document's output charset.
         * @param charset the new charset (by name) to use.
         * @return the document's output settings, for chaining
         */
        public OutputSettings charset(String charset) {
            charset(Charset.forName(charset));
            return this;
        }

        CharsetEncoder encoder() {
            return Charset.forName(charsetName).newEncoder();
        }

        /**
         * Get the document's current output syntax.
         * @return current syntax
         */
        public Syntax syntax() {
            return syntax;
        }

        /**
         * Set the document's output syntax. Either {@code html}, with empty tags and boolean attributes (etc), or
         * {@code xml}, with self-closing tags.
         * @param syntax serialization syntax
         * @return the document's output settings, for chaining
         */
        public OutputSettings syntax(Syntax syntax) {
            this.syntax = syntax;
            return this;
        }

        /**
         * Get if pretty printing is enabled. Default is true. If disabled, the HTML output methods will not re-format
         * the output, and the output will generally look like the input.
         * @return if pretty printing is enabled.
         */
        public boolean prettyPrint() {
            return prettyPrint;
        }

        /**
         * Enable or disable pretty printing.
         * @param pretty new pretty print setting
         * @return this, for chaining
         */
        public OutputSettings prettyPrint(boolean pretty) {
            prettyPrint = pretty;
            return this;
        }

        /**
         * Get if outline mode is enabled. Default is false. If enabled, the HTML output methods will consider
         * all tags as block.
         * @return if outline mode is enabled.
         */
        public boolean outline() {
            return outline;
        }

        /**
         * Enable or disable HTML outline mode.
         * @param outlineMode new outline setting
         * @return this, for chaining
         */
        public OutputSettings outline(boolean outlineMode) {
            outline = outlineMode;
            return this;
        }

        /**
         * Get the current tag indent amount, used when pretty printing.
         * @return the current indent amount
         */
        public int indentAmount() {
            return indentAmount;
        }

        /**
         * Set the indent amount for pretty printing
         * @param indentAmount number of spaces to use for indenting each level. Must be {@literal >=} 0.
         * @return this, for chaining
         */
        public OutputSettings indentAmount(int indentAmount) {
            Validate.isTrue(indentAmount >= 0);
            this.indentAmount = indentAmount;
            return this;
        }

        @Override
        public OutputSettings clone() {
            OutputSettings clone;
            try {
                clone = (OutputSettings) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            clone.charset(charsetName); // new charset and charset encoder
            clone.escapeMode = Entities.EscapeMode.valueOf(escapeMode.name());
            // indentAmount, prettyPrint are primitives so object.clone() will handle
            return clone;
        }
    }

    /**
     * Get the document's current output settings.
     * @return the document's current output settings.
     */
    public OutputSettings outputSettings() {
        return outputSettings;
    }

    /**
     * Set the document's output settings.
     * @param outputSettings new output settings.
     * @return this document, for chaining.
     */
    public Document outputSettings(OutputSettings outputSettings) {
        Validate.notNull(outputSettings);
        this.outputSettings = outputSettings;
        return this;
    }

    public enum QuirksMode {
        noQuirks, quirks, limitedQuirks
    }

    public QuirksMode quirksMode() {
        return quirksMode;
    }

    public Document quirksMode(QuirksMode quirksMode) {
        this.quirksMode = quirksMode;
        return this;
    }
}
