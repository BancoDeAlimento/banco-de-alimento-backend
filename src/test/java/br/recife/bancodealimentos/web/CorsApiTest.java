package br.recife.bancodealimentos.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CorsApiTest {

    private static final String ORIGEM_ANGULAR =
            "http://localhost:4200";

    @Autowired
    private WebApplicationContext contexto;

    private MockMvc mvc() {
        return MockMvcBuilders
                .webAppContextSetup(contexto)
                .build();
    }

    private void verificarPreflight(
            String rota,
            String metodo) throws Exception {

        mvc().perform(options(rota)
                        .header(HttpHeaders.ORIGIN, ORIGEM_ANGULAR)
                        .header(
                                HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                metodo
                        )
                        .header(
                                HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                "content-type"
                        ))

                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        ORIGEM_ANGULAR
                ));
    }

    @Test
    void angularPodeCadastrarPedido() throws Exception {
        verificarPreflight("/pedidos", "POST");
    }

    @Test
    void angularPodeConsultarPedido() throws Exception {
        verificarPreflight("/pedidos/ABCDEFGH", "GET");
    }

    @Test
    void angularPodeCadastrarDoacao() throws Exception {
        verificarPreflight("/doacoes", "POST");
    }

    @Test
    void angularPodeCadastrarEntidade() throws Exception {
        verificarPreflight("/entidades", "POST");
    }

    @Test
    void angularPodeConsultarImpacto() throws Exception {
        verificarPreflight("/api/impacto", "GET");
    }

    @Test
    void origemDesconhecidaNaoPodeUsarPreflight()
            throws Exception {

        mvc().perform(options("/doacoes")
                        .header(
                                HttpHeaders.ORIGIN,
                                "http://site-nao-autorizado.example"
                        )
                        .header(
                                HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                "POST"
                        ))

                .andExpect(status().isForbidden());
    }
}
