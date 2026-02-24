package net.unit8.moshas.cache;

import net.unit8.moshas.Template;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple LRU (Least Recently Used) template cache backed by {@link LinkedHashMap}.
 * Thread-safe via synchronization.
 *
 * @author kawasima
 */
public class LruTemplateCache implements TemplateCache {
    private static final int DEFAULT_MAX_SIZE = 256;

    private final Map<String, Template> cache;

    public LruTemplateCache() {
        this(DEFAULT_MAX_SIZE);
    }

    public LruTemplateCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Template> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public synchronized Template getTemplate(String source) {
        return cache.get(source);
    }

    @Override
    public synchronized void putTemplate(String source, Template template) {
        cache.put(source, template);
    }
}
