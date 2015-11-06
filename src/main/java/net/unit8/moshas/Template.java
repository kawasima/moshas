package net.unit8.moshas;

import java.io.IOException;
import java.io.InputStream;
import net.unit8.moshas.dom.Document;
import net.unit8.moshas.helper.DataUtil;

/**
 *
 * @author kawasima
 */
public class Template extends Snippet {
    protected Template(InputStream is) throws IOException {
        Document doc = DataUtil.load(is, "UTF-8", "");
        super.setRootElement(doc);
    }
}
