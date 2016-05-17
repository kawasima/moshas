package net.unit8.moshas;

import net.unit8.moshas.context.IContext;
import net.unit8.moshas.dom.Element;

import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 *
 * @author kawasima
 */
public class MoshasEngine {
    private final TemplateManager manager;
    private final ConcurrentHashMap<String, ViewLogicDescriber> describerCache = new ConcurrentHashMap<>();

    public MoshasEngine(TemplateManager manager) {
        this.manager = manager;
    }

    public MoshasEngine() {
        this(new StandardTemplateManager());
    }

    public TemplateManager getTemplateManager() {
        return manager;
    }

    private Snippet describeSnippet(Snippet snippet, ViewLogicDescriber describer) {
        ViewLogicDescriptor descriptor = new ViewLogicDescriptor(snippet.getRootElement());
        describer.describe(descriptor);
        Stream.of(snippet)
                .filter(DescribableTemplate.class::isInstance)
                .map(DescribableTemplate.class::cast)
                .forEach(dt -> dt.setProcessors(descriptor.getProcessors()));
        return snippet;
    }

    public Template describe(String templateName, ViewLogicDescriber describer) {
        if (describer == null) {
            throw new IllegalArgumentException("describer is required");
        }

        Template template = manager.loadTemplate(templateName);

        describeSnippet(template, describer);
        // Mark selected elements
        Element root = template.getRootElement();
        root.children().forEach(Element::selected);

        manager.cacheTemplate(templateName, template);
        describerCache.put(templateName, describer);
        return template;
    }

    public Snippet describe(String templateName, String selector, ViewLogicDescriber describer) {
        Template template = manager.loadTemplate(templateName);
        Element el = template.getRootElement().select(selector).first();
        Snippet snippet = new DefaultSnippet(el);
        return describeSnippet(snippet, describer);
    }

    public String process(String templateName, IContext context) {
        StringWriter sw = new StringWriter();
        process(templateName, context, sw);
        return sw.toString();
    }

    public void process(String templateName, IContext context, Writer writer) {
        Template template = manager.getTemplate(templateName);
        if (template == null) {
            if (describerCache.containsKey(templateName)) {
                template = describe(templateName, describerCache.get(templateName));
            } else {
                throw new TemplateNotDescribedException(templateName);
            }
        }
        template.render(context, writer);
    }
}
