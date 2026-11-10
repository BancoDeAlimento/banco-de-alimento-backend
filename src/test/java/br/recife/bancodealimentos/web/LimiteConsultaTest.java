package br.recife.bancodealimentos.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.rate-limit.consulta-por-minuto=3")
class LimiteConsultaTest {

    @Autowired
    private WebApplicationContext contexto;

    @Test
    void bloqueiaConsultasAcimaDoLimite() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(contexto).build();

        for (int i = 0; i < 3; i++) {
            mvc.perform(get("/pedidos/AAAAAAAA")).andExpect(status().isNotFound());
        }
        mvc.perform(get("/pedidos/AAAAAAAA"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"));
    }
}
