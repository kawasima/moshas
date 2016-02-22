package net.unit8.moshas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.unit8.moshas.context.Context;
import org.junit.Test;

import java.io.StringWriter;

import static net.unit8.moshas.RenderUtils.*;

/**
 *
 * @author kawasima
 */
public class TemplateTest {
    @Test
    public void test() {
        MoshasEngine engine = new MoshasEngine();
        Template index = engine.defineTemplate("META-INF/templates/index.html", t -> t.select("p#message", text("message")));
        Context context = new Context();
        context.setVariable("message", "We changed the message!");
        index.render(context, System.out);
    }

    @Test
    public void eachTest() {
        MoshasEngine engine = new MoshasEngine();
        Template eachTemplate = engine.defineTemplate("META-INF/templates/each.html", t -> {
            t.select("#title", text("title"));

            Snippet linkSnippet = engine.defineSnippet("META-INF/templates/each.html" , ".section .content li", s ->
                    s.select("a",
                            doAll(
                                    attr("href", "link", "href"),
                                    text("link", "text"))));

            Snippet sectionSnippet = engine.defineSnippet("META-INF/templates/each.html",".section", s -> {
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
        context.setVariable("sections", Lists.newArrayList(
                ImmutableMap.of(
                        "title", "Clojure",
                        "data", ImmutableList.of(
                                ImmutableMap.of(
                                        "text", "Macros",
                                        "href", "http://www.clojure.org/macros")
                        )),
                ImmutableMap.of(
                        "title", "Compojure",
                        "data", ImmutableList.of(
                                ImmutableMap.of(
                                        "text", "Requests",
                                        "href", "http://www.compojure.org/docs/requests"),
                                ImmutableMap.of(
                                        "text", "Middleware",
                                        "href", "http://www.compojure.org/docs/middleware")))
        ));
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
