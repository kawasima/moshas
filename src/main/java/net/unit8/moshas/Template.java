package net.unit8.moshas;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.unit8.moshas.dom.TemplateDocmentType;
import net.unit8.moshas.dom.TemplateElement;
import net.unit8.moshas.dom.TemplateNode;
import net.unit8.moshas.dom.TemplateTextNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

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
            TemplateElement root = (TemplateElement) createTemplateNode(doc, null);
            super.setTargetElement(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            
            
    }

    private TemplateNode createTemplateNode(Node jsoupNode, TemplateNode parent) {
        TemplateNode templateNode = null;
        
        if (jsoupNode instanceof Element) {
            templateNode = new TemplateElement((Element) jsoupNode, parent);
        } else if (jsoupNode instanceof TextNode) {
            templateNode = new TemplateTextNode((TextNode) jsoupNode, parent);
        } else if (jsoupNode instanceof DocumentType) {
            templateNode = new TemplateDocmentType((DocumentType) jsoupNode, parent);
        } else {
            throw new RuntimeException("Unknown node type.");
        }
        

        for (int i = 0; i < jsoupNode.childNodeSize(); i++) {
            ((TemplateElement) templateNode).appendChild(createTemplateNode(jsoupNode.childNode(i), templateNode), 0);
        }
        
        return templateNode;
    }
    
    public static Template define(URL url, TemplateDefinition def) {
        Template template = new Template(url);
        def.define(template);
        return template;
    }
    
}
