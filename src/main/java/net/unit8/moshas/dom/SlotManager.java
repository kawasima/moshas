package net.unit8.moshas.dom;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author kawasima
 */
public class SlotManager {

    private static final Map<Integer, HashSet<Element>> modifiedElements = new ConcurrentHashMap<>();

    private static HashSet<Element> newSet() {
        return new HashSet<>();
    }
    public static void add(Element el) {
        modifiedElements.putIfAbsent(RenderingId.get(), newSet());
        modifiedElements.get(RenderingId.get()).add(el);
    }

    public static void clear(Integer id) {
        Set<Element> elements = modifiedElements.remove(id);
        if (elements == null) return;
        for (Element el : elements) {
            el.cleanThreadCache(id);
        }
    }
}
