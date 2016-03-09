package net.unit8.moshas.dom;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author kawasima
 */
public class Attributes implements Iterable<Attribute>, Cloneable, Serializable {
    private LinkedHashMap<String, Attribute> attributes = null;
    // linked hash map to preserve insertion order.
    // null be default as so many elements have no attributes -- saves a good chunk of memory

    /**
     Get an attribute value by key.
     @param key the attribute key
     @return the attribute value if set; or empty string if not set.
     @see #hasKey(String)
     */
    public String get(String key) {
        if (attributes == null)
            return "";

        Attribute attr = attributes.get(key.toLowerCase());
        return attr != null ? attr.getValue() : "";
    }

    /**
     Set a new attribute, or replace an existing one by key.
     @param key attribute key
     @param value attribute value
     */
    public void put(String key, String value) {
        Attribute attr = new Attribute(key, value);
        put(attr);
    }

    /**
    Set a new boolean attribute, remove attribute if value is false.
    @param key attribute key
    @param value attribute value
    */
    public void put(String key, boolean value) {
        if (value)
            put(new BooleanAttribute(key));
        else
            remove(key);
    }

    /**
     Set a new attribute, or replace an existing one by key.
     @param attribute attribute
     */
    public void put(Attribute attribute) {
        if (attributes == null)
             attributes = new LinkedHashMap<>(2);
        attributes.put(attribute.getKey(), attribute);
    }

    /**
     Remove an attribute by key.
     @param key attribute key to remove
     */
    public void remove(String key) {
        if (attributes == null)
            return;
        attributes.remove(key.toLowerCase());
    }

    /**
     Tests if these attributes contain an attribute with this key.
     @param key key to check for
     @return true if key exists, false otherwise
     */
    public boolean hasKey(String key) {
        return attributes != null && attributes.containsKey(key.toLowerCase());
    }

    /**
     Get the number of attributes in this set.
     @return size
     */
    public int size() {
        if (attributes == null)
            return 0;
        return attributes.size();
    }

    /**
     Add all the attributes from the incoming set to this set.
     @param incoming attributes to add to these attributes.
     */
    public void addAll(Attributes incoming) {
        if (incoming.size() == 0)
            return;
        if (attributes == null)
            attributes = new LinkedHashMap<>(incoming.size());
        attributes.putAll(incoming.attributes);
    }

    public Iterator<Attribute> iterator() {
        return asList().iterator();
    }

    /**
     Get the attributes as a List, for iteration. Do not modify the keys of the attributes via this view, as changes
     to keys will not be recognised in the containing set.
     @return an view of the attributes as a List.
     */
    public List<Attribute> asList() {
        if (attributes == null)
            return Collections.emptyList();

        List<Attribute> list = new ArrayList<>(attributes.size());
        list.addAll(attributes.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
        return Collections.unmodifiableList(list);
    }

    /**
     Get the HTML representation of these attributes.
     @return HTML
     */
    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum, (new Document("")).outputSettings()); // output settings a bit funky, but this html() seldom used
        return accum.toString();
    }

    void html(StringBuilder accum, Document.OutputSettings out) {
        if (attributes == null)
            return;

        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            Attribute attribute = entry.getValue();
            accum.append(" ");
            attribute.html(accum);
        }
    }

    @Override
    public String toString() {
        return html();
    }

    /**
     * Checks if these attributes are equal to another set of attributes, by comparing the two sets
     * @param o attributes to compare with
     * @return if both sets of attributes have the same content
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attributes)) return false;

        Attributes that = (Attributes) o;

        return !(attributes != null ? !attributes.equals(that.attributes) : that.attributes != null);
    }

    /**
     * Calculates the hashcode of these attributes, by iterating all attributes and summing their hashcodes.
     * @return calculated hashcode
     */
    @Override
    public int hashCode() {
        return attributes != null ? attributes.hashCode() : 0;
    }

    @Override
    public Attributes clone() {
        if (attributes == null)
            return new Attributes();

        Attributes clone;
        try {
            clone = (Attributes) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.attributes = new LinkedHashMap<>(attributes.size());
        for (Attribute attribute: this)
            clone.attributes.put(attribute.getKey(), attribute.clone());
        return clone;
    }

}
