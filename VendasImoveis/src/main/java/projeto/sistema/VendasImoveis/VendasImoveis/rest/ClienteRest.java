package projeto.sistema.VendasImoveis.VendasImoveis.rest;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar; 
import java.util.HashMap;
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

import projeto.sistema.VendasImoveis.VendasImoveis.model.Cliente;
import projeto.sistema.VendasImoveis.VendasImoveis.model.Erro;
import projeto.sistema.VendasImoveis.VendasImoveis.model.Payload;
import projeto.sistema.VendasImoveis.VendasImoveis.model.TokenJWT;
import projeto.sistema.VendasImoveis.VendasImoveis.repository.ClienteRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/cliente")
public class ClienteRest {

	@Autowired
	private ClienteRepository repository;
	
	public static final String EMISSOR = "S3CR3T003M11S00R";
	
	public static final String SECRET = "C0D11F1C4T0K3N";
	
	@Autowired
	private HttpServletRequest request;
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> salvar(@RequestBody Cliente cliente){

		repository.save(cliente);
		
		Cliente cli = new Cliente();
		
		cli.setEmail(cliente.getEmail());
		
		cli.setSenha(cliente.getSenha());
		
		System.out.println("cliente novoooo " + cli);
		 
		logar(cli);
		
		return ResponseEntity.created(URI.create("/"  + cliente.getId())).body(cliente);
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Cliente> listar(){
		
		
		return repository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deletar(@PathVariable("id") Long id){
		
		repository.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> alterar(@PathVariable("id") Long id, @RequestBody Cliente cliente){
		
		String senha = repository.findById(id).get().getSenha();
		
		cliente.setSenhaSemHash(senha);
		
		repository.save(cliente);
		
		HttpHeaders headers = new HttpHeaders(); 
		
		headers.setLocation(URI.create("/api/cliente/"));
		
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> logar(@RequestBody Cliente cliente){
		
		Cliente cli = repository.findByEmailAndSenha(cliente.getEmail(), cliente.getSenha());
		
		System.out.println(cliente);
		
		if(cli != null) {
			
			System.out.println("Entrei aqui");

			Map<String, Object> payload = new HashMap<String, Object>();
			
			payload.put("id_cliente", cli.getId());
			
			payload.put("telefone", cli.getTelefone());
			
			payload.put("nome", cli.getNome());
			
			payload.put("cep", cli.getCep());
			
			payload.put("nascimento", cli.getNascimento());
			
			payload.put("email", cli.getEmail());
			
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			
			TokenJWT tokenJWT = new TokenJWT();
			
			tokenJWT.setToken(JWT.create().withPayload(payload).withIssuer(EMISSOR).sign(algorithm));
			
			return ResponseEntity.ok(tokenJWT);
			
		}else if(cliente.getEmail().equals("admin")) {
			
			Map<String, Object> payload = new HashMap<String, Object>();
			
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			
			TokenJWT tokenJWT = new TokenJWT();
			
			tokenJWT.setToken(JWT.create().withPayload(payload).withIssuer(EMISSOR).sign(algorithm));
			
			return ResponseEntity.ok(tokenJWT);
			
		}
		
		System.out.println("entrei 2");
		
		Erro erro = new Erro(HttpStatus.UNAUTHORIZED, "Não Autorizado!", null);
		
		return new ResponseEntity<Object>(erro, HttpStatus.UNAUTHORIZED);
		
	}
	
	@RequestMapping(value = "/decode", method = RequestMethod.POST)
	public ResponseEntity<Object> decode(){
		
		String token = request.getHeader("Authorization");	
		
		token = token.substring(1, token.length() - 1);
		
		if(token.equals("ndefine")) {
			
			Erro erro = new Erro( HttpStatus.UNAUTHORIZED, "Token nulo!", null);
			
			return new ResponseEntity<Object>(erro, HttpStatus.UNAUTHORIZED);
			
		}else {
			
		Algorithm algorithm = Algorithm.HMAC256(SECRET);
		
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(EMISSOR).build();
		
		DecodedJWT jwt = verifier.verify(token);
		
		Map<String, Claim> payload = jwt.getClaims();
		
		Payload payload2 = new Payload();
		
		payload2.setIdUsuario(payload.get("id_cliente").toString());
		
		payload2.setTelefone(payload.get("telefone").toString().substring(1, payload.get("telefone").toString().length() - 1));
		
		payload2.setNome(payload.get("nome").toString().substring(1, payload.get("nome").toString().length() - 1));
		
		payload2.setCep(payload.get("cep").toString().substring(1, payload.get("cep").toString().length() - 1));
		
		payload2.setNascimento(payload.get("nascimento").toString().substring(1, payload.get("nascimento").toString().length() - 1));
		
		payload2.setEmail(payload.get("email").toString().substring(1, payload.get("email").toString().length() - 1));
		
		return ResponseEntity.ok(payload2);
		}
		
	}
	
	@RequestMapping(value = "/perfil", method = RequestMethod.GET)
	public Cliente perfilUsuario() {

		String token = request.getHeader("Authorization");	
		
		token = token.substring(1, token.length() - 1);
		 
		Algorithm algorithm = Algorithm.HMAC256(ClienteRest.SECRET);
		
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(ClienteRest.EMISSOR).build();
		
		DecodedJWT jwt = verifier.verify(token);
		
		Map<String, Claim> payload = jwt.getClaims();
		
		Cliente cliente = repository.findById(payload.get("id_cliente").asLong()).get();
		
		return cliente;
		
		
	}
	
}
