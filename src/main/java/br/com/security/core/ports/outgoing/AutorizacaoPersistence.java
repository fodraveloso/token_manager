package br.com.security.core.ports.outgoing;

import br.com.security.core.models.commands.CadastrarAutorizacaoCommand;

public interface AutorizacaoPersistence {

	Long cadastrarAutorizacao(CadastrarAutorizacaoCommand command);

}
