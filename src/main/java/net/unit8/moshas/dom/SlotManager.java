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
        HashSet<Element> slot = modifiedElements.get(RenderingId.get());
        if (slot == null) {
            slot = new HashSet<>();
            modifiedElements.put(RenderingId.get(), slot);
        }
        slot.add(el);
    }
    
    public static void clear(Integer id) {
        Set<Element> elements = modifiedElements.remove(id);
        for (Element el : elements) {
            el.cleanThreadCache(id);
        }
    }
}
