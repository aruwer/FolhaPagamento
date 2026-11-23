package folhapagamento;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class FolhaPagamentoService {
    private final RepositorioColaboradores repositorio;

    public FolhaPagamentoService(RepositorioColaboradores repositorio) {
        this.repositorio = repositorio;
    }

    public List<Colaborador> listar() {
        return repositorio.listar();
    }

    public Optional<Colaborador> buscar(String matricula) {
        return repositorio.buscar(matricula);
    }

    public void cadastrar(Colaborador colaborador) throws IOException {
        repositorio.cadastrar(colaborador);
    }

    public void atualizar(Colaborador colaborador) throws IOException {
        repositorio.atualizar(colaborador);
    }

    public void excluir(String matricula) throws IOException {
        repositorio.excluir(matricula);
    }

    public List<FolhaProcessada> processarFolha(int mes, int ano) {
        return repositorio.processarFolha(mes, ano);
    }

    public List<FolhaProcessada> consultarFolha(int mes, int ano) {
        return repositorio.consultarFolha(mes, ano);
    }

    public Optional<FolhaProcessada> buscarFolha(String matricula, int mes, int ano) {
        return repositorio.buscarFolha(matricula, mes, ano);
    }

    public ResumoFolha gerarResumo(int mes, int ano) {
        return resumir(consultarFolha(mes, ano));
    }

    public ResumoFolha resumir(List<FolhaProcessada> folha) {
        Map<String, BigDecimal> totaisPorCategoria = new LinkedHashMap<>();
        Map<String, Integer> quantidadePorCategoria = new LinkedHashMap<>();
        for (FolhaProcessada item : folha) {
            totaisPorCategoria.merge(item.tipoDescricao(), item.salarioFinal(), BigDecimal::add);
            quantidadePorCategoria.merge(item.tipoDescricao(), 1, Integer::sum);
        }
        BigDecimal total = folha.stream().map(FolhaProcessada::salarioFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ResumoFolha(folha.size(), total, Collections.unmodifiableMap(totaisPorCategoria),
                Collections.unmodifiableMap(quantidadePorCategoria));
    }

    public ResumoFolha gerarResumo() {
        List<Colaborador> colaboradores = listar();
        Map<String, BigDecimal> totaisPorCategoria = new LinkedHashMap<>();
        Map<String, Integer> quantidadePorCategoria = new LinkedHashMap<>();
        for (Colaborador colaborador : colaboradores) {
            totaisPorCategoria.merge(colaborador.getTipo(), colaborador.getSalarioFinal(), BigDecimal::add);
            quantidadePorCategoria.merge(colaborador.getTipo(), 1, Integer::sum);
        }
        BigDecimal total = colaboradores.stream().map(Colaborador::getSalarioFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ResumoFolha(colaboradores.size(), total, Collections.unmodifiableMap(totaisPorCategoria),
                Collections.unmodifiableMap(quantidadePorCategoria));
    }
}
