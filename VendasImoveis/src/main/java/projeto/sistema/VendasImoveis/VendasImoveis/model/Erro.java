package projeto.sistema.VendasImoveis.VendasImoveis.model;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class Erro {

	private HttpStatus codErro;
	
	private String mensagem;
	
	private String exception;
	
	public Erro(HttpStatus codErro, String mensagem, String exception) {
		
		this.codErro = codErro;
		
		this.mensagem = mensagem;
		
		this.exception = exception;
	}
}
