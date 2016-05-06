package net.unit8.moshas;

import net.unit8.moshas.cache.JCacheTemplateCache;
import net.unit8.moshas.cache.TemplateCache;
import net.unit8.moshas.loader.ResourceTemplateLoader;
import net.unit8.moshas.loader.TemplateLoader;
import net.unit8.moshas.loader.TemplateNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author kawasima
 */
public class StandardTemplateManager implements TemplateManager {
    private List<TemplateLoader> templateLoaders;
    private final TemplateCache cache;

    public StandardTemplateManager(TemplateCache templateCache) {
        templateLoaders = new ArrayList<>();
        templateLoaders.add(new ResourceTemplateLoader());
        cache = templateCache;
    }

    public StandardTemplateManager() {
        this(new JCacheTemplateCache());
    }

    @Override
    public Template loadTemplate(String templateName) {
        try (InputStream is = findTemplate(templateName)) {
            Template newTemplate = new DefaultTemplate(is);
            return newTemplate;
        } catch (IOException e) {
            throw new TemplateNotFoundException(templateName, e);
        }
    }

    @Override
    public Template getTemplate(String templateName) {
        return cache.getTemplate(templateName);
    }

    @Override
    public void cacheTemplate(String templateName, Template template) {
        cache.putTemplate(templateName, template);
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
