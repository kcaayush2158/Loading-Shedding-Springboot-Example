import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InflightRequestFilter implements Filter {
    private final int maxRequests = 3;
    private AtomicInteger currentRequest=  new AtomicInteger(0);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(currentRequest.get() >= maxRequests){
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            byte[] byteMessage = "Server Unavailable. please retry after sometime".getBytes(StandardCharsets.UTF_8);
            httpServletResponse.getOutputStream().write(byteMessage);
            return;
        }

        currentRequest.incrementAndGet();
        try {
            filterChain.doFilter(servletRequest,servletResponse);
        }finally {
            currentRequest.decrementAndGet();
        }

    }
}
