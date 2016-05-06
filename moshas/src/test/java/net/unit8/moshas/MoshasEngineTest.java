package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.loader.TemplateNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.StringWriter;
import java.util.Locale;

import static net.unit8.moshas.RenderUtils.text;

/**
 * @author kawasima
 */
@RunWith(Parameterized.class)
public class MoshasEngineTest {
    @Parameterized.Parameter(0)
    public MoshasEngine engine;

    @Parameterized.Parameters(name = "engine [{0}]")
    public static Object[][] arguments() {
        return new Object[][]{
                {new MoshasEngine()},
                {new MoshasEngine(new StandardTemplateManager(new ConcurrentHashMapTemplateCache()))}
        };
    }

    @Test(expected = TemplateNotFoundException.class)
    public void test() {
        engine.describe("notfound", t -> {});
    }

    @Test
    public void test1() {
        Template template = engine.describe("META-INF/templates/index.html", t -> {});
        Context context = new Context();
        StringWriter writer = new StringWriter();
        template.render(context, writer);
        System.out.println(writer.toString());
    }

    @Test
    public void variableNotFound() {
        Template template = engine.describe("META-INF/templates/index.html", t -> t.select("#message", text("message", "japanese")));
        Context context = new Context();
        StringWriter writer = new StringWriter();
        template.render(context, writer);
    }

    @Test
    public void conversionError() {
        Template template = engine.describe("META-INF/templates/index.html", t -> t.select("#message", (el, ctx) -> el.text(String.format(Locale.US, "%.2f", ctx.getDouble("message")))));
        Context context = new Context();
        context.setVariable("message", 3.14);
        StringWriter writer = new StringWriter();
        template.render(context, writer);
    }
}
