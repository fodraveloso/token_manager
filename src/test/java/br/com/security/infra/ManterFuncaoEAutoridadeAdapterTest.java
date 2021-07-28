package br.com.security.infra;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import br.com.security.core.exceptions.NaoEPossivelCadastrarAutorizacaoException;
import br.com.security.core.exceptions.UsuarioNaoExisteException;
import br.com.security.core.models.commands.CadastrarAutorizacaoCommand;
import br.com.security.infra.entities.AuthorityEntity;
import br.com.security.infra.entities.UserEntity;
import br.com.security.infra.repositories.AuthorityRepository;
import br.com.security.infra.repositories.RoleRepository;
import br.com.security.infra.repositories.UserRepository;

@DataJpaTest
@Sql("/schema.sql")
@DisplayName("Infra: Manter Autorizacão, Função e Usuário")
class ManterFuncaoEAutoridadeAdapterTest {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	private ManterFuncaoEAutoridadeAdapter adapter;
	private Long mainUserId;

	@BeforeEach
	void setup() {

		this.adapter = new ManterFuncaoEAutoridadeAdapter(userRepository, roleRepository, authorityRepository);

		this.mainUserId = testEntityManager
				.persistAndFlush(UserEntity.builder().username("joao.veloso").password("123qwe!@#").build()).getId();
	}

	@Nested
	@DisplayName("Autorização")
	class Autorizacao {

		@Test
		@DisplayName("Tenta cadastrar autorização")
		void cadastrarAutorizacao_sucesso() {

			CadastrarAutorizacaoCommand command = new CadastrarAutorizacaoCommand("auth_adm_user", mainUserId);

			assertDoesNotThrow(() -> adapter.cadastrarAutorizacao(command));
		}

		@Test
		@DisplayName("Tenta cadastrar autorização com ID de usuário que não existe")
		void cadastrarAutorizacao_usuarioNaoExiste() {

			CadastrarAutorizacaoCommand command = new CadastrarAutorizacaoCommand("auth_adm_user", mainUserId + 1);

			assertThrows(UsuarioNaoExisteException.class, () -> adapter.cadastrarAutorizacao(command));
		}

		@Test
		@DisplayName("Tenta cadastrar autorizaçao já cadasrada")
		void cadastrarAutorizacao_comNomeJaCadastrado() {

			testEntityManager.persistAndFlush(
					new AuthorityEntity("auth_adm_user", testEntityManager.find(UserEntity.class, mainUserId)));
			testEntityManager.clear();

			CadastrarAutorizacaoCommand command = new CadastrarAutorizacaoCommand("auth_adm_user", mainUserId);
			
			assertThrows(NaoEPossivelCadastrarAutorizacaoException.class, () -> adapter.cadastrarAutorizacao(command));
		}
	}

	@Nested
	@DisplayName("Função")
	class Funcao {
		
	}
}