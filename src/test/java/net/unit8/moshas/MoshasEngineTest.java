package net.unit8.moshas;

import net.unit8.moshas.context.Context;
import net.unit8.moshas.loader.TemplateNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;

/**
 * @author kawasima
 */
public class MoshasEngineTest {
    MoshasEngine engine;

    @Before
    public void setUpEngine() {
        engine = new MoshasEngine();
    }

    @Test(expected = TemplateNotFoundException.class)
    public void test() {
        engine.defineTemplate("notfound", t -> {});
    }

    @Test
    public void test1() {
        Template template = engine.defineTemplate("META-INF/templates/index.html", t -> {});
        Context context = new Context();
        StringWriter writer = new StringWriter();
        template.render(context, writer);
        System.out.println(writer.toString());
    }
}
