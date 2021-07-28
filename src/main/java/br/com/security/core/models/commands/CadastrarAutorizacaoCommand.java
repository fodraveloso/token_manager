package br.com.security.core.models.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CadastrarAutorizacaoCommand {

	private final String nomeAutoridade;
	private final Long idDoUsuario;
}
