package tacos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class Taco {

    @NotNull
    @Size(min = 5, message = "Name은 최소 5자 이상이어야 합니다.")
    private String name;

    @Size(min = 1, message = "최소 재료는 1개 이상입니다.")
    private List<String> ingredients;
}
