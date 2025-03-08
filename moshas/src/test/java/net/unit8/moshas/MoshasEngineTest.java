package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.loader.TemplateNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringWriter;
import java.util.Locale;
import java.util.stream.Stream;

import static net.unit8.moshas.RenderUtils.text;

/**
 * @author kawasima
 */
public class MoshasEngineTest {
    private MoshasEngine engine;

    static Stream<MoshasEngine> engineProvider() {
        return Stream.of(
            new MoshasEngine(),
            new MoshasEngine(new StandardTemplateManager(new ConcurrentHashMapTemplateCache()))
        );
    }

    @ParameterizedTest
    @MethodSource("engineProvider")
    void testTemplateNotFound(MoshasEngine engine) {
        org.junit.jupiter.api.Assertions.assertThrows(TemplateNotFoundException.class, () -> {
            engine.describe("notfound", t -> {});
        });
    }

    @ParameterizedTest
    @MethodSource("engineProvider")
    void test1(MoshasEngine engine) {
        Template template = engine.describe("META-INF/templates/index.html", t -> {});
        Context context = new Context();
        StringWriter writer = new StringWriter();
        template.render(context, writer);
        System.out.println(writer.toString());
    }

    @ParameterizedTest
    @MethodSource("engineProvider")
    void testVariableNotFound(MoshasEngine engine) {
        Template template = engine.describe("META-INF/templates/index.html", t -> t.select("#message", text("message", "japanese")));
        Context context = new Context();
        StringWriter writer = new StringWriter();
        template.render(context, writer);
    }

    @ParameterizedTest
    @MethodSource("engineProvider")
    void testConversionError(MoshasEngine engine) {
        Template template = engine.describe("META-INF/templates/index.html", t -> t.select("#message", (el, ctx) -> el.text(String.format(Locale.US, "%.2f", ctx.getDouble("message")))));
        Context context = new Context();
        context.setVariable("message", 3.14);
        StringWriter writer = new StringWriter();
        template.render(context, writer);
    }
}
