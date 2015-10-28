package net.unit8.moshas.loader;

/**
 *
 * @author kawasima
 */
public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(String msg) {
        super(msg);
    }
    
    public TemplateNotFoundException(String msg, Exception cause) {
        super(msg, cause);
    }
}
