package hr.algebra.webshop.listener;

import hr.algebra.webshop.service.LoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class LoginAttemptListener {

    private final LoginHistoryService loginHistoryService;

    @EventListener
    private void handleSuccess(AuthenticationSuccessEvent successEvent) {
        String ip = getClientIpAddress();
        Authentication authentication = successEvent.getAuthentication();
        loginHistoryService.logLogin(authentication.getName(), ip, "SUCCESS");
    }

    @EventListener
    private void handleFailure(AuthenticationFailureBadCredentialsEvent failureEvent) {
        Object principal = failureEvent.getAuthentication().getPrincipal();
        String username = principal instanceof String ? (String) principal : "UNKNOWN USER";
        String ip = getClientIpAddress();
        loginHistoryService.logLogin(username, ip, "FAILURE");
    }


    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "UNKNOWN IP";

        HttpServletRequest request = attributes.getRequest();
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
