package net.unit8.moshas;

import net.unit8.moshas.context.IContext;
import net.unit8.moshas.dom.Element;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

/**
 *
 * @author kawasima
 */
public interface Snippet extends Serializable {
    Element getRootElement();

    Element render(IContext context);

    default void render(IContext context, OutputStream out) {
        try {
            out.write(render(context).cachedHtml().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void render(IContext context, Writer writer) {
        try {
            writer.write(render(context).cachedHtml());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
