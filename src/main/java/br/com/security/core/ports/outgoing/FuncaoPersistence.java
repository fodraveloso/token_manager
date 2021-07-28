package br.com.security.core.ports.outgoing;

import br.com.security.core.models.commands.AtribuirAutorizacaoCommand;
import br.com.security.core.models.commands.CadastrarFuncaoCommand;

public interface FuncaoPersistence {

	Long cadastrarFuncao(CadastrarFuncaoCommand command);

	void atribuirAutorizacao(AtribuirAutorizacaoCommand command);

}
