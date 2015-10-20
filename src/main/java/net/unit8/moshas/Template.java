package net.unit8.moshas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author kawasima
 */
public class Template extends Snippet {
    private final URL templateUrl;
    
    protected Template(URL url) {
        this.templateUrl = url;
        try (InputStream in = templateUrl.openStream()) {
            Document doc = Jsoup.parse(in, "UTF-8", "");
            super.setTargetElement(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            
            
    }
    
    public static Template define(URL url, TemplateDefinition def) {
        Template template = new Template(url);
        def.define(template);
        return template;
    }
    
}
