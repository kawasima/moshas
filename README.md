# Moshas

Moshas is a java template tool.


In Moshas, a template is a pure HTML file and a view logic is written by Java code. So it enables to separate logic from design completely.


Template is a pure HTML as follows:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Moshas tutorial!</title>
    </head>
    <body>
        <p>Hello world!</p>
        <p id="message">A message could go here.</p>
    </body>
</html>
```

And define a template as follows:

```java
MoshasEngine engine = new MoshasEngine();
Template indexTemplate = engine.defineTemplate("index", t -> {
    t.select("p#message", text("message"));
});
Context context = new Context();
context.setVariable("message", "We changed the message!");
indexTemplate.render(context, System.out);
```

## Manual

### MoshasEngine

Mo

### Template

The template is a.

```java

```

### Snippet

The snippet is a fragment of a template.
