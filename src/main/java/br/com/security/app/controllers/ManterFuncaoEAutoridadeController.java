package br.com.security.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.security.app.models.AtribuirAutorizacaoRequest;
import br.com.security.app.models.AtribuirFuncaoRequest;
import br.com.security.app.models.CriarAutoridadeRequest;
import br.com.security.app.models.CriarAutorizacaoResponse;
import br.com.security.app.models.CriarFuncaoRequest;
import br.com.security.app.models.CriarFuncaoResponse;
import br.com.security.app.models.CriarUsuarioRequest;
import br.com.security.app.models.CriarUsuarioResponse;
import br.com.security.app.models.ErroResponse;
import br.com.security.core.exceptions.AutorizaJaVinculadaAFuncaoException;
import br.com.security.core.exceptions.NaoEPossivelCadastrarAutorizacaoException;
import br.com.security.core.exceptions.DataDeValidadeNoPassadoException;
import br.com.security.core.exceptions.FuncaoJaCadastradaException;
import br.com.security.core.exceptions.UsuarioJaCadastradaException;
import br.com.security.core.models.commands.AtribuirAutorizacaoCommand;
import br.com.security.core.models.commands.AtribuirFuncaoCommand;
import br.com.security.core.models.commands.CadastrarAutorizacaoCommand;
import br.com.security.core.models.commands.CadastrarFuncaoCommand;
import br.com.security.core.models.commands.CadastrarUsuarioCommand;
import br.com.security.core.ports.incoming.AtribuirAutorizacao;
import br.com.security.core.ports.incoming.AtribuirFuncao;
import br.com.security.core.ports.incoming.CadastrarAutorizacao;
import br.com.security.core.ports.incoming.CadastrarFuncao;
import br.com.security.core.ports.incoming.CadastrarUsuario;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RequestMapping("/v1/security")
public class ManterFuncaoEAutoridadeController {

	private final CadastrarFuncao cadastrarFuncao;
	private final CadastrarUsuario cadastrarUsuario;
	private final CadastrarAutorizacao cadastrarAutorizacao;
	private final AtribuirAutorizacao atribuirAutorizacao;
	private final AtribuirFuncao atribuirFuncao;

	@PostMapping("/funcao")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('auth_adm_security')")
	public CriarFuncaoResponse criarFuncao(@RequestBody CriarFuncaoRequest request) {

		return new CriarFuncaoResponse(
				this.cadastrarFuncao.executar(new CadastrarFuncaoCommand(request.getRole(), 1L)));
	}

	@PostMapping("/autorizacao")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('auth_adm_security')")
	public CriarAutorizacaoResponse criarAutorizacao(@RequestBody CriarAutoridadeRequest request) {

		return new CriarAutorizacaoResponse(
				this.cadastrarAutorizacao.executar(new CadastrarAutorizacaoCommand(request.getNomeAutoridade(), 1L)));
	}

	@PostMapping("/usuario")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('auth_adm_user')")
	public CriarUsuarioResponse criarUsuario(@RequestBody CriarUsuarioRequest request) {

		return new CriarUsuarioResponse(this.cadastrarUsuario.executar(
				new CadastrarUsuarioCommand(request.getUsername(), request.getPassword(), 1L, request.getValidade())));
	}

	@PostMapping("/funcao/{id}/autorizacao")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('auth_adm_security')")
	public void atribuirAutorizacao(@PathVariable("id") Long id, @RequestBody AtribuirAutorizacaoRequest request) {

		this.atribuirAutorizacao.executar(new AtribuirAutorizacaoCommand(id, request.getIdDaAutorizacao(), 1L));
	}

	@PostMapping("/usuario/{id}/funcao")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('auth_adm_security')")
	public void atribuirFuncao(@PathVariable("id") Long id, @RequestBody AtribuirFuncaoRequest request) {

		this.atribuirFuncao.executar(new AtribuirFuncaoCommand(request.getIdDoUsuario(), id, 1L));
	}

	@ExceptionHandler(FuncaoJaCadastradaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErroResponse funcaoJaCadastrada(FuncaoJaCadastradaException exception) {

		return new ErroResponse(HttpStatus.BAD_REQUEST)
				.add(String.format("A função '%s' já esta cadastrada", exception.getNomeFuncao()));
	}

	@ExceptionHandler(NaoEPossivelCadastrarAutorizacaoException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErroResponse autorizacaoJaCadastrada(NaoEPossivelCadastrarAutorizacaoException exception) {

		return new ErroResponse(HttpStatus.BAD_REQUEST)
				.add(String.format("A autorização '%s' já esta cadastrada", exception.getAutorizacao()));
	}

	@ExceptionHandler(UsuarioJaCadastradaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErroResponse usuarioJaCadastrado(UsuarioJaCadastradaException exception) {

		return new ErroResponse(HttpStatus.BAD_REQUEST)
				.add(String.format("O usuário '%s' já esta cadastrada", exception.getCodigoUsuario()));
	}

	@ExceptionHandler(AutorizaJaVinculadaAFuncaoException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErroResponse autorizacaoJaVinculadaException(AutorizaJaVinculadaAFuncaoException exception) {

		return new ErroResponse(HttpStatus.BAD_REQUEST)
				.add(String.format("A atribuição '%s' já esta vinculada a função '%s'", exception.getNomeAutorizacao(),
						exception.getNomeFuncao()));
	}

	@ExceptionHandler(DataDeValidadeNoPassadoException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErroResponse dataDeValidadeNoPassadoException(DataDeValidadeNoPassadoException exception) {

		return new ErroResponse(HttpStatus.BAD_REQUEST).add(
				String.format("A data de validade fornecida '%s' esta no passado", exception.getData().toString()));
	}
}
