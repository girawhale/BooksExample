package tacos.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Component
@ConfigurationProperties(prefix = "taco.orders")
@Data
public class OrderProps {
    @Min(value = 5, message = "5 <= pageSize <= 25")
    @Max(value = 25, message = "5 <= pageSize <= 25")
    private int pageSize = 20;
}
