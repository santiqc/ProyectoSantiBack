package com.proyecto.santi.emailpassword.dto;

import javax.validation.constraints.NotBlank;

public class ChangePasswordDTO {
	
	@NotBlank
	private String password;
	@NotBlank
	private String confirmPassword;
	@NotBlank
	private String tokenPassword;
	
	public ChangePasswordDTO() {
		super();
	}

	public ChangePasswordDTO(@NotBlank String password, @NotBlank String confirmPassword,
			@NotBlank String tokenPassword) {
		super();
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.tokenPassword = tokenPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getTokenPassword() {
		return tokenPassword;
	}

	public void setTokenPassword(String tokenPassword) {
		this.tokenPassword = tokenPassword;
	}
	
}