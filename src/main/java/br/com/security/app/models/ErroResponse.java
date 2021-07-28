package br.com.security.app.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErroResponse {

	private final LocalDateTime timestamp = LocalDateTime.now();
	private final Collection<String> erros = new ArrayList<>();
	private final Integer status;
	private final String erro;

	public ErroResponse(HttpStatus status) {
		this.status = status.value();
		this.erro = status.name();
	}
	
	public ErroResponse add(String mensagem) {
		
		this.erros.add(mensagem);
		return this;
	}
}