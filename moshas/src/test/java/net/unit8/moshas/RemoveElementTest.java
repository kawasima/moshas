package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.dom.Element;
import net.unit8.moshas.parser.Tag;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * @author kawasima
 */
public class RemoveElementTest {
    MoshasEngine moshas = new MoshasEngine();
    @Test
    public void test() {
        moshas.describe("META-INF/templates/head.html", t -> {
            t.select("#css-link", (el, ctx) -> el.remove());
            t.select("#css", (el, ctx) -> el.text("hello!"));
            t.select("#message1", (el, ctx) -> el.appendChild(new Element(Tag.valueOf("a"), "")));
        });

        Context ctx = new Context();
        String res = moshas.process("META-INF/templates/head.html", ctx);
        System.out.println(res);
        assertTrue(res.contains("<a></a>"));
    }

    @Test
    public void testWithTemplate() {
        Template template = moshas.describe("META-INF/templates/head.html", t -> {
            t.select("#css-link", (el, ctx) -> el.remove());
            t.select("#css", (el, ctx) -> el.text("hello!"));
            t.select("#message1", (el, ctx) -> el.appendChild(new Element(Tag.valueOf("a"), "")));
        });
        Context ctx = new Context();
        template.render(ctx, System.out);
        template.render(ctx, System.out);

    }

}
