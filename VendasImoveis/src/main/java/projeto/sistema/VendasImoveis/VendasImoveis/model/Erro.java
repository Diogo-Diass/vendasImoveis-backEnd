package projeto.sistema.VendasImoveis.VendasImoveis.model;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class Erro {

	private HttpStatus codErro;
	
	private String mensagem;
	
	private int codigo;
	
	public Erro(HttpStatus codErro, String mensagem, int codigo) {
		
		this.codErro = codErro;
		
		this.mensagem = mensagem;
		
		this.codigo = codigo;
	}
}
