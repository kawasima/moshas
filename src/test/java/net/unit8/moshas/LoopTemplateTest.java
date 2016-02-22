package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.loader.ResourceTemplateLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author kawasima
 */
public class LoopTemplateTest {
    private Template template;

    @Before
    public void setup() throws IOException {
        MoshasEngine engine = new MoshasEngine();
        ResourceTemplateLoader resourceTemplateLoader = new ResourceTemplateLoader();
        resourceTemplateLoader.setPrefix("META-INF/templates/");
        resourceTemplateLoader.setSuffix(".html");

        engine.getTemplateManager().setTemplateLoaders(resourceTemplateLoader);
        template = engine.defineTemplate("stocks.moshas", t -> {
            Snippet stockSnippet = engine.defineSnippet("stocks.moshas", "tbody > tr", s -> {
                s.root((el, ctx) -> {
                    el.addClass(ctx.getInt("itemIndex") % 2 == 0 ? "even" : "odd");
                });
                s.select("td:eq(0)", (el, ctx) -> { el.text(ctx.getString("itemIndex")); });
                s.select("td:eq(1) > a", (el, ctx) -> {
                    el.attr("href", "/stocks/" + ctx.getString("stock", "symbol"));
                    el.text(ctx.getString("stock", "symbol"));
                });
                s.select("td:eq(2) > a", (el, ctx) -> {
                    el.attr("href", ctx.getString("stock", "url"));
                    el.text(ctx.getString("stock", "name"));
                });
                s.select("td:eq(3) > strong", (el, ctx) -> { el.text(ctx.getString("stock", "price")); });
                s.select("td:eq(4)", (el, ctx) -> {
                    if (ctx.getDouble("stock", "change") < 0) {
                        el.attr("class", "minus");
                    }
                    el.text(ctx.getString("stock", "change"));
                });
                s.select("td:eq(5)", (el, ctx) -> {
                    if (ctx.getDouble("stock", "change") < 0) {
                        el.attr("class", "minus");
                    }
                    el.text(ctx.getString("stock", "ratio"));
                });

            });

            t.select("tbody", (el, ctx) -> {
                el.empty();
                AtomicInteger counter = new AtomicInteger(0);
                ctx.getCollection("items").forEach(item -> {
                    ctx.localScope("stock", item, "itemIndex", counter.incrementAndGet(), () -> {
                        el.appendChild(stockSnippet.render(ctx));
                    });
                });
            });

        });
    }

    protected Map<String, Object> getContext() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("items", Stock.dummyItems());
        return ctx;
    }

    @Test
    public void benchmark() throws IOException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(100);

        for (int i=0; i<100; i++) {
            service.execute(() -> {
                Context context = new Context(getContext());
                Writer writer = new StringWriter();
                template.render(context, writer);
                Assert.assertEquals(5634, writer.toString().length());
            });
        }
        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        Context context = new Context(getContext());
        template.render(context, System.out);
    }
}
