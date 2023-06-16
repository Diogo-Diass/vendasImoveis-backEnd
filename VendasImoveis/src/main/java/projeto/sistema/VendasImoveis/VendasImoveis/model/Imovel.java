package projeto.sistema.VendasImoveis.VendasImoveis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class Imovel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition = "LONGTEXT")
	private String fotosImovel;
	
	private Double preco;
	
	private int qtdBanheiro;
	
	private int qtdSala;
	
	private int qtdCozinha;
	
	private int qtdQuarto;
	
	private int qtdLavanderia;
	
	private int qtdGaragem;
	
	private int qtdComodo;
	
	private String endereco;
	
	private String descricao;
	
	@ManyToOne
	private Cliente donoImovel;
	
	
	
	
	
	

}
