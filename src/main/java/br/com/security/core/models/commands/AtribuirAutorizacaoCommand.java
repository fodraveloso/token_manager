package br.com.security.core.models.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AtribuirAutorizacaoCommand {

	private final Long idDaFuncao;
	private final Long idDaAutorizacao;
	private final Long idDoUsuario;
}
