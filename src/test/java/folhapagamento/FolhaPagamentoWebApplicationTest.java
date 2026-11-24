package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:folha-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FolhaPagamentoWebApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepositorioColaboradores repositorio;

    @Test
    @DisplayName("RNF003: inicializa persistência JPA em ambiente de teste")
    void iniciaRepositorioJPA() {
        assertNotNull(repositorio);
    }

    @Test
    @DisplayName("RF005 e RF007: renderiza telas principais com persistência JPA")
    void renderizaTelasPrincipais() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        mockMvc.perform(get("/colaboradores/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"));

        mockMvc.perform(get("/folha").param("mes", "1").param("ano", "2025"))
                .andExpect(status().isOk())
                .andExpect(view().name("folha"));
    }

}
