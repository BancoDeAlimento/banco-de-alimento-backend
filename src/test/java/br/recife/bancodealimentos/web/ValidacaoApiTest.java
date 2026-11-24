
package br.recife.bancodealimentos.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ValidacaoApiTest {

    @Autowired
    private WebApplicationContext contexto;

    private MockMvc mvc() {
        return MockMvcBuilders
                .webAppContextSetup(contexto)
                .build();
    }

    private void verificarErroDeValidacao(
            String rota,
            String campo) throws Exception {

        mvc().perform(post(rota)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isBadRequest())

                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))

                .andExpect(jsonPath("$.status").value(400))

                .andExpect(jsonPath("$.detail").value(
                        "Há campos inválidos na requisição."
                ))

                .andExpect(jsonPath(
                        "$.erros." + campo
                ).exists());
    }

    @Test
    void validaCamposObrigatoriosDoPedido() throws Exception {
        verificarErroDeValidacao(
                "/pedidos",
                "nomeOuApelido"
        );
    }

    @Test
    void validaCamposObrigatoriosDaDoacao() throws Exception {
        verificarErroDeValidacao(
                "/doacoes",
                "nomeDoador"
        );
    }

    @Test
    void validaCamposObrigatoriosDaEntidade() throws Exception {
        verificarErroDeValidacao(
                "/entidades",
                "nome"
        );
    }
}
