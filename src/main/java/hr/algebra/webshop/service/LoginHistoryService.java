package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.LoginHistoryDto;
import hr.algebra.webshop.model.LoginHistory;
import hr.algebra.webshop.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;

    public void logLogin(String username, String ipAddress) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUsername(username);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setLoginTime(LocalDateTime.now());
        loginHistoryRepository.save(loginHistory);
    }

    public List<LoginHistoryDto> getFilteredHistory(String username, LocalDateTime start, LocalDateTime end) {
        return loginHistoryRepository.findFiltered(username, start, end)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<LoginHistoryDto> getAllLogins() {
        return loginHistoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private LoginHistoryDto toDto(LoginHistory loginHistory) {
        return new LoginHistoryDto(
                loginHistory.getUsername(),
                loginHistory.getLoginTime(),
                loginHistory.getIpAddress()
        );
    }
}
