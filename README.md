# Moshas

Moshas is a lightweight and flexible template engine for Java that enables complete separation of HTML and logic. It provides a clean and intuitive way to manipulate HTML templates using CSS selectors.

## Features

- **Clean Separation of Concerns**: Keep your HTML templates pure and logic-free
- **CSS Selectors**: Familiar and intuitive DOM manipulation using standard CSS selectors
- **Jakarta EE 10 Support**: Full compatibility with the latest Jakarta EE specifications
- **Fluent API**: Natural and easy-to-use API design
- **Scoped Variables**: Support for request, session, and application scopes
- **Type-Safe Operations**: Leverages Java's type system for safe template manipulation
- **Extensible**: Easy to customize with your own template loaders and manipulators

## Requirements

- Java 21 or higher
- Jakarta EE 10 or higher

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.unit8.moshas</groupId>
    <artifactId>moshas-servlet</artifactId>
    <version>0.3.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

### 1. Create an HTML Template

Create a pure HTML file without any special syntax or markers:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Moshas Tutorial</title>
    </head>
    <body>
        <p>Hello world!</p>
        <p id="message">A message could go here.</p>
    </body>
</html>
```

### 2. Configure the Servlet

Add Moshas servlet configuration to your `web.xml`:

```xml
<servlet>
    <servlet-name>moshas</servlet-name>
    <servlet-class>net.unit8.moshas.servlet.MoshasServlet</servlet-class>
    <init-param>
        <param-name>prefix</param-name>
        <param-value>/WEB-INF/templates</param-value>
    </init-param>
    <init-param>
        <param-name>suffix</param-name>
        <param-value>.html</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>moshas</servlet-name>
    <url-pattern>*.html</url-pattern>
</servlet-mapping>
```

### 3. Implement the Logic

Use Java code to manipulate your template:

```java
@WebServlet("/example")
public class ExampleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Define template manipulation
        ServletMoshasEngineProvier.get().describe("/index.html", t -> {
            t.select("#message", (el, ctx) -> el.text("Hello from Moshas!"));
        });
        
        // Process the template
        WebContext context = new WebContext(getServletContext(), req);
        MoshasEngine engine = ServletMoshasEngineProvier.get();
        engine.process("/index.html", context, resp.getWriter());
    }
}
```

## Advanced Usage

### Working with Scopes

Moshas provides three scope levels for variable management:

1. **RequestScope**: Access request-scoped variables
2. **SessionScope**: Access session-scoped variables
3. **ApplicationScope**: Access application-scoped variables

Example usage:

```java
// Setting scoped variables
request.setAttribute("message", "Hello!");
session.setAttribute("user", currentUser);
servletContext.setAttribute("config", appConfig);

// Using in templates
t.select("#message", (el, ctx) -> el.text(ctx.getString("message")));
t.select("#user", (el, ctx) -> el.text(ctx.getString("user", "name")));
```

### Template Loaders

Moshas comes with two built-in template loaders:

1. **WebAppTemplateLoader**: Loads templates from web application resources
2. **ResourceTemplateLoader**: Loads templates from the classpath

You can implement custom loaders by extending the `TemplateLoader` class:

```java
public class CustomTemplateLoader extends TemplateLoader {
    @Override
    public InputStream getTemplateStream(String templateSource) 
            throws TemplateNotFoundException {
        // Custom implementation
    }
}
```

### Template Manipulation Examples

```java
// Adding classes
t.select(".container", (el, ctx) -> el.addClass("active"));

// Modifying attributes
t.select("a", (el, ctx) -> el.attr("href", ctx.getString("url")));

// Conditional rendering
t.select(".admin-panel", (el, ctx) -> {
    if (!ctx.getBoolean("isAdmin")) {
        el.remove();
    }
});

// List rendering
t.select("#items", (el, ctx) -> {
    ctx.getCollection("items").forEach(item -> {
        el.append("<li>" + item + "</li>");
    });
});
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

We welcome contributions! Here's how you can help:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -am 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

If you encounter any issues or have questions:

1. Check the [documentation](https://github.com/kawasima/moshas/wiki)
2. Open an [issue](https://github.com/kawasima/moshas/issues)
3. Join our [discussions](https://github.com/kawasima/moshas/discussions)

## Author

- Yoshitaka Kawashima ([@kawasima](https://github.com/kawasima))

## Acknowledgments

Special thanks to all contributors who have helped make Moshas better!

