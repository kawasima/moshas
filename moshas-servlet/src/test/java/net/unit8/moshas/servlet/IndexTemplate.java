package net.unit8.moshas.servlet;

import net.unit8.moshas.Template;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kawasima
 */
public abstract class IndexTemplate implements Template {
    protected IndexTemplate(InputStream is) throws IOException {
        super();
    }
}
