package hr.algebra.webshop.listener;

import hr.algebra.webshop.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAttemptListener {

    private final LoginHistoryService loginHistoryService;

    @EventListener
    private void handleSuccess(AuthenticationSuccessEvent successEvent) {
        Authentication authentication = successEvent.getAuthentication();
        if (authentication.getDetails() instanceof WebAuthenticationDetails details) {
            loginHistoryService.logLogin(authentication.getName(), details.getRemoteAddress(), "SUCCESS");
        }
    }

    @EventListener
    private void handleFailure(AuthenticationFailureBadCredentialsEvent failureEvent) {
        Object principal = failureEvent.getAuthentication().getPrincipal();
        String username = principal instanceof String ? (String) principal : "UNKNOWN USER";
        Object details = failureEvent.getAuthentication().getDetails();
        String ip = details instanceof WebAuthenticationDetails ? ((WebAuthenticationDetails) details).getRemoteAddress() : "UNKNOWN IP";
        loginHistoryService.logLogin(username, ip, "FAILURE");
    }

}
