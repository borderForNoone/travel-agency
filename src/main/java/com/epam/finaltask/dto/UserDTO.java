package com.epam.finaltask.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.Voucher;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private String id;

	private String email;

	@NotBlank(message = "Password cannot be empty")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
			message = "Your password must contain upper and lower case letters and numbers, " +
					"at least 7 and maximum 30 characters." +
					"Password cannot contains spaces")
	private String password;

	@NotBlank(message = "Username cannot be blank")
	@Pattern(
			regexp = "^[a-zA-Z0-9]+$",
			message = "Username must contain only characters and numbers"
	)
	private String username;

	private Role role;

	private List<VoucherDTO> vouchers;

	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(
			regexp = "^[0-9]+$",
			message = "Phone number must contain only numbers"
	)
	private String phoneNumber;

	@NotNull(message = "Balance is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Balance must be zero or positive")
	private BigDecimal balance;

	private boolean active;
}
