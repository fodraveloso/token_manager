package br.com.security.core.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AutorizaJaVinculadaAFuncaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String nomeFuncao;
	private final String nomeAutorizacao;
}
