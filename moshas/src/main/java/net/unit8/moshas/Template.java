package net.unit8.moshas;

/**
 * Represents a template in the Moshas template engine.
 * A template is a specialized type of snippet that can be loaded, cached, and processed
 * by the template engine. Templates are typically HTML documents that can be manipulated
 * using CSS selectors to modify their content dynamically.
 * 
 * <p>Templates support the following features:</p>
 * <ul>
 *   <li>DOM-based manipulation using CSS selectors</li>
 *   <li>Caching for improved performance</li>
 *   <li>Context-aware rendering</li>
 *   <li>Safe HTML output generation</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * Template template = engine.describe("template.html", t -> {
 *     t.select("#message", (el, ctx) -> el.text("Hello, World!"));
 * });
 * template.render(context, writer);
 * </pre>
 * 
 * @author kawasima
 * @see Snippet
 * @see MoshasEngine
 */
public interface Template extends Snippet {
}
