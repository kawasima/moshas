package net.unit8.moshas.loader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author kawasima
 */
public class CompositTemplateLoader extends TemplateLoader {
    private List<TemplateLoader> loaders;

    public CompositTemplateLoader(TemplateLoader... loaders) {
        this.loaders = Arrays.asList(loaders);
    }

    @Override
    public InputStream getTemplateStream(String templateSource) throws TemplateNotFoundException {
        InputStream is = null;

        for (TemplateLoader loader : loaders) {
            try {
                is = loader.getTemplateStream(templateSource);
            } catch (TemplateNotFoundException ignore) {

            }
        }

        if (is == null) {
            throw new TemplateNotFoundException(templateSource);
        }
        return is;
    }
}
