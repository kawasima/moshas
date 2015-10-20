package net.unit8.moshas;

import org.jsoup.nodes.Element;

/**
 *
 * @author kawasima
 */
public interface RenderFunction {
    void render(Element el, Context ctx);
}
