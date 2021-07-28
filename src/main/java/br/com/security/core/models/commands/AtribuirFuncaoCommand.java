package br.com.security.core.models.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AtribuirFuncaoCommand {

	private final Long idDoUsuario;
	private final Long idDaFuncao;
	private final Long idDoUsuarioCriado;
}
