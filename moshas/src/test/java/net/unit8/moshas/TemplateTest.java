package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.unit8.moshas.RenderUtils.*;

/**
 *
 * @author kawasima
 */
public class TemplateTest {
    @Test
    public void test() {
        MoshasEngine engine = new MoshasEngine();
        Template index = engine.describe("META-INF/templates/index.html", t -> t.select("p#message", text("message")));
        Context context = new Context();
        context.setVariable("message", "We changed the message!");
        index.render(context, System.out);
    }

    @Test
    public void eachTest() {
        MoshasEngine engine = new MoshasEngine();
        Template eachTemplate = engine.describe("META-INF/templates/each.html", t -> {
            t.select("#title", text("title"));

            Snippet linkSnippet = engine.describe("META-INF/templates/each.html" , ".section .content li", s ->
                    s.select("a",
                            doAll(
                                    attr("href", "link", "href"),
                                    text("link", "text"))));

            Snippet sectionSnippet = engine.describe("META-INF/templates/each.html",".section", s -> {
                s.select(".title", text("section", "title"));
                s.select(".content", (el, ctx)-> {
                    el.empty();

                    ctx.getCollection("section", "data").forEach(data -> ctx.localScope("link", data, () -> {
                        el.appendChild(linkSnippet.render(ctx));
                    }));
                });
            });

            t.select("body", (el, ctx) -> {
                el.empty();
                ctx.getCollection("sections").forEach(section ->
                    ctx.localScope("section", section, () ->
                        el.appendChild(sectionSnippet.render(ctx))
                    )
                );
            });
        });
        Context context = new Context();
        context.setVariable("title", "Moshas  Template2 Tutorial");
        context.setVariable("sections", new ArrayList<>(List.of(
                Map.of(
                        "title", "Clojure",
                        "data", List.of(
                                Map.of(
                                        "text", "Macros",
                                        "href", "http://www.clojure.org/macros")
                        )),
                Map.of(
                        "title", "Compojure",
                        "data", List.of(
                                Map.of(
                                        "text", "Requests",
                                        "href", "http://www.compojure.org/docs/requests"),
                                Map.of(
                                        "text", "Middleware",
                                        "href", "http://www.compojure.org/docs/middleware")))
        )));
        long t1 = System.currentTimeMillis();
        for (int i=0; i < 1; i++) {
            StringWriter writer = new StringWriter();
            eachTemplate.render(context, writer);
            writer.toString();
        }
        eachTemplate.render(context, System.out);
        System.out.println("elaspe=" + (System.currentTimeMillis() - t1));
    }
}
