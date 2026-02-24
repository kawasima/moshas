package net.unit8.moshas.cache;

import net.unit8.moshas.DefaultTemplate;
import net.unit8.moshas.Template;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LruTemplateCacheTest {

    private Template createDummyTemplate(String html) {
        try {
            InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
            // Use reflection to access the protected constructor
            var ctor = DefaultTemplate.class.getDeclaredConstructor(InputStream.class);
            ctor.setAccessible(true);
            return ctor.newInstance(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getReturnsNullForMissingKey() {
        LruTemplateCache cache = new LruTemplateCache();
        assertNull(cache.getTemplate("nonexistent"));
    }

    @Test
    void putAndGet() {
        LruTemplateCache cache = new LruTemplateCache();
        Template t = createDummyTemplate("<html><body>hello</body></html>");
        cache.putTemplate("test", t);
        assertSame(t, cache.getTemplate("test"));
    }

    @Test
    void evictsLeastRecentlyUsed() {
        LruTemplateCache cache = new LruTemplateCache(3);

        Template t1 = createDummyTemplate("<html><body>1</body></html>");
        Template t2 = createDummyTemplate("<html><body>2</body></html>");
        Template t3 = createDummyTemplate("<html><body>3</body></html>");
        Template t4 = createDummyTemplate("<html><body>4</body></html>");

        cache.putTemplate("t1", t1);
        cache.putTemplate("t2", t2);
        cache.putTemplate("t3", t3);

        // All three should be present
        assertSame(t1, cache.getTemplate("t1"));
        assertSame(t2, cache.getTemplate("t2"));
        assertSame(t3, cache.getTemplate("t3"));

        // Adding t4 should evict t1 (least recently used, since we accessed t1 first above)
        // After the gets above, access order is t1, t2, t3. t1 was accessed earliest.
        // Actually after gets: t1 accessed, then t2, then t3 - so t1 is LRU
        cache.putTemplate("t4", t4);

        assertNull(cache.getTemplate("t1"), "t1 should have been evicted as LRU");
        assertSame(t2, cache.getTemplate("t2"));
        assertSame(t3, cache.getTemplate("t3"));
        assertSame(t4, cache.getTemplate("t4"));
    }

    @Test
    void accessOrderAffectsEviction() {
        LruTemplateCache cache = new LruTemplateCache(3);

        Template t1 = createDummyTemplate("<html><body>1</body></html>");
        Template t2 = createDummyTemplate("<html><body>2</body></html>");
        Template t3 = createDummyTemplate("<html><body>3</body></html>");
        Template t4 = createDummyTemplate("<html><body>4</body></html>");

        cache.putTemplate("t1", t1);
        cache.putTemplate("t2", t2);
        cache.putTemplate("t3", t3);

        // Access t1 to make it recently used; t2 becomes LRU
        cache.getTemplate("t1");

        cache.putTemplate("t4", t4);

        assertSame(t1, cache.getTemplate("t1"), "t1 should survive (recently accessed)");
        assertNull(cache.getTemplate("t2"), "t2 should have been evicted as LRU");
        assertSame(t3, cache.getTemplate("t3"));
        assertSame(t4, cache.getTemplate("t4"));
    }

    @Test
    void maxSizeOne() {
        LruTemplateCache cache = new LruTemplateCache(1);

        Template t1 = createDummyTemplate("<html><body>1</body></html>");
        Template t2 = createDummyTemplate("<html><body>2</body></html>");

        cache.putTemplate("t1", t1);
        assertSame(t1, cache.getTemplate("t1"));

        cache.putTemplate("t2", t2);
        assertNull(cache.getTemplate("t1"), "t1 should have been evicted");
        assertSame(t2, cache.getTemplate("t2"));
    }

    @Test
    void overwriteExistingKey() {
        LruTemplateCache cache = new LruTemplateCache(3);

        Template t1 = createDummyTemplate("<html><body>1</body></html>");
        Template t1Updated = createDummyTemplate("<html><body>1-updated</body></html>");

        cache.putTemplate("t1", t1);
        cache.putTemplate("t1", t1Updated);

        assertSame(t1Updated, cache.getTemplate("t1"));
    }

    @Test
    void invalidMaxSizeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new LruTemplateCache(0));
        assertThrows(IllegalArgumentException.class, () -> new LruTemplateCache(-1));
    }

    @Test
    void defaultConstructorAllowsManyEntries() {
        LruTemplateCache cache = new LruTemplateCache();
        // Default size is 256, put more than a few to ensure it works
        for (int i = 0; i < 100; i++) {
            Template t = createDummyTemplate("<html><body>" + i + "</body></html>");
            cache.putTemplate("t" + i, t);
        }
        // All 100 should still be in cache (well under 256 limit)
        for (int i = 0; i < 100; i++) {
            assertNotNull(cache.getTemplate("t" + i), "t" + i + " should be in cache");
        }
    }
}
