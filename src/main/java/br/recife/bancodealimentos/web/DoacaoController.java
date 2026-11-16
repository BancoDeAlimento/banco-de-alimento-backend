package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.servico.DoacaoService;
import br.recife.bancodealimentos.web.dto.DoacaoCriadaResponse;
import br.recife.bancodealimentos.web.dto.DoacaoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    private final DoacaoService doacoes;

    public DoacaoController(DoacaoService doacoes) {
        this.doacoes = doacoes;
    }

    @PostMapping
    public ResponseEntity<DoacaoCriadaResponse> criar(@Valid @RequestBody DoacaoRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doacoes.criar(requisicao));
    }
}