package hr.algebra.webshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotBlank(message = "Korisničko ime je obavezno")
    @Size(min = 3, max = 20, message = "Korisničko ime mora imati između 3 i 20 znakova")
    private String username;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 8, message = "Lozinka mora imati najmanje 8 znakova")
    private String password;
}
