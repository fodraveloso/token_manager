package br.com.security.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.security.core.exceptions.DataDeValidadeNoPassadoException;
import br.com.security.core.models.commands.AtribuirAutorizacaoCommand;
import br.com.security.core.models.commands.AtribuirFuncaoCommand;
import br.com.security.core.models.commands.CadastrarAutorizacaoCommand;
import br.com.security.core.models.commands.CadastrarFuncaoCommand;
import br.com.security.core.models.commands.CadastrarUsuarioCommand;
import br.com.security.core.ports.outgoing.AutorizacaoPersistence;
import br.com.security.core.ports.outgoing.FuncaoPersistence;
import br.com.security.core.ports.outgoing.UsuarioPersistence;

@ExtendWith(MockitoExtension.class)
@DisplayName("Core: Manter Autorizacão, Função e Usuário")
class ManterFuncaoEAutoridadeFacadeTest {

	@Mock
	private AutorizacaoPersistence autorizacaoPersistence;
	@Mock
	private FuncaoPersistence funcaoPersistence;
	@Mock
	private UsuarioPersistence usuarioPersistence;

	@InjectMocks
	private ManterFuncaoEAutoridadeFacade facade;

	@Nested
	@DisplayName("Função")
	class Funcao {
		
		@Test
		@DisplayName("Tenta cadastrar função")
		void cadastrarFuncao_sucesso() {

			doReturn(1L).when(funcaoPersistence).cadastrarFuncao(any(CadastrarFuncaoCommand.class));

			Long executar = facade.executar(new CadastrarFuncaoCommand("ROLE_ENG_MAT", 1L));

			assertEquals(1L, executar);
		}
	}
	
	@Nested
	@DisplayName("Autorização")
	class Autorizacao {
		
		@Test
		@DisplayName("Tenta cadastrar autorização")
		void cadastrarAutorizacao_sucesso() {

			CadastrarAutorizacaoCommand command = new CadastrarAutorizacaoCommand("auth_adm", 1L);

			doReturn(1L).when(autorizacaoPersistence).cadastrarAutorizacao(any(CadastrarAutorizacaoCommand.class));

			Long executar = facade.executar(command);

			assertEquals(1L, executar);
		}
		
		@Test
		@DisplayName("Tenra atribuir autorização a função")
		void atribuirAutorizacaoAFuncao_sucesso() {
			
			AtribuirAutorizacaoCommand command = new AtribuirAutorizacaoCommand(1L, 1L, 1L);
			
			assertDoesNotThrow(() -> facade.executar(command));
		}
	}

	@Nested
	@DisplayName("Usuário")
	class Usuario {
		
		@Test
		@DisplayName("Tenta cadastrar usuário")
		void cadastrarUsuario_sucesso() {

			CadastrarUsuarioCommand command = new CadastrarUsuarioCommand("joao.veloso", "123qwe!@#", 1L,
					LocalDate.now().plusYears(1));

			doReturn(1L).when(usuarioPersistence).cadastrarUsuario(any(CadastrarUsuarioCommand.class));

			Long executar = facade.executar(command);

			assertEquals(1L, executar);
		}
		
		@Test
		@DisplayName("Tenta cadastrar usuário com validade no passado")
		void cadastrarUsuario_comValidadeNoPassado() {
			
			CadastrarUsuarioCommand command = new CadastrarUsuarioCommand("joao.veloso", "123qwe!@#", 1L,
					LocalDate.now().minusWeeks(1));
			
			assertThrows(DataDeValidadeNoPassadoException.class, () -> facade.executar(command));
		}
		
		
		@Test
		@DisplayName("Tenta atribuir função a usuário")
		void atribuirFuncaoAUsuario_sucesso() {
			
			AtribuirFuncaoCommand command = new AtribuirFuncaoCommand(2L, 1L, 1L);
			
			assertDoesNotThrow(() -> facade.executar(command));
		}
	}
}
