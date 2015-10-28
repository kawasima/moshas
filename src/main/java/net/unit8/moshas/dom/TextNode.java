package net.unit8.moshas.dom;

import com.coverity.security.Escape;
import net.unit8.moshas.helper.StringUtil;
import net.unit8.moshas.helper.Validate;

/**
 *
 * @author kawasima
 */
public class TextNode extends Node {
    private final String text;
    
    public TextNode(String text, String baseUri) {
        this.text = text;
    }

    @Override
    public String nodeName() {
        return "#text";
    }
   
    public String getWholeText() {
        return text;
    }
    
    @Override
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append(Escape.htmlText(text));
    }

    @Override
    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {}
    
    /**
     Test if this text node is blank -- that is, empty or only whitespace (including newlines).
     @return true if this document is empty or only whitespace, false if it contains any text content.
     */
    public boolean isBlank() {
        return StringUtil.isBlank(getWholeText());
    }

    static boolean lastCharIsWhitespace(StringBuilder sb) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == ' ';
    }
}