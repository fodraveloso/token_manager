package br.com.security.core.ports.outgoing;

import br.com.security.core.models.commands.AtribuirFuncaoCommand;
import br.com.security.core.models.commands.CadastrarUsuarioCommand;

public interface UsuarioPersistence {

	Long cadastrarUsuario(CadastrarUsuarioCommand command);

	void atribuirFuncao(AtribuirFuncaoCommand command);

}
