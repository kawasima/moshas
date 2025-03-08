package net.unit8.moshas.servlet;

import net.unit8.moshas.ServletMoshasEngineProvier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author kawasima
 */
@ExtendWith(MockitoExtension.class)
public class MoshasServletTest {
    @Test
    void test() throws IOException, ServletException {
        MoshasServlet servlet = new MoshasServlet();
        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);

        when(config.getServletContext()).thenReturn(context);
        servlet.init(config);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/test.html");
        when(context.getResourceAsStream(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            return ClassLoader.getSystemResourceAsStream(path);
        });
        ServletMoshasEngineProvier.get().describe("/test.html", t -> {
            t.select("#message", (el, ctx) -> el.text("HELLO SERVLET"));
        });

        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        servlet.doGet(request, response);
        System.out.println(sw);
        assertTrue(sw.toString().contains("<title>Title</title>"));
    }
}
