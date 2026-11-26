package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.web.dto.ImpactoResponse;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/impacto")
public class ImpactoController {

    @Value("${app.impacto.total-kg:143817}")
    private long totalKg;

    @GetMapping
    public ImpactoResponse consultarImpacto() {
        return new ImpactoResponse(totalKg);
    }
}