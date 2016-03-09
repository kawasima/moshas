package net.unit8.moshas.dom;

import com.coverity.security.Escape;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author kawasima
 */
public class Attribute implements Map.Entry<String, String>, Cloneable, Serializable {
    private static final String[] booleanAttributes = {
            "allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled",
            "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize",
            "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected",
            "sortable", "truespeed", "typemustmatch"
    };

    private String key;
    private String value;

    /**
     * Create a new attribute from unencoded (raw) key and value.
     * @param key attribute key
     * @param value attribute value
     */
    public Attribute(String key, String value) {
        this.key = key.trim().toLowerCase();
        this.value = value;
    }

    /**
     Get the attribute key.
     @return the attribute key
     */
    public String getKey() {
        return key;
    }

    /**
     Set the attribute key. Gets normalised as per the constructor method.
     @param key the new key; must not be null
     */
    public void setKey(String key) {
        this.key = key.trim().toLowerCase();
    }

    /**
     Get the attribute value.
     @return the attribute value
     */
    public String getValue() {
        return value;
    }

    /**
     Set the attribute value.
     @param value the new attribute value; must not be null
     */
    public String setValue(String value) {
        String old = this.value;
        this.value = value;
        return old;
    }

    /**
     Get the HTML representation of this attribute; e.g. {@code href="index.html"}.
     @return HTML
     */
    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum);
        return accum.toString();
    }

    protected void html(StringBuilder accum) {
        accum.append(key);
        if (!shouldCollapseAttribute()) {
            accum.append("=\"")
                 .append(Escape.htmlText(value))
                 .append('"');
        }
    }

    /**
     Get the string representation of this attribute, implemented as {@link #html()}.
     @return string
     */
    @Override
    public String toString() {
        return html();
    }


    /**
     * Collapsible if it's a boolean attribute and value is empty or same as name
     *
     * @return  Returns whether collapsible or not
     */
    protected final boolean shouldCollapseAttribute() {
        return ("".equals(value) || value.equalsIgnoreCase(key))
                && isBooleanAttribute();
    }

    protected boolean isBooleanAttribute() {
        return Arrays.binarySearch(booleanAttributes, key) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;

        Attribute attribute = (Attribute) o;

        if (key != null ? !key.equals(attribute.key) : attribute.key != null) return false;
        return !(value != null ? !value.equals(attribute.value) : attribute.value != null);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone(); // only fields are immutable strings key and value, so no more deep copy required
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
