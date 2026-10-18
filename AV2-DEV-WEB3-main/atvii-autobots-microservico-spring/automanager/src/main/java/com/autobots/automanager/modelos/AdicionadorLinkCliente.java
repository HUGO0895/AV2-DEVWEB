package com.autobots.automanager.modelos;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.DTO.Response.ClienteDTO;
import com.autobots.automanager.DTO.Response.DocumentoDtO;
import com.autobots.automanager.DTO.Response.TelefoneDto;
import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.controles.TelefoneControle;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<ClienteDTO> {

    @Override
    public void adicionarLink(List<ClienteDTO> lista) {
        for (ClienteDTO cliente : lista) {
            cliente.add(linkTo(methodOn(ClienteControle.class)
                    .obterCliente(cliente.getId())).withSelfRel());
        }
    }

    @Override
    public void adicionarLink(ClienteDTO cliente) {
        // link para ele mesmo
        cliente.add(linkTo(methodOn(ClienteControle.class)
                .obterCliente(cliente.getId())).withSelfRel());

        // link para a lista de clientes
        cliente.add(linkTo(methodOn(ClienteControle.class)
                .obterClientes()).withRel("clientes"));

        // link para o ENDEREÇO (outro controller)
        if (cliente.getEndereco() != null) {
            cliente.add(linkTo(methodOn(EnderecoControle.class)
                    .pegarEndereco(cliente.getEndereco().getId())).withRel("endereco"));
        }

        // cada TELEFONE ganha o link dele (outro controller)
        if (cliente.getTelefones() != null) {
            for (TelefoneDto tel : cliente.getTelefones()) {
                tel.add(linkTo(methodOn(TelefoneControle.class)
                        .obterTelefone(tel.getId())).withSelfRel());
            }
        }

        // cada DOCUMENTO ganha o link dele (outro controller)
        if (cliente.getDocumento() != null) {
            for (DocumentoDtO doc : cliente.getDocumento()) {
                doc.add(linkTo(methodOn(DocumentoControle.class)
                        .pegarDoc(doc.getId())).withSelfRel());
            }
        }
    }
}