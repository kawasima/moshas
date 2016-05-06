package net.unit8.moshas.servlet;

import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

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
        try (InputStream is = getClass().getResourceAsStream(request.getServletPath())) {
            when(context.getResourceAsStream(anyString())).thenReturn(is);

            HttpServletResponse response = mock(HttpServletResponse.class);
            StringWriter sw = new StringWriter();
            when(response.getWriter()).thenReturn(new PrintWriter(sw));
            servlet.doGet(request, response);

            Assert.assertTrue(sw.toString().contains("<title>Title</title>"));
        }
    }
}
