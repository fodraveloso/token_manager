package br.com.security.core.exceptions;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DataDeValidadeNoPassadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final LocalDate data;
}
