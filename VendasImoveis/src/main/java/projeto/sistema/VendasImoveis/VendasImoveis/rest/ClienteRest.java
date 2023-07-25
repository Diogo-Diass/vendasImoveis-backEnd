package projeto.sistema.VendasImoveis.VendasImoveis.rest;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

import projeto.sistema.VendasImoveis.VendasImoveis.email.EmailSenha;
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
		
		List<Cliente> lista = repository.findAll();
		
		if(cliente.getNome().equals("") || cliente.getCep().equals("") || cliente.getEmail().equals("") || 
				cliente.getSenha().equals("") || cliente.getNascimento().equals("") || 
				cliente.getTelefone().equals("")) {
			
				Erro erro = new Erro(HttpStatus.CONFLICT, "Por favor, informe os campos corretamente!", 1);
				
				return new ResponseEntity<Object>(erro, HttpStatus.CONFLICT);
		}
		
		for(Cliente cliente2 : lista) {
			
			if(cliente2.getEmail().equals(cliente.getEmail())) {
				
				Erro erro = new Erro(HttpStatus.CONFLICT, "Email já cadastrado!", 2);
				
				return new ResponseEntity<Object>(erro, HttpStatus.CONFLICT);	
			}
			
			break;
		}
		
		repository.save(cliente);
		
		Cliente cli = new Cliente();
		
		cli.setEmail(cliente.getEmail());
		
		cli.setSenha(cliente.getSenha());
		
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
	public ResponseEntity<?> alterar(@PathVariable("id") Long id, @RequestBody Cliente cliente){
		
		String senha = repository.findById(id).get().getSenha();
		
		String emailGoogle = repository.findById(id).get().getEmailGoogle();
		
		cliente.setEmailGoogle(emailGoogle);
		cliente.setSenhaSemHash(senha);
		
		List<Cliente> list = repository.findAll();
		
		for(Cliente cli : list) {
			
			if(cli.getEmail().equals(cliente.getEmail())) {
				
				if(cli.getId()!= cliente.getId()) {
				
				Erro erro = new Erro(HttpStatus.CONFLICT, "Email já cadastrado", 1);
				
				return new ResponseEntity<Object>(erro, HttpStatus.CONFLICT);
				}
			}
		}
		
		repository.save(cliente);
		
		HttpHeaders headers = new HttpHeaders(); 
		
		headers.setLocation(URI.create("/api/cliente/"));
		
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> logar(@RequestBody Cliente cliente){
		
		Cliente cli = repository.findByEmailAndSenhaOrEmailGoogle(cliente.getEmail(), cliente.getSenha(), cliente.getEmailGoogle());
		
		if(cli != null) {
			
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
			
		}
		
		Erro erro = new Erro(HttpStatus.UNAUTHORIZED, "Não Autorizado!", 0);
		
		return new ResponseEntity<Object>(erro, HttpStatus.UNAUTHORIZED);
		
	}
	
	@RequestMapping(value = "/decode", method = RequestMethod.POST)
	public ResponseEntity<Object> decode(){
		
		String token = request.getHeader("Authorization");	
		
		if(token.equals("undefined")) {
			
			Erro erro = new Erro( HttpStatus.UNAUTHORIZED, "Token nulo!", 0);
			
			return new ResponseEntity<Object>(erro, HttpStatus.UNAUTHORIZED);
			
		}else {
			
		Algorithm algorithm = Algorithm.HMAC256(SECRET);
		
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(EMISSOR).build();
		
		DecodedJWT jwt = verifier.verify(token);
		
		Map<String, Claim> payload = jwt.getClaims();
		
		Payload payload2 = new Payload();
		
		payload2.setIdUsuario(payload.get("id_cliente").toString());
		
		payload2.setTelefone(payload.get("telefone").toString());
		
		payload2.setNome(payload.get("nome").toString());
		
		payload2.setCep(payload.get("cep").toString());
		
		payload2.setNascimento(payload.get("nascimento").toString());
		
		payload2.setEmail(payload.get("email").toString());
		
		return ResponseEntity.ok(payload2);
		}
		
	}
	
	@RequestMapping(value = "/perfil", method = RequestMethod.GET)
	public Cliente perfilUsuario() {

		String token = request.getHeader("Authorization");	
		
		Algorithm algorithm = Algorithm.HMAC256(ClienteRest.SECRET);
		
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(ClienteRest.EMISSOR).build();
		
		DecodedJWT jwt = verifier.verify(token);
		
		Map<String, Claim> payload = jwt.getClaims();
		
		Cliente cliente = repository.findById(payload.get("id_cliente").asLong()).get();
		
		return cliente;
		
	}
	
	@RequestMapping(value = "loginGoogle", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> loginGoogle(@RequestBody Cliente cliente){
	
		Cliente cliente2 = repository.findByEmailGoogle(cliente.getEmailGoogle());
		
		if(cliente2 != null) {	
			
			return ResponseEntity.ok(cliente2);
			
		}else {
			
			repository.save(cliente);
			
			return ResponseEntity.created(URI.create("/"  + cliente.getId())).body(cliente);
			
		}
		
	}
	
	@RequestMapping(value = "/verificar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean verificarUsu(@RequestBody String email){
		
		Cliente cliente = repository.findByEmailGoogle(email);
		
		if(cliente != null) {
			
			return true;
			
		}else {
			
			return false;
		}
		

	}
	
	@RequestMapping(value = "/novaSenha", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> novaSenha(@RequestBody Cliente cli){
		
		Cliente cliente = repository.findByEmail(cli.getEmail());
		
		if(cliente != null) {
			
			int length = 6;
			
			String caracteres = "0123456789";
			
			StringBuilder construirString = new StringBuilder();
			
			Random random = new Random();
			
			for(int i = 0; i < length; i++) {
				
				int numeros = random.nextInt(caracteres.length());
				
				char valorNumerico = caracteres.charAt(numeros);
				
				construirString.append(valorNumerico);
				
			}	
			
			EmailSenha.recuperarSenha(cli.getEmail(), construirString.toString());
			
			return new ResponseEntity<Object>(HttpStatus.OK);
			
		}else {
		
		Erro erro = new Erro(HttpStatus.UNAUTHORIZED, "Não existe", 0);
		
		return new ResponseEntity<Object>(erro, HttpStatus.UNAUTHORIZED);
		}
	}
	
}
