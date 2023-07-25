package projeto.sistema.VendasImoveis.VendasImoveis.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import projeto.sistema.VendasImoveis.VendasImoveis.model.Imovel;
import projeto.sistema.VendasImoveis.VendasImoveis.model.Payload;
import projeto.sistema.VendasImoveis.VendasImoveis.repository.ImovelRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/imovel")
public class ImovelRest {
	
	@Autowired
	private ImovelRepository repository;
	
	@Autowired
	HttpServletRequest request;

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> salvar(@RequestBody Imovel imovel) {
		
		repository.save(imovel);
		
		return ResponseEntity.created(URI.create("/" + imovel.getId())).build();
	}
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable<Imovel> listar(){
		
		return repository.findAll();
	}
	
	@RequestMapping(value = "/listaDono", method = RequestMethod.GET)
	public List<Imovel> listaDonoImovel(){
		
		String token = request.getHeader("Authorization");	
	
		Algorithm algorithm = Algorithm.HMAC256(ClienteRest.SECRET);
		
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(ClienteRest.EMISSOR).build();
		
		DecodedJWT jwt = verifier.verify(token);
		
		Map<String, Claim> payload = jwt.getClaims();
		
		List<Imovel> list = repository.listaImoveis(payload.get("id_cliente").asLong());
		
		return list;
	}
	
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deletar(@PathVariable("id") Long id){
		
		repository.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> alterar(@PathVariable("id") Long id, @RequestBody Imovel imovel){
		
		repository.save(imovel);

		HttpHeaders headers = new HttpHeaders(); 
		
		headers.setLocation(URI.create("/api/imovel/"));
		
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
		
	}
	
	
}
	