package projeto.sistema.VendasImoveis.VendasImoveis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;


@Data
@Entity
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;

	private String cep;
	
	private String nascimento;
	
	private String telefone;
	
	private String email;
	
	private String senha;
	
	private String emailGoogle;
	
	// metodo para setar a senha aplicando o hash
		public void setSenha(String senha) {

			// aplica o hash e seta a senha no objeto
			this.senha = HashUtil.hash256(senha);
		}

		public void setSenhaSemHash(String hash) {

			// tira o hash na senha
			this.senha = hash;

		}
}
