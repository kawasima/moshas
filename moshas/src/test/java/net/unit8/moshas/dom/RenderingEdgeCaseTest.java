package net.unit8.moshas.dom;

import net.unit8.moshas.*;
import net.unit8.moshas.context.Context;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RenderingEdgeCaseTest {

    // --- OutputSettings isolation ---

    @Test
    void cachedHtmlDoesNotMutateSharedOutputSettings() {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t ->
                t.select("#message", (el, ctx) -> el.text("hello")));

        Document doc = (Document) template.getRootElement();
        boolean prettyPrintBefore = doc.outputSettings().prettyPrint();

        Context ctx = new Context();
        StringWriter writer = new StringWriter();
        template.render(ctx, writer);

        assertEquals(prettyPrintBefore, doc.outputSettings().prettyPrint(),
                "cachedHtml() should not mutate the original Document's OutputSettings");
    }

    @Test
    void multipleRendersProduceIdenticalOutput() {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t ->
                t.select("#message", (el, ctx) -> el.text(ctx.getString("msg"))));

        Context ctx = new Context();
        ctx.setVariable("msg", "consistent");

        StringWriter w1 = new StringWriter();
        template.render(ctx, w1);
        StringWriter w2 = new StringWriter();
        template.render(ctx, w2);
        StringWriter w3 = new StringWriter();
        template.render(ctx, w3);

        assertEquals(w1.toString(), w2.toString(), "Second render should match first");
        assertEquals(w2.toString(), w3.toString(), "Third render should match second");
    }

    // --- Concurrent rendering correctness ---

    @Test
    void concurrentRenderingProducesCorrectResults() throws Exception {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t ->
                t.select("#message", (el, ctx) -> el.text(ctx.getString("msg"))));

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final String msg = "thread-" + i;
            futures.add(executor.submit(() -> {
                latch.countDown();
                latch.await(); // all threads start together
                Context ctx = new Context();
                ctx.setVariable("msg", msg);
                StringWriter writer = new StringWriter();
                template.render(ctx, writer);
                return writer.toString();
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        for (int i = 0; i < threadCount; i++) {
            String result = futures.get(i).get();
            // Use delimited marker to avoid partial matches (e.g. "thread-1" in "thread-10")
            String expectedMarker = ">thread-" + i + "<";
            assertTrue(result.contains(expectedMarker),
                    "Thread " + i + " output should contain '" + expectedMarker + "' but was: " + result);
            // Ensure no cross-thread contamination
            for (int j = 0; j < threadCount; j++) {
                if (j != i) {
                    String otherMarker = ">thread-" + j + "<";
                    assertFalse(result.contains(otherMarker),
                            "Thread " + i + " output should NOT contain '" + otherMarker + "'");
                }
            }
        }
    }

    @Test
    void concurrentSnippetRenderingIsIsolated() throws Exception {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t -> {
            Snippet snippet = engine.describe("META-INF/templates/index.html", "#message", s ->
                    s.root((el, ctx) -> el.text(ctx.getString("val"))));

            t.select("#message", (el, ctx) -> {
                el.empty();
                el.appendChild(snippet.render(ctx));
            });
        });

        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final String val = "snippet-" + i;
            futures.add(executor.submit(() -> {
                latch.countDown();
                latch.await();
                Context ctx = new Context();
                ctx.setVariable("val", val);
                StringWriter writer = new StringWriter();
                template.render(ctx, writer);
                return writer.toString();
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        for (int i = 0; i < threadCount; i++) {
            String result = futures.get(i).get();
            assertTrue(result.contains("snippet-" + i),
                    "Thread " + i + " should contain 'snippet-" + i + "'");
        }
    }

    // --- RenderingId lifecycle ---

    @Test
    void renderingIdReturnsZeroWhenEmpty() {
        // On a fresh thread, no push has been done
        assertEquals(0, RenderingId.get(), "RenderingId should return 0 when deque is empty");
    }

    @Test
    void renderingIdPushPopLifecycle() {
        assertEquals(0, RenderingId.get());

        int id1 = RenderingId.push();
        assertTrue(id1 > 0, "Pushed ID should be positive");
        assertEquals(id1, RenderingId.get());

        int id2 = RenderingId.push();
        assertTrue(id2 > id1, "Second ID should be greater than first");
        assertEquals(id2, RenderingId.get());

        int popped2 = RenderingId.pop();
        assertEquals(id2, popped2);
        assertEquals(id1, RenderingId.get(), "After popping, should see previous ID");

        int popped1 = RenderingId.pop();
        assertEquals(id1, popped1);
        assertEquals(0, RenderingId.get(), "After popping all, should return 0");
    }

    @Test
    void renderingIdIsThreadLocal() throws Exception {
        int mainId = RenderingId.push();

        Future<Integer> otherId = Executors.newSingleThreadExecutor().submit(() -> {
            assertEquals(0, RenderingId.get(), "Other thread should start with 0");
            return RenderingId.push();
        });

        assertNotEquals(mainId, otherId.get(), "Different threads should get different IDs");
        assertEquals(mainId, RenderingId.get(), "Main thread ID should be unchanged");

        RenderingId.pop();
    }

    // --- SlotManager lifecycle ---

    @Test
    void slotManagerCleansUpAfterRendering() {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t ->
                t.select("#message", (el, ctx) -> el.text("modified")));

        Context ctx = new Context();
        StringWriter writer = new StringWriter();
        template.render(ctx, writer);

        // Render completes without error and output is correct
        assertTrue(writer.toString().contains("modified"));

        // Render again to verify cleanup was proper (no stale state)
        StringWriter writer2 = new StringWriter();
        template.render(ctx, writer2);
        assertEquals(writer.toString(), writer2.toString(),
                "Repeated renders should produce identical output after SlotManager cleanup");
    }

    // --- Empty / edge-case templates ---

    @Test
    void renderTemplateWithNoProcessors() {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t -> {
            // no selectors registered
        });

        Context ctx = new Context();
        StringWriter writer = new StringWriter();
        template.render(ctx, writer);
        String result = writer.toString();

        assertTrue(result.contains("Moshas tutorial!"), "Original content should be preserved");
        assertTrue(result.contains("A message could go here."), "Unmodified elements should be intact");
    }

    @Test
    void renderWithEmptyContext() {
        MoshasEngine engine = new MoshasEngine();
        Template template = engine.describe("META-INF/templates/index.html", t ->
                t.select("#message", (el, ctx) -> el.text(ctx.getString("missing"))));

        Context ctx = new Context();
        StringWriter writer = new StringWriter();
        template.render(ctx, writer);
        // Should not throw; missing variable returns empty/null gracefully
        assertNotNull(writer.toString());
    }

    @Test
    void renderWithElementRemovalAndReAdd() {
        MoshasEngine engine = new MoshasEngine();
        AtomicInteger renderCount = new AtomicInteger(0);

        Template template = engine.describe("META-INF/templates/index.html", t ->
                t.select("#message", (el, ctx) -> {
                    el.empty();
                    el.text("render-" + renderCount.incrementAndGet());
                }));

        Context ctx = new Context();

        StringWriter w1 = new StringWriter();
        template.render(ctx, w1);
        assertTrue(w1.toString().contains("render-1"));

        StringWriter w2 = new StringWriter();
        template.render(ctx, w2);
        assertTrue(w2.toString().contains("render-2"));
        assertFalse(w2.toString().contains("render-1"),
                "Second render should not contain first render's content");
    }

    // --- Attributes edge cases ---

    @Test
    void attributesCloneIsIndependent() {
        Attributes original = new Attributes();
        original.put("class", "foo");
        original.put("id", "bar");

        Attributes cloned = original.clone();
        cloned.put("class", "changed");
        cloned.put("data-new", "added");

        assertEquals("foo", original.get("class"), "Original should be unchanged");
        assertFalse(original.hasKey("data-new"), "Original should not have new attribute");
        assertEquals("changed", cloned.get("class"));
        assertTrue(cloned.hasKey("data-new"));
    }

    @Test
    void emptyAttributesClone() {
        Attributes original = new Attributes();
        Attributes cloned = original.clone();

        assertEquals(0, cloned.size());
        cloned.put("key", "value");
        assertEquals(0, original.size(), "Original should remain empty");
    }

    @Test
    void attributesIteratorOnEmpty() {
        Attributes attrs = new Attributes();
        assertFalse(attrs.iterator().hasNext(), "Empty attributes should have no elements");
        assertTrue(attrs.asList().isEmpty());
    }

    @Test
    void attributesIteratorPreservesOrder() {
        Attributes attrs = new Attributes();
        attrs.put("z-attr", "1");
        attrs.put("a-attr", "2");
        attrs.put("m-attr", "3");

        List<String> keys = new ArrayList<>();
        for (Attribute attr : attrs) {
            keys.add(attr.getKey());
        }

        assertEquals(List.of("z-attr", "a-attr", "m-attr"), keys,
                "Iterator should preserve insertion order");
    }

    @Test
    void attributesCaseInsensitiveLookup() {
        Attributes attrs = new Attributes();
        attrs.put("Class", "foo");

        assertTrue(attrs.hasKey("class"), "Lookup should be case-insensitive");
        assertTrue(attrs.hasKey("CLASS"), "Lookup should be case-insensitive");
        assertEquals("foo", attrs.get("class"));
    }
}
