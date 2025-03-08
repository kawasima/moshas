package net.unit8.moshas.loader;

import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Security tests for WebAppTemplateLoader.
 * Tests various path traversal attack scenarios and valid path handling.
 */
@ExtendWith(MockitoExtension.class)
class WebAppTemplateLoaderSecurityTest {
    @Mock
    private ServletContext servletContext;

    private WebAppTemplateLoader loader;
    private static final String MOCK_CONTENT = "test content";

    @BeforeEach
    void setUp() {
        loader = new WebAppTemplateLoader(servletContext);
        // Mock ServletContext.getResourceAsStream with lenient mode
        lenient().when(servletContext.getResourceAsStream(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            // Return special content for paths containing "secret" to simulate protected resources
            if (path.contains("secret")) {
                return new ByteArrayInputStream("SECRET DATA".getBytes());
            }
            return new ByteArrayInputStream(MOCK_CONTENT.getBytes());
        });
    }

    @Test
    void testPathTraversalWithDotDot() {
        // Attempt to access WEB-INF/secrets directory using path traversal
        String maliciousPath = "../../WEB-INF/secrets/config.properties";
        assertThrows(SecurityException.class, () -> {
            loader.getTemplateStream(maliciousPath);
        });
    }

    @Test
    void testPathTraversalWithEncodedDotDot() {
        // Attempt path traversal using URL encoded characters
        String encodedPath = "..%2F..%2FWEB-INF/secret.txt";
        assertThrows(SecurityException.class, () -> {
            loader.getTemplateStream(encodedPath);
        });
    }

    @Test
    void testPathTraversalWithAbsolutePath() {
        // Test access with absolute path (normalized form)
        String absolutePath = "/templates/test.html";
        assertDoesNotThrow(() -> {
            InputStream is = loader.getTemplateStream(absolutePath);
            assertNotNull(is);
            assertEquals(MOCK_CONTENT, new String(is.readAllBytes()));
        });
    }

    @Test
    void testPathTraversalWithBackslashes() {
        // Attempt path traversal using backslashes
        String backslashPath = "..\\..\\WEB-INF\\secret.txt";
        assertThrows(SecurityException.class, () -> {
            loader.getTemplateStream(backslashPath);
        });
    }

    @Test
    void testValidTemplatePath() throws TemplateNotFoundException, IOException {
        // Test with a valid template path
        String validPath = "templates/valid.html";
        InputStream is = loader.getTemplateStream(validPath);
        assertNotNull(is);
        // Verify content
        byte[] content = is.readAllBytes();
        assertEquals(MOCK_CONTENT, new String(content));
    }

    @Test
    void testNormalizedPath() throws TemplateNotFoundException, IOException {
        // Test with a valid path that requires normalization
        String path = "templates/subfolder/valid.html";
        InputStream is = loader.getTemplateStream(path);
        assertNotNull(is);
        // Verify content
        byte[] content = is.readAllBytes();
        assertEquals(MOCK_CONTENT, new String(content));
    }
} 