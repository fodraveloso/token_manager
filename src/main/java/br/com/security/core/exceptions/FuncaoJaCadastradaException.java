package br.com.security.core.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FuncaoJaCadastradaException extends RuntimeException {

	private final String nomeFuncao;
	
	private static final long serialVersionUID = 1L;

}
