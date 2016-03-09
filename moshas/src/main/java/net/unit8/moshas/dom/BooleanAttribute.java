package net.unit8.moshas.dom;

/**
 *
 * @author kawasima
 */
public class BooleanAttribute extends Attribute {
    /**
     * Create a new boolean attribute from unencoded (raw) key.
     * @param key attribute key
     */
    public BooleanAttribute(String key) {
        super(key, "");
    }

    @Override
    protected boolean isBooleanAttribute() {
        return true;
    }    
}
