package br.com.security.core.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsuarioJaCadastradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String codigoUsuario;

}
