package net.unit8.moshas;

import net.unit8.moshas.loader.ResourceTemplateLoader;
import net.unit8.moshas.loader.TemplateLoader;
import net.unit8.moshas.loader.TemplateNotFoundException;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kawasima
 */
public class StandardTemplateManager implements TemplateManager {
    private List<TemplateLoader> templateLoaders;
    private Cache<String, Template> cache;

    public StandardTemplateManager() {
        templateLoaders = new ArrayList<>();
        templateLoaders.add(new ResourceTemplateLoader());

        Iterator<CachingProvider> cachingProviders = Caching.getCachingProviders().iterator();

        if (cachingProviders.hasNext()) {
            CachingProvider cachingProvider = cachingProviders.next();
            CacheManager cacheManager = cachingProvider.getCacheManager();
            Configuration<String, Template> config = new MutableConfiguration<String, Template>()
                    .setTypes(String.class, Template.class)
                    .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 5)));
            cache = cacheManager.getCache("TemplateCache", String.class, Template.class);
            if (cache == null) {
                cache = cacheManager.createCache("TemplateCache", config);
            }
        }
    }

    @Override
    public Template getTemplate(String source) {
        if (cache != null) {
            Template template = cache.get(source);
            if (template != null) {
                return template;
            }
        }
        try (InputStream is = findTemplate(source)) {
            Template template = new Template(is);
            if (cache != null) {
                cache.put(source, template);
            }
            return template;
        } catch (IOException e) {
            throw new TemplateNotFoundException(source, e);
        }
    }

    @Override
    public void setTemplateLoaders(TemplateLoader... loaders) {
        if (loaders == null)
            throw new IllegalArgumentException("loaders is required");

        this.templateLoaders = Arrays.asList(loaders);
    }

    protected InputStream findTemplate(String source) {
        for (TemplateLoader loader : templateLoaders) {
            InputStream is = loader.getTemplateStream(source);
            if (is != null) {
                return is;
            }
        }

        throw new TemplateNotFoundException("Template not found " + source);
    }
}
