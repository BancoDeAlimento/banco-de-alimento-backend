package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.servico.EntidadeService;
import br.recife.bancodealimentos.web.dto.EntidadeCriadaResponse;
import br.recife.bancodealimentos.web.dto.EntidadeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entidades")
public class EntidadeController {

    private final EntidadeService entidades;

    public EntidadeController(EntidadeService entidades) {
        this.entidades = entidades;
    }

    @PostMapping
    public ResponseEntity<EntidadeCriadaResponse> criar(@Valid @RequestBody EntidadeRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entidades.criar(requisicao));
    }
}