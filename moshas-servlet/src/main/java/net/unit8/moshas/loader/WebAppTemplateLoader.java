package net.unit8.moshas.loader;

import jakarta.servlet.ServletContext;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Template loader implementation for web applications.
 * Loads templates from the web application context with security checks.
 * 
 * @author kawasima
 */
public class WebAppTemplateLoader extends TemplateLoader {
    private String prefix;
    private String suffix;
    private ServletContext servletContext;

    public WebAppTemplateLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private void validatePath(String path) {
        try {
            // Check URL decoded path for security
            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
            
            // Prevent backslash usage in paths
            if (decodedPath.contains("\\")) {
                throw new SecurityException("Backslashes are not allowed in paths: " + path);
            }

            // Normalize the path for security checks
            String normalizedPath = Paths.get(decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath)
                    .normalize()
                    .toString()
                    .replace('\\', '/');

            // Check for directory traversal attempts
            if (normalizedPath.startsWith("..") || normalizedPath.contains("/../") || normalizedPath.contains("/./")) {
                throw new SecurityException("Path traversal detected: " + path);
            }

            // Prevent access to WEB-INF and META-INF directories
            if (normalizedPath.toUpperCase().contains("WEB-INF") || 
                normalizedPath.toUpperCase().contains("META-INF")) {
                throw new SecurityException("Access to WEB-INF and META-INF is not allowed: " + path);
            }
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Invalid path: " + path, e);
        }
    }

    @Override
    public InputStream getTemplateStream(String templateSource) throws TemplateNotFoundException {
        if (templateSource == null) {
            throw new IllegalArgumentException("Template source cannot be null");
        }

        String processedPath = templateSource;
        if (prefix != null) {
            processedPath = prefix + processedPath;
        }
        if (suffix != null) {
            processedPath = processedPath + suffix;
        }

        // Perform security validation
        validatePath(processedPath);

        // Remove leading slash (safe operation after validation)
        if (processedPath.startsWith("/")) {
            processedPath = processedPath.substring(1);
        }

        InputStream is = servletContext.getResourceAsStream(processedPath);
        if (is == null) {
            throw new TemplateNotFoundException("Can't find template " + processedPath);
        }
        return is;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
