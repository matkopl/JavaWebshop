package hr.algebra.webshop.filter;

import hr.algebra.webshop.service.LoginHistoryService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class LoginFilter implements Filter {
    private final LoginHistoryService loginHistoryService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        if ("/login".equals(httpRequest.getRequestURI())
                && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String username = httpRequest.getParameter("username");
            String ip = httpRequest.getRemoteAddr();

            if (username != null && !username.isEmpty()) {
                loginHistoryService.logLogin(username, ip);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
