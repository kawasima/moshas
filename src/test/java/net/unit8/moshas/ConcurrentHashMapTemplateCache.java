package net.unit8.moshas;

import net.unit8.moshas.cache.TemplateCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashMapTemplateCache implements TemplateCache {
    ConcurrentMap<String, Template> cache;

    public ConcurrentHashMapTemplateCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public Template getTemplate(String source) {
        return this.cache.get(source);
    }

    @Override
    public void putTemplate(String source, Template template) {
        this.cache.put(source, template);
    }
}
