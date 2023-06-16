package projeto.sistema.VendasImoveis.VendasImoveis.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import projeto.sistema.VendasImoveis.VendasImoveis.Enum.FormaPagamento;

@Data
@Entity
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int qtdParcela;
	
	@ManyToOne
	private Cliente cliente;
	
	private Double valor;
	
	@ManyToOne
	private Imovel imovel;
	
	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;
	
}
