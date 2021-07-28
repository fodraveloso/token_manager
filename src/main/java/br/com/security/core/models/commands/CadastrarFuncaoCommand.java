package br.com.security.core.models.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CadastrarFuncaoCommand {

	private final String funcao;
	private final Long userId;
}
