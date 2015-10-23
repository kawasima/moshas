package net.unit8.moshas;

import net.unit8.moshas.dom.TemplateElement;

/**
 *
 * @author kawasima
 */
public interface RenderFunction {
    void render(TemplateElement el, Context ctx);
}
