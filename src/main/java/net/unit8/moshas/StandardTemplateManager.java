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
    public Template getTemplate(String source) {
        Template template = cache.getTemplate(source);
        if (template != null) {
            return template;
        }

        try (InputStream is = findTemplate(source)) {
            Template newTemplate = new Template(is);
            cache.putTemplate(source, newTemplate);
            return newTemplate;
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
