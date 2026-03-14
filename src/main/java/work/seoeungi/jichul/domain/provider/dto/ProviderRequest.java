package work.seoeungi.jichul.domain.provider.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProviderRequest(
    @NotBlank(message = "제공사 이름을 입력해주세요.")
    @Size(max = 100, message = "제공사 이름은 100자 이하이어야 합니다.")
    String name
) {

}
