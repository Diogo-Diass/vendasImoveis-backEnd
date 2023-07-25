package projeto.sistema.VendasImoveis.VendasImoveis.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import projeto.sistema.VendasImoveis.VendasImoveis.model.Cliente;


public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long> {

	public List<Cliente> findAll();
	
	public Cliente findByEmailAndSenhaOrEmailGoogle(String email, String senha, String emailGoogle);
	
	public Cliente findByEmailGoogle(String email);
	
	public Cliente findByEmail(String email);
	
}
