package net.unit8.moshas.cache;

import net.unit8.moshas.Template;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class JCacheTemplateCache implements TemplateCache {
    private final Cache<String, Template> cache;

    public JCacheTemplateCache() {
        Iterator<CachingProvider> cachingProviders = Caching.getCachingProviders().iterator();
        if (cachingProviders.hasNext()) {
            CachingProvider cachingProvider = cachingProviders.next();
            CacheManager cacheManager = cachingProvider.getCacheManager();
            Configuration<String, Template> config = new MutableConfiguration<String, Template>()
                    .setTypes(String.class, Template.class)
                    .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 5)));
            Cache<String, Template> cache = cacheManager.getCache("TemplateCache", String.class, Template.class);
            if (cache == null) {
                this.cache = cacheManager.createCache("TemplateCache", config);
            } else {
                this.cache = cache;
            }
        } else {
            this.cache = null; // to keep compatibility with 0.1.0, but ugly
        }
    }

    @Override
    public Template getTemplate(String source) {
        if (this.cache == null) {
            return null;
        }
        return this.cache.get(source);
    }

    @Override
    public void putTemplate(String source, Template template) {
        if (this.cache == null) {
            return;
        }
        this.cache.put(source, template);
    }
}
