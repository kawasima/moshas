package net.unit8.moshas.servlet;

import net.unit8.moshas.ServletMoshasEngineProvier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static net.unit8.moshas.RenderUtils.text;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author kawasima
 */
public class MoshasServletTest {
    @Test
    public void test() throws IOException, ServletException {
        MoshasServlet servlet = new MoshasServlet();
        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);

        when(config.getServletContext()).thenReturn(context);
        servlet.init(config);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/test.html");
        when(context.getResourceAsStream(anyString())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return ClassLoader.getSystemResourceAsStream(args[0].toString());
        });
        ServletMoshasEngineProvier.get().describe("/test.html", t -> {
            t.select("#message", (el, ctx) -> el.text("HELLO SERVLET"));
        });

        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        servlet.doGet(request, response);
        System.out.println(sw);
        Assert.assertTrue(sw.toString().contains("<title>Title</title>"));
    }
}
