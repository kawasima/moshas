package net.unit8.moshas.select;

import java.util.Collection;
import java.util.LinkedHashSet;
import net.unit8.moshas.dom.Element;
import net.unit8.moshas.helper.Validate;

/**
 *
 * @author kawasima
 */
public class Selector {
    private final Evaluator evaluator;
    private final Element root;

    private Selector(String query, Element root) {
        Validate.notNull(query);
        query = query.trim();
        Validate.notEmpty(query);
        Validate.notNull(root);

        this.evaluator = QueryParser.parse(query);

        this.root = root;
    }

    private Selector(Evaluator evaluator, Element root) {
        Validate.notNull(evaluator);
        Validate.notNull(root);

        this.evaluator = evaluator;
        this.root = root;
    }

    /**
     * Find elements matching selector.
     *
     * @param query CSS selector
     * @param root  root element to descend into
     * @return matching elements, empty if none
     * @throws Selector.SelectorParseException (unchecked) on an invalid CSS query.
     */
    public static Elements select(String query, Element root) {
        return new Selector(query, root).select();
    }

    /**
     * Find elements matching selector.
     *
     * @param evaluator CSS selector
     * @param root root element to descend into
     * @return matching elements, empty if none
     */
    public static Elements select(Evaluator evaluator, Element root) {
        return new Selector(evaluator, root).select();
    }

    /**
     * Find elements matching selector.
     *
     * @param query CSS selector
     * @param roots root elements to descend into
     * @return matching elements, empty if none
     */
    public static Elements select(String query, Iterable<Element> roots) {
        Validate.notEmpty(query);
        Validate.notNull(roots);
        Evaluator evaluator = QueryParser.parse(query);
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();

        for (Element root : roots) {
            elements.addAll(select(evaluator, root));
        }
        return new Elements(elements);
    }

    private Elements select() {
        return Collector.collect(evaluator, root);
    }

    // exclude set. package open so that Elements can implement .not() selector.
    static Elements filterOut(Collection<Element> elements, Collection<Element> outs) {
        Elements output = new Elements();
        for (Element el : elements) {
            boolean found = false;
            for (Element out : outs) {
                if (el.equals(out)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                output.add(el);
        }
        return output;
    }

    public static class SelectorParseException extends IllegalStateException {
        public SelectorParseException(String msg, Object... params) {
            super(String.format(msg, params));
        }
    }
    
}
