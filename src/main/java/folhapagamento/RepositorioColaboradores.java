package folhapagamento;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface RepositorioColaboradores {
    void cadastrar(Colaborador colaborador) throws IOException;

    Optional<Colaborador> buscar(String matricula);

    List<Colaborador> listar();

    void atualizar(Colaborador colaborador) throws IOException;

    void excluir(String matricula) throws IOException;

    List<FolhaProcessada> processarFolha(int mes, int ano);

    List<FolhaProcessada> consultarFolha(int mes, int ano);

    Optional<FolhaProcessada> buscarFolha(String matricula, int mes, int ano);
}
