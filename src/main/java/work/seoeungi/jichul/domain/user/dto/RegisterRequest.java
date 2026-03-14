package work.seoeungi.jichul.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    String password,

    @NotBlank @Size(max = 100, message = "이름은 100자 이하이어야 합니다.")
    String name
) {

}
