package hr.algebra.webshop.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class BlockedIpFilter implements Filter {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BlockedIpFilter.class);
    private static final Set<String> BLOCKED_IPS = Set.of(); //"127.0.0.1", "0:0:0:0:0:0:0:1"

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String ip = httpRequest.getRemoteAddr();
        logger.info("Provjera IP adrese: {}", ip);
        if (BLOCKED_IPS.contains(ip)) {
            logger.warn("Blokiran pristup za IP adresu: {}", ip);
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Pristup zabranjen");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
