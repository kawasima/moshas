# Moshas

Moshas is a java template tool.

If you use moshas, you can separate logic from HTML completely.

A template is a pure HTML as follows:

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

And define the view logic as follows:

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

MoshasEngine processes rendering templates.  

```java
MoshasEngine moshas = new MoshasEngine();
```

### Template

The template class represents a pure HTML template file and binds view logic.

HTML template file is very simple as follows:

```html
<html>
<body>
  <p id="msg">Replace here!</p>
</body>
</html>
```

Describes view logic to MoshasEngine. `select` method defines a selector and a manipulation of the selected element. 

```java
moshas.describe("template", t -> {
    t.select("msg", (el, ctx) -> el.text(ctx.getString("msg")));
});
```

To render the template as follows: 

```java
Context ctx = new Context();
ctx.setVariable("msg", "Hello world!");
moshas.process("template", ctx);
```

And then moshas engine renders as follows:  

```
<html>
<body>
  <p id="msg">Hello world!</p>
</body>
</html>
```
