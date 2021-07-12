package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
	
	public TokenDto(String jwt) {
		this.token = jwt;
	}

	private String token;
}
