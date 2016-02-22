package net.unit8.moshas.select;

import net.unit8.moshas.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author kawasima
 */
public class Elements extends ArrayList<Element> {
    public Elements() {
    }

    public Elements(int initialCapacity) {
        super(initialCapacity);
    }

    public Elements(Collection<Element> elements) {
        super(elements);
    }

    public Elements(List<Element> elements) {
        super(elements);
    }

    public Elements(Element... elements) {
    	super(Arrays.asList(elements));
    }

    /**
     * Get the first matched element.
     * @return The first matched element, or <code>null</code> if contents is empty.
     */
    public Element first() {
        return isEmpty() ? null : get(0);
    }

    /**
     Get the last matched element.
     @return The last matched element, or <code>null</code> if contents is empty.
     */
    public Element last() {
        return isEmpty() ? null : get(size() - 1);
    }

}
