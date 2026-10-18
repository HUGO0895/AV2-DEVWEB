package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.DTO.Request.CriarClienteDTo;
import com.autobots.automanager.DTO.Request.atualizarClienteDTO;
import com.autobots.automanager.DTO.Response.ClienteDTO;
import com.autobots.automanager.Mappers.MapperCliente;
import com.autobots.automanager.Servicos.ClienteService;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;   // NOVO

@RestController
public class ClienteControle {
	@Autowired
	private ClienteService servicoCliente;

	@Autowired
	private MapperCliente mapperCli;

	@Autowired
	private AdicionadorLinkCliente adicionadorLink;              

	@GetMapping("/clientes/{id}")
	public ResponseEntity<ClienteDTO> obterCliente(@PathVariable long id) {
		ClienteDTO dto = mapperCli.clienteParaClienteDTO(servicoCliente.acharUmCliente(id));
		adicionadorLink.adicionarLink(dto);                      
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/clientes")
	public ResponseEntity<List<ClienteDTO>> obterClientes() {
		List<ClienteDTO> lista = servicoCliente.listarClientes().stream()
				.map(cliente -> mapperCli.clienteParaClienteDTO(cliente))
				.collect(Collectors.toList());
		adicionadorLink.adicionarLink(lista);                     
		return ResponseEntity.ok(lista);
	}

	@PostMapping("/clientes")
	public ResponseEntity<Void> cadastrarCliente(@RequestBody @Valid CriarClienteDTo cliente) {
		Cliente salvo = servicoCliente.salvarCliente(cliente);
		URI uri = linkTo(methodOn(ClienteControle.class).obterCliente(salvo.getId())).toUri();
		return ResponseEntity.created(uri).build();
	}

	@PutMapping("/clientes")
	public ResponseEntity<Void> atualizarCliente(@RequestBody @Valid atualizarClienteDTO atualizacao) {
		servicoCliente.atualizarCliente(atualizacao);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
		servicoCliente.deletarCLiente(id);
		return ResponseEntity.noContent().build();
	}
}