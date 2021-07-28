package br.com.security.core.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsuarioNaoExisteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Long idDoUsuario;
}
