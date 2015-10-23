package net.unit8.moshas;

/**
 *
 * @author kawasima
 */
public class RenderUtils {
    public static RenderFunction text(String... keys) {
        return (el, ctx) ->  {
            el.text(ctx.getString(keys));
        };
    }
    
    public static RenderFunction attr(String attrName, String... keys) {
        return (el, ctx) ->  {
            el.setAttr(attrName, ctx.getString(keys));
        };
    }

    public static RenderFunction doAll(RenderFunction... funcs) {
        return (el, ctx) -> {
            for (RenderFunction func : funcs) {
                func.render(el, ctx);
            }
        };
    }
}
