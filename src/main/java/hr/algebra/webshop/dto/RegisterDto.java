package hr.algebra.webshop.dto;

import hr.algebra.webshop.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto extends LoginDto {
    @NotBlank(message = "Email je obavezan")
    @Email(message = "Neispravan email format")
    private String email;

    private Role role = Role.USER;
}
