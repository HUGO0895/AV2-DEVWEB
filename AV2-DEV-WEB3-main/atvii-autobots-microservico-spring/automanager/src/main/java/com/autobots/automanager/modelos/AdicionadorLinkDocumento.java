package com.autobots.automanager.modelos;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.DTO.Response.DocumentoDtO;
import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;

@Component
public class AdicionadorLinkDocumento implements AdicionadorLink<DocumentoDtO> {

    @Override
    public void adicionarLink(List<DocumentoDtO> lista) {
        for (DocumentoDtO doc : lista) {
            doc.add(linkTo(methodOn(DocumentoControle.class)
                    .pegarDoc(doc.getId())).withSelfRel());
        }
    }

    @Override
    public void adicionarLink(DocumentoDtO doc) {
        doc.add(linkTo(methodOn(DocumentoControle.class)
                .pegarDoc(doc.getId())).withSelfRel());

        doc.add(linkTo(methodOn(DocumentoControle.class)
                .listarDocs()).withRel("documentos"));

        // link para o CLIENTE dono do documento (outro controller)
        if (doc.getCliente() != null) {
            doc.add(linkTo(methodOn(ClienteControle.class)
                    .obterCliente(doc.getCliente())).withRel("cliente"));
        }
    }
}