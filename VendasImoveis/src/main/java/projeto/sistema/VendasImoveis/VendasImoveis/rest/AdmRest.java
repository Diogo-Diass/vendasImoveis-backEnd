package projeto.sistema.VendasImoveis.VendasImoveis.rest;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import projeto.sistema.VendasImoveis.VendasImoveis.model.Administrador;
import projeto.sistema.VendasImoveis.VendasImoveis.repository.AdmRepository;

@RestController
@RequestMapping("/api/adm")
public class AdmRest {
	
	@Autowired
	private AdmRepository repository;
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> salvar(@RequestBody Administrador adm){
		
		repository.save(adm);
		
		return ResponseEntity.created(URI.create("/" + adm.getId())).body(adm);
		
	}

}
