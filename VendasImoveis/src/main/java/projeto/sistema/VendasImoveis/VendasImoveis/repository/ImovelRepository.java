package projeto.sistema.VendasImoveis.VendasImoveis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import projeto.sistema.VendasImoveis.VendasImoveis.model.Imovel;

public interface ImovelRepository extends PagingAndSortingRepository<Imovel, Long>{
	
	@Query("SELECT i FROM Imovel i ORDER BY i.id DESC")
	public List<Imovel> findAll();
	
	@Query("SELECT i FROM Imovel i WHERE i.donoImovel.id = :p ")
	public List<Imovel> listaImoveis(@Param("p") Long id);
}
