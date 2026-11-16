package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class FolhaPagamentoConfigTest {
    @Test
    @DisplayName("Deve criar repositório usando caminho de dados configurado")
    void criaRepositorioComCaminhoConfigurado(@TempDir Path diretorio) throws Exception {
        FolhaPagamentoConfig config = new FolhaPagamentoConfig();

        assertNotNull(config.repositorioColaboradores(diretorio.resolve("colaboradores.tsv").toString()));
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
