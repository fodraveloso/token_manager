package br.com.security.core.ports.incoming;

import br.com.security.core.models.commands.AtribuirAutorizacaoCommand;

public interface AtribuirAutorizacao {

	void executar(AtribuirAutorizacaoCommand command);

}
