package br.com.security.infra;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import br.com.security.core.exceptions.NaoEPossivelCadastrarAutorizacaoException;
import br.com.security.core.exceptions.UsuarioNaoExisteException;
import br.com.security.core.models.commands.AtribuirAutorizacaoCommand;
import br.com.security.core.models.commands.AtribuirFuncaoCommand;
import br.com.security.core.models.commands.CadastrarAutorizacaoCommand;
import br.com.security.core.models.commands.CadastrarFuncaoCommand;
import br.com.security.core.models.commands.CadastrarUsuarioCommand;
import br.com.security.core.ports.outgoing.AutorizacaoPersistence;
import br.com.security.core.ports.outgoing.FuncaoPersistence;
import br.com.security.core.ports.outgoing.UsuarioPersistence;
import br.com.security.infra.entities.AuthorityEntity;
import br.com.security.infra.entities.UserEntity;
import br.com.security.infra.repositories.AuthorityRepository;
import br.com.security.infra.repositories.RoleRepository;
import br.com.security.infra.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManterFuncaoEAutoridadeAdapter implements AutorizacaoPersistence, FuncaoPersistence, UsuarioPersistence {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final AuthorityRepository authorityRepository;

	@Override
	public Long cadastrarUsuario(CadastrarUsuarioCommand command) {
		return null;
	}

	@Override
	public void atribuirFuncao(AtribuirFuncaoCommand command) {
		// TODO Auto-generated method stub

	}

	@Override
	public Long cadastrarFuncao(CadastrarFuncaoCommand command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void atribuirAutorizacao(AtribuirAutorizacaoCommand command) {
		// TODO Auto-generated method stub

	}

	@Override
	public Long cadastrarAutorizacao(CadastrarAutorizacaoCommand command) {

		UserEntity user = userRepository.findById(command.getIdDoUsuario())
				.orElseThrow(() -> new UsuarioNaoExisteException(command.getIdDoUsuario()));

		try {
			
			return authorityRepository.saveAndFlush(new AuthorityEntity(command.getNomeAutoridade(), user)).getId();
		} catch (DataIntegrityViolationException e) {

			throw new NaoEPossivelCadastrarAutorizacaoException(command.getNomeAutoridade());
		}
	}
}