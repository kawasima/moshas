package net.unit8.moshas;

/**
 * @author kawasima
 */
public class TemplateNotDescribedException extends RuntimeException {
    private String templateName;

    public TemplateNotDescribedException(String templateName) {
        super("Template `" + templateName + "` is not described.");
        this.templateName = templateName;
    }

    public String getTemplateName(String templateName) {
        return this.templateName;
    }
}
