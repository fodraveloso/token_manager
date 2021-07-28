package br.com.security.core.ports.incoming;

import br.com.security.core.models.commands.CadastrarFuncaoCommand;

public interface CadastrarFuncao {

	Long executar(CadastrarFuncaoCommand command);

}
