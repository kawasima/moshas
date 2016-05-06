package net.unit8.moshas;

import net.unit8.moshas.dom.Document;
import net.unit8.moshas.helper.DataUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kawasima
 */
public class DefaultTemplate extends DefaultSnippet implements Template {
    protected DefaultTemplate(InputStream is) throws IOException {
        Document doc = DataUtil.load(is, "UTF-8", "");
        super.setRootElement(doc);
    }
}
