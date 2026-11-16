package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositorioColaboradoresTest {
    @TempDir
    Path diretorioTemporario;

    @Test
    @DisplayName("CT-030 - Deve iniciar vazio quando o arquivo ainda não existe")
    void arquivoInexistenteIniciaVazio() throws IOException {
        Path arquivo = diretorioTemporario.resolve("subdiretorio").resolve("colaboradores.tsv");

        RepositorioColaboradores repositorio = new RepositorioColaboradores(arquivo);

        assertTrue(repositorio.listar().isEmpty());
        assertFalse(Files.exists(arquivo));
    }

    @Test
    @DisplayName("CT-031 - Deve iniciar vazio quando o arquivo existe sem registros")
    void arquivoVazioIniciaVazio() throws IOException {
        Path arquivo = diretorioTemporario.resolve("colaboradores.tsv");
        Files.createFile(arquivo);

        assertTrue(new RepositorioColaboradores(arquivo).listar().isEmpty());
    }

    @Test
    @DisplayName("CT-030 - Deve gravar e recuperar colaboradores entre instâncias")
    void persisteTiposEmNovasInstancias() throws IOException {
        Path arquivo = diretorioTemporario.resolve("dados").resolve("colaboradores.tsv");
        RepositorioColaboradores escritor = new RepositorioColaboradores(arquivo);
        escritor.cadastrar(new ColaboradorPadrao("001", "Ana Silva", 1_000.25));
        escritor.cadastrar(new ColaboradorComissionado("002", "Bia/Costas", 2_000, 500, 10));
        escritor.cadastrar(new ColaboradorProducao("003", "Caio", 1_500, 12.5, 2.75));

        RepositorioColaboradores leitor = new RepositorioColaboradores(arquivo);

        assertEquals(3, leitor.listar().size());
        assertEquals("Ana Silva", leitor.buscar("001").orElseThrow().getNome());
        assertEquals(2_050.0, leitor.buscar("002").orElseThrow().getSalarioFinal());
        assertEquals(1_534.375, leitor.buscar("003").orElseThrow().getSalarioFinal(), 0.000001);
    }

    @Test
    @DisplayName("CT-023 - Deve retornar cópia imutável da lista cadastrada")
    void listaEImutavel() throws IOException {
        RepositorioColaboradores repositorio =
                new RepositorioColaboradores(diretorioTemporario.resolve("colaboradores.tsv"));
        repositorio.cadastrar(new ColaboradorPadrao("001", "Ana", 1_000));

        assertThrows(UnsupportedOperationException.class, () -> repositorio.listar().clear());
    }

    @Test
    @DisplayName("CT-009 - Deve rejeitar matrícula duplicada")
    void rejeitaCadastroDuplicado() throws IOException {
        RepositorioColaboradores repositorio =
                new RepositorioColaboradores(diretorioTemporario.resolve("colaboradores.tsv"));
        repositorio.cadastrar(new ColaboradorPadrao("001", "Ana", 1_000));

        assertThrows(IllegalArgumentException.class,
                () -> repositorio.cadastrar(new ColaboradorPadrao("001", "Bia", 2_000)));
    }

    @Test
    @DisplayName("CT-024 - Deve atualizar cadastro existente e rejeitar matrícula inexistente")
    void atualizaCadastro() throws IOException {
        RepositorioColaboradores repositorio =
                new RepositorioColaboradores(diretorioTemporario.resolve("colaboradores.tsv"));
        repositorio.cadastrar(new ColaboradorPadrao("001", "Ana", 1_000));

        repositorio.atualizar(new ColaboradorPadrao("001", "Ana Atualizada", 1_500));

        assertEquals("Ana Atualizada", repositorio.buscar("001").orElseThrow().getNome());
        assertThrows(IllegalArgumentException.class,
                () -> repositorio.atualizar(new ColaboradorPadrao("999", "Ninguém", 1_000)));
    }

    @Test
    @DisplayName("CT-025 - Deve remover fisicamente o colaborador do arquivo atual")
    void exclusaoFisicaDoCadastroAtivo() throws IOException {
        Path arquivo = diretorioTemporario.resolve("colaboradores.tsv");
        RepositorioColaboradores repositorio = new RepositorioColaboradores(arquivo);
        repositorio.cadastrar(new ColaboradorPadrao("001", "Ana", 1_000));

        repositorio.excluir("001");

        assertTrue(repositorio.listar().isEmpty());
        assertTrue(new RepositorioColaboradores(arquivo).listar().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> repositorio.excluir("001"));
    }

    @Test
    @DisplayName("CT-037 / RN009 - Deve preservar histórico após exclusão lógica")
    void exclusaoLogicaDevePreservarRegistroHistorico() throws IOException {
        RepositorioColaboradores repositorio =
                new RepositorioColaboradores(diretorioTemporario.resolve("colaboradores.tsv"));
        repositorio.cadastrar(new ColaboradorPadrao("001", "Ana", 1_000));

        repositorio.excluir("001");

        assertTrue(repositorio.buscar("001").isPresent(),
                "RN009 exige manter o colaborador disponível para histórico após excluir da consulta ativa");
    }

    @Test
    @DisplayName("Deve ignorar linhas em branco e rejeitar linha corrompida com número da linha")
    void ignoraVaziasEInformaLinhaCorrompida() throws IOException {
        Path arquivo = diretorioTemporario.resolve("colaboradores.tsv");
        Files.writeString(arquivo, System.lineSeparator() + "linha-corrompida" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        IOException erro = assertThrows(IOException.class, () -> new RepositorioColaboradores(arquivo));

        assertTrue(erro.getMessage().contains("linha 2"));
    }

    @Test
    @DisplayName("Deve rejeitar registros duplicados durante a carga do arquivo")
    void rejeitaDuplicidadePersistida() throws IOException {
        Path arquivo = diretorioTemporario.resolve("colaboradores.tsv");
        String linha = "PADRAO\tMDAx\tQW5h\t1000.0";
        Files.write(arquivo, List.of(linha, linha), StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> new RepositorioColaboradores(arquivo));
    }

    @Test
    @DisplayName("Deve rejeitar tipo desconhecido e quantidade incorreta de campos no arquivo")
    void rejeitaTiposOuQuantidadeDeCamposIncorretos() throws IOException {
        Path arquivo = diretorioTemporario.resolve("colaboradores.tsv");
        for (String linha : List.of(
                "DESCONHECIDO\tMDAx\tQW5h\t1000.0",
                "PADRAO\tMDAx\tQW5h\t1000.0\tcampo-extra")) {
            Files.writeString(arquivo, linha, StandardCharsets.UTF_8);
            assertThrows(IOException.class, () -> new RepositorioColaboradores(arquivo));
        }
    }
}
