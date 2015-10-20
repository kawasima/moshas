package net.unit8.moshas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.StringWriter;
import org.junit.Test;

/**
 *
 * @author kawasima
 */
public class TemplateTest {
    @Test
    public void test() {
        Template index = Template.define(getClass().getClassLoader().getResource("META-INF/templates/index.html"), t -> {
            t.select("p#message", (el, ctx) -> {
                el.text((String) ctx.get("message"));
            });
        });
        Context context = new Context();
        context.setVariable("message", "We changed the message!");
        index.render(context, System.out);
    }
    
    @Test
    public void eachTest() {
        Template eachTemplate = Template.define(getClass().getClassLoader().getResource("META-INF/templates/each.html"), (t) -> {
            t.select("#title", (el, ctx) -> {
                el.text((String) ctx.get("title"));
            });
            Snippet link = Snippet.define(t.select(".section .content li"), s -> {
                s.select("a", (el, ctx) -> {
                    el.attr("href", (String) ctx.get("link", "href"));
                    el.text((String) ctx.get("link", "text"));
                });
            });

            Snippet sec = Snippet.define(t.select(".section"), s -> {
                s.select(".title", (el, ctx) -> {
                    el.text((String) ctx.get("section", "title"));
                });
                s.select(".content", (el, ctx)-> {
                    el.children().remove();
                    ctx.getCollection("section", "data").forEach(data -> {
                        ctx.localScope("link", data, () -> {
                            el.appendChild(link.render(ctx));
                        });
                    });
                });
            });
            t.select("body", (el, ctx) -> {
                el.children().remove();
                ctx.getCollection("sections").forEach(section -> {
                    ctx.localScope("section", section, () -> {
                        el.appendChild(sec.render(ctx));
                    });

                });
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
        for (int i=0; i < 100; i++) {
            StringWriter writer = new StringWriter();
            eachTemplate.render(context, writer);
            writer.toString();
        }
        System.out.println("elaspe=" + (System.currentTimeMillis() - t1));
    }
}
