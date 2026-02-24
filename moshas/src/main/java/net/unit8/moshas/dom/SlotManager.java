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

    public static void add(Element el) {
        modifiedElements.computeIfAbsent(RenderingId.get(), k -> new HashSet<>()).add(el);
    }

    public static void clear(Integer id) {
        Set<Element> elements = modifiedElements.remove(id);
        if (elements == null) return;
        for (Element el : elements) {
            el.cleanThreadCache(id);
        }
    }
}
