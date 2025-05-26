package hr.algebra.webshop.filter;

import hr.algebra.webshop.repository.BlockedIpRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BlockedIpFilter implements Filter {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BlockedIpFilter.class);
    private final BlockedIpRepository blockedIpRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String ip = httpRequest.getRemoteAddr();
        logger.info("Provjera IP adrese: {}", ip);
        if (blockedIpRepository.existsByIpAddress(ip)) {
            logger.warn("Blokiran pristup za IP adresu: {}", ip);
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Pristup zabranjen");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
