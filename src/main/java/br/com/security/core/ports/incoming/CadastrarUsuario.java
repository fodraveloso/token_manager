package br.com.security.core.ports.incoming;

import br.com.security.core.models.commands.CadastrarUsuarioCommand;

public interface CadastrarUsuario {

	Long executar(CadastrarUsuarioCommand command);

}
