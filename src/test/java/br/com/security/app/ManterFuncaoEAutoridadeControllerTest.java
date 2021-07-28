package br.com.security.app;

import static br.com.security.JsonCreator.startJson;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

@SpringBootTest
@DisplayName("App: Manter Autorizacão, Função e Usuário")
class ManterFuncaoEAutoridadeControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@MockBean
	private CadastrarFuncao cadastrarFuncao;
	@MockBean
	private CadastrarAutorizacao cadastrarAutorizacao;
	@MockBean
	private CadastrarUsuario cadastrarUsuario;
	@MockBean
	private AtribuirAutorizacao atribuirAutorizacao;
	@MockBean
	private AtribuirFuncao atribuirFuncao;

	@Value("${api.header-name}")
	private String headerName;

	private MockMvc mockMvc;
	private String tokenRolesAndAuthorities;
	private String tokenUserCreator;

	@BeforeEach
	void setup() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

		this.tokenRolesAndAuthorities = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(
						startJson().name("username").value("admin").name("password").value("password").endJson()))
				.andReturn().getResponse().getContentAsString();

		this.tokenUserCreator = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(
						startJson().name("username").value("principal").name("password").value("password").endJson()))
				.andReturn().getResponse().getContentAsString();
	}

	@Test
	@DisplayName("Tenta obter token com código de usuário inválido")
	void tentaGerarTokenUsuarioErrado() throws Exception {

		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(
						startJson().name("username").value("nao_existe").name("password").value("password").endJson()))
				.andExpect(status().isUnauthorized());
	}

	@Nested
	@DisplayName("Função")
	class FuncaoTest {

		@Test
		@DisplayName("Tenta cadastrar nova função")
		void cadastrarFuncao_sucesso() throws Exception {

			doReturn(1L).when(cadastrarFuncao).executar(any(CadastrarFuncaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao")
					.header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("role").value("ROLE_ENG_MAT").endJson())).andExpect(status().isCreated())
					.andExpect(jsonPath("id").value("1"));
		}

		@Test
		@DisplayName("Tenta cadastrar função com token sem autorização")
		void cadastrarFuncao_tokenNaoAutorizado() throws Exception {

			doReturn(1L).when(cadastrarFuncao).executar(any(CadastrarFuncaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao").contentType(MediaType.APPLICATION_JSON)
					.header(headerName, tokenUserCreator)
					.content(startJson().name("role").value("ROLE_ENG_MAT").endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta cadastrar função sem token")
		void cadastrarFuncao_semToken() throws Exception {

			doReturn(1L).when(cadastrarFuncao).executar(any(CadastrarFuncaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao").contentType(MediaType.APPLICATION_JSON)
					.content(startJson().name("role").value("ROLE_ENG_MAT").endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta cadastrar função já cadastrada")
		void cadastrarFuncao_jaCadastrado() throws Exception {

			final String FUNCAO = "ROLE_ENG_MAT";

			doThrow(new FuncaoJaCadastradaException(FUNCAO)).when(cadastrarFuncao)
					.executar(any(CadastrarFuncaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao").contentType(MediaType.APPLICATION_JSON)
					.header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("role").value(FUNCAO).endJson())).andExpect(status().isBadRequest())
					.andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(jsonPath("status").value("400"))
					.andExpect(jsonPath("erro").value(HttpStatus.BAD_REQUEST.name())).andExpect(jsonPath("erros[0]")
							.value(containsString(String.format("A função '%s' já esta cadastrada", FUNCAO))));
		}

		@Test
		@DisplayName("Tenta atribuir função a usuário")
		void atribuirFuncao_sucesso() throws Exception {

			doNothing().when(atribuirFuncao).executar(any(AtribuirFuncaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario/1/funcao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("idDaFuncao").value(1).endJson())).andExpect(status().isCreated());
		}

		@Test
		@DisplayName("Tenta atribuir função a usuário sem token")
		void atribuirAutorizacaoAFuncao_SemToken() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario/1/funcao")
					.contentType(MediaType.APPLICATION_JSON).content(startJson().name("idDaFuncao").value(1).endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta atribuir funcão à usuário com token sem permissão")
		void atribuirAutorizacaoAFuncao_tokenSemPermissao() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario/1/funcao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenUserCreator)
					.contentType(MediaType.APPLICATION_JSON).content(startJson().name("idDaFuncao").value(1).endJson()))
					.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("Autorização")
	class AutorizacaoTest {

		@Test
		@DisplayName("Tenta cadastrar autorização")
		void cadastrarAutoridade_sucesso() throws Exception {

			doReturn(1L).when(cadastrarAutorizacao).executar(any(CadastrarAutorizacaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/autorizacao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("nomeAutoridade").value("auth_cadastrar_material").endJson()))
					.andExpect(status().isCreated()).andExpect(jsonPath("id").value("1"));
		}

		@Test
		@DisplayName("Tenta cadastrar autorização com token sem autorização")
		void cadastrarAutoridade_tokenNaoAutorizado() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/autorizacao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenUserCreator)
					.content(startJson().name("nomeAutoridade").value("auth_cadastrar_material").endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta cadastrar autorização sem token")
		void cadastrarAutoridade_semToken() throws Exception {

			mockMvc.perform(
					MockMvcRequestBuilders.post("/v1/security/autorizacao").contentType(MediaType.APPLICATION_JSON)
							.content(startJson().name("nomeAutoridade").value("auth_cadastrar_material").endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta cadastrar autorização já cadastrada")
		void cadastrarAutorizacao_jaCadastrado() throws Exception {

			final String AUTH = "auth_cadastrar_material";

			doThrow(new NaoEPossivelCadastrarAutorizacaoException(AUTH)).when(cadastrarAutorizacao)
					.executar(any(CadastrarAutorizacaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/autorizacao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("role").value(AUTH).endJson())).andExpect(status().isBadRequest())
					.andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(jsonPath("status").value("400"))
					.andExpect(jsonPath("erro").value(HttpStatus.BAD_REQUEST.name())).andExpect(jsonPath("erros[0]")
							.value(containsString(String.format("A autorização '%s' já esta cadastrada", AUTH))));
		}

		@Test
		@DisplayName("Tenta atribuir autorização à função")
		void atribuirAutorizacaoAFuncao_sucesso() throws Exception {

			doNothing().when(atribuirAutorizacao).executar(any(AtribuirAutorizacaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao/1/autorizacao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("idDaAutorizacao").value(1).endJson())).andExpect(status().isCreated());
		}

		@Test
		@DisplayName("Tenta atribuir autorização à função que já possui vinculo")
		void atribuirAutorizacaoAFuncao_jaAtribuida() throws Exception {

			doThrow(new AutorizaJaVinculadaAFuncaoException("ROLE_ENG_MAT", "auth_cadastrar_material"))
					.when(atribuirAutorizacao).executar(any(AtribuirAutorizacaoCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao/1/autorizacao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("idDaAutorizacao").value(1).endJson())).andExpect(status().isBadRequest())
					.andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(jsonPath("status").value("400"))
					.andExpect(jsonPath("erro").value(HttpStatus.BAD_REQUEST.name()))
					.andExpect(jsonPath("erros[0]")
							.value(containsString(String.format("A atribuição '%s' já esta vinculada a função '%s'",
									"auth_cadastrar_material", "ROLE_ENG_MAT"))));
		}

		@Test
		@DisplayName("Tenta atribuir autorização à função sem token")
		void atribuirAutorizacaoAFuncao_SemToken() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao/1/autorizacao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(startJson().name("idDaAutorizacao").value(1).endJson())).andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta atribuir autorização à função com token sem permissão")
		void atribuirAutorizacaoAFuncao_tokenSemPermissao() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/funcao/1/autorizacao")
					.contentType(MediaType.APPLICATION_JSON).header(headerName, tokenUserCreator)
					.contentType(MediaType.APPLICATION_JSON)
					.content(startJson().name("idDaAutorizacao").value(1).endJson())).andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("Usuário")
	class UsuarioTest {

		@Test
		@DisplayName("Tenta cadastrar usuário")
		void cadastrarUsuario_sucesso() throws Exception {

			doReturn(1L).when(cadastrarUsuario).executar(any(CadastrarUsuarioCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario").contentType(MediaType.APPLICATION_JSON)
					.header(headerName, tokenUserCreator)
					.content(startJson().name("username").value("joao.veloso").name("password").value("password")
							.name("validade").value(LocalDate.now().plusYears(1L).toString()).endJson()))
					.andExpect(status().isCreated()).andExpect(jsonPath("id").value("1"));
		}

		@Test
		@DisplayName("Tenta cadastrar usuário já cadastrado")
		void cadastrarUsuario_jaCadastrado() throws Exception {

			doThrow(new UsuarioJaCadastradaException("joao.veloso")).when(cadastrarUsuario)
					.executar(any(CadastrarUsuarioCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario").contentType(MediaType.APPLICATION_JSON)
					.header(headerName, tokenUserCreator)
					.content(startJson().name("role").value("joao.veloso").endJson()))
					.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp").isNotEmpty())
					.andExpect(jsonPath("status").value("400"))
					.andExpect(jsonPath("erro").value(HttpStatus.BAD_REQUEST.name())).andExpect(jsonPath("erros[0]")
							.value(containsString(String.format("O usuário '%s' já esta cadastrada", "joao.veloso"))));
		}

		@Test
		@DisplayName("Tenta cadastrar usuário com token sem autorização")
		void cadastrarUsuario_tokenNaoAutorizado() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario").contentType(MediaType.APPLICATION_JSON)
					.header(headerName, tokenRolesAndAuthorities)
					.content(startJson().name("nomeAutoridade").value("auth_cadastrar_material").endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta cadastrar usuário sem token")
		void cadastrarUsuario_semToken() throws Exception {

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario").contentType(MediaType.APPLICATION_JSON)
					.content(startJson().name("nomeAutoridade").value("auth_cadastrar_material").endJson()))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Tenta cadastrar usuário com validade no passado")
		void cadastrarUsuario_comValidadeNoPassado() throws Exception {

			LocalDate validade = LocalDate.now().minusWeeks(1);

			doThrow(new DataDeValidadeNoPassadoException(validade)).when(cadastrarUsuario)
					.executar(any(CadastrarUsuarioCommand.class));

			mockMvc.perform(MockMvcRequestBuilders.post("/v1/security/usuario").contentType(MediaType.APPLICATION_JSON)
					.header(headerName, tokenUserCreator)
					.content(startJson().name("role").value("joao.veloso").name("validade").value(validade.toString())
							.endJson()))
					.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp").isNotEmpty())
					.andExpect(jsonPath("status").value("400"))
					.andExpect(jsonPath("erro").value(HttpStatus.BAD_REQUEST.name()))
					.andExpect(jsonPath("erros[0]").value(containsString(
							String.format("A data de validade fornecida '%s' esta no passado", validade.toString()))));
		}
	}
}