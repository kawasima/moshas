package net.unit8.moshas;

import net.unit8.moshas.context.IContext;
import net.unit8.moshas.dom.Element;

import java.io.Serializable;

/**
 *
 * @author kawasima
 */
public interface RenderFunction extends Serializable {
    void render(Element el, IContext ctx);
}
