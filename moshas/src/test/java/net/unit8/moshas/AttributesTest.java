package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author kawasima
 */
public class AttributesTest {
    MoshasEngine moshas = new MoshasEngine();

    @Test
    public void removeAttribute() {
        moshas.describe("META-INF/templates/attributes.html", t -> {
            t.select("#container", (el, ctx) -> {
                el.removeAttr("id");
                el.attr("class", "container");
            });

            t.select(".column", (el, ctx) -> el.addClass("red"));
        });

        Context ctx = new Context();
        String res = moshas.process("META-INF/templates/attributes.html", ctx);
        assertTrue(res.contains("class=\"container\""));
        assertFalse(res.contains("id=\"container\""));
        assertTrue(res.contains("class=\"column red\""));
    }
}
