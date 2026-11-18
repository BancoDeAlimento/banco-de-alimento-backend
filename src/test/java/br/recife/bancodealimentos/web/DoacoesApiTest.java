package br.recife.bancodealimentos.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DoacoesApiTest {

    @Autowired private WebApplicationContext contexto;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    @Test
    void criaDoacaoSemExporDadosPessoaisNaResposta() throws Exception {
        String corpo = """
                {"nomeDoador":"Mercado Boa Compra","tipoDoador":"EMPRESA",
                 "contato":"81 99999-0000","descricaoAlimentos":"Arroz e feijão",
                 "quantidadeEstimadaKg":50,"consentimentoLgpd":true}
                """;
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.contato").doesNotExist());
    }

    @Test
    void recusaDoacaoSemCamposObrigatorios() throws Exception {
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"consentimentoLgpd\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nomeDoador").exists())
                .andExpect(jsonPath("$.erros.descricaoAlimentos").exists());
    }

    @Test
    void exigeConsentimento() throws Exception {
        String corpo = """
                {"nomeDoador":"Ana","tipoDoador":"PESSOA_FISICA","contato":"81 98888-0000",
                 "descricaoAlimentos":"Frutas","consentimentoLgpd":false}
                """;
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.consentimentoLgpd").exists());
    }
}