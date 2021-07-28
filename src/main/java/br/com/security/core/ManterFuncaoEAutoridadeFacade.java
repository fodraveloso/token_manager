package br.com.security.core;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.com.security.core.exceptions.DataDeValidadeNoPassadoException;
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
import br.com.security.core.ports.outgoing.AutorizacaoPersistence;
import br.com.security.core.ports.outgoing.FuncaoPersistence;
import br.com.security.core.ports.outgoing.UsuarioPersistence;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManterFuncaoEAutoridadeFacade
		implements CadastrarAutorizacao, CadastrarFuncao, CadastrarUsuario, AtribuirFuncao, AtribuirAutorizacao {

	private final AutorizacaoPersistence autorizacaoPersistence;
	private final FuncaoPersistence funcaoPersistence;
	private final UsuarioPersistence usuarioPersistence;

	@Override
	public void executar(AtribuirAutorizacaoCommand command) {
		
		funcaoPersistence.atribuirAutorizacao(command);
	}

	@Override
	public void executar(AtribuirFuncaoCommand command) {

		usuarioPersistence.atribuirFuncao(command);
	}

	@Override
	public Long executar(CadastrarUsuarioCommand command) {

		if (command.getValidade().isBefore(LocalDate.now())) {
			
			throw new DataDeValidadeNoPassadoException(command.getValidade());
		}

		return usuarioPersistence.cadastrarUsuario(command);
	}

	@Override
	public Long executar(CadastrarFuncaoCommand command) {

		return funcaoPersistence.cadastrarFuncao(command);
	}

	@Override
	public Long executar(CadastrarAutorizacaoCommand command) {

		return autorizacaoPersistence.cadastrarAutorizacao(command);
	}
}
