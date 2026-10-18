package com.autobots.automanager.modelos;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.DTO.Response.TelefoneDto;
import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.TelefoneControle;

@Component
public class AdicionadorLinkTelefone implements AdicionadorLink<TelefoneDto> {

    @Override
    public void adicionarLink(List<TelefoneDto> lista) {
        for (TelefoneDto tel : lista) {
            tel.add(linkTo(methodOn(TelefoneControle.class)
                    .obterTelefone(tel.getId())).withSelfRel());
        }
    }

    @Override
    public void adicionarLink(TelefoneDto tel) {
        tel.add(linkTo(methodOn(TelefoneControle.class)
                .obterTelefone(tel.getId())).withSelfRel());

        tel.add(linkTo(methodOn(TelefoneControle.class)
                .obterTelefones()).withRel("telefones"));

        // link para o CLIENTE dono do telefone (outro controller)
        if (tel.getCliente() != null) {
            tel.add(linkTo(methodOn(ClienteControle.class)
                    .obterCliente(tel.getCliente())).withRel("cliente"));
        }
    }
}