package hr.algebra.webshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginHistoryDto {
    private String username;
    private LocalDateTime loginTime;
    private String ipAddress;
    private String status;
}
