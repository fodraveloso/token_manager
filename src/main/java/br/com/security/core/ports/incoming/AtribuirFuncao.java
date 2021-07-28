package br.com.security.core.ports.incoming;

import br.com.security.core.models.commands.AtribuirFuncaoCommand;

public interface AtribuirFuncao {

	void executar(AtribuirFuncaoCommand command);

}
