package br.com.security.core.ports.incoming;

import br.com.security.core.models.commands.CadastrarAutorizacaoCommand;

public interface CadastrarAutorizacao {

	Long executar(CadastrarAutorizacaoCommand command);

}
