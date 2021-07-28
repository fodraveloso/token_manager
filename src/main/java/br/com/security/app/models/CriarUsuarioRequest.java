package br.com.security.app.models;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class CriarUsuarioRequest {

	private String username;
	private String password;
	private LocalDate validade;
}
