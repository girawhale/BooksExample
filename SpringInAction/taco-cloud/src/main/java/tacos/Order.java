package tacos;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class Order {
    @NotBlank(message = "Name Blank")
    private String deliveryName;

    @NotBlank(message = "Street Blank")
    private String deliveryStreet;

    @NotBlank(message = "City Blank")
    private String deliveryCity;

    @NotBlank(message = "State Blank")
    private String deliveryState;

    @NotBlank(message = "Zip Code Blank")
    private String deliveryZip;

    @CreditCardNumber(message = "Not Valid Credit Card Number")
    private String ccNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$")
    private String ccExpiration;

    @Digits(integer = 3, fraction = 0, message = "Invalid CVV")
    private String ccCVV;
}
