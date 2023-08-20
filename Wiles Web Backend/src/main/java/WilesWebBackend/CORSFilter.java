package WilesWebBackend;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {

    /**
     * CORS filter for http-request and response
     */
    public CORSFilter() {
        super();
    }

    List<String> allowedOrigins = List.of("http://wiles.costea.in", "http://localhost:4200");

    /**
     * Do Filter on every http-request.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String domain = request.getHeader("origin");
        if(domain!=null && allowedOrigins.contains(domain)) {
            response.setHeader("Access-Control-Allow-Origin", domain);
        }
        response.setHeader("Access-Control-Allow-Methods", "PUT, OPTIONS");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "access_token, authorization, content-type, x-xsrf-token");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    /**
     * Destroy method
     */
    @Override
    public void destroy() {
    }

    /**
     * Initialize CORS filter 
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}