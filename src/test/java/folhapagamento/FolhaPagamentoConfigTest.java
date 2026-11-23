package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.mockito.MockedStatic;

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
class FolhaPagamentoConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepositorioColaboradores repositorio;

    @Test
    @DisplayName("Deve inicializar persistência JPA em ambiente de teste")
    void iniciaRepositorioJPA() {
        assertNotNull(repositorio);
    }

    @Test
    @DisplayName("Deve renderizar as telas principais com a persistência JPA")
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

    @Test
    @DisplayName("Deve delegar inicialização da aplicação ao Spring Boot")
    void iniciaAplicacao() {
        String[] argumentos = { "--spring.main.web-application-type=none" };
        new FolhaPagamentoWebApplication();
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            FolhaPagamentoWebApplication.main(argumentos);

            springApplication.verify(
                    () -> SpringApplication.run(FolhaPagamentoWebApplication.class, argumentos));
        }
    }
}
