package br.com.security.core.models.commands;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CadastrarUsuarioCommand {

	private final String username;
	private final String password;
	private final Long idDoUsuario;
	private final LocalDate validade;
}
