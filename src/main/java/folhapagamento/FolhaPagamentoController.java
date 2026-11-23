package folhapagamento;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class FolhaPagamentoController {
    private final FolhaPagamentoService service;

    public FolhaPagamentoController(FolhaPagamentoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        YearMonth competencia = YearMonth.now();
        model.addAttribute("colaboradores", service.listar());
        model.addAttribute("resumo", service.gerarResumo());
        model.addAttribute("mes", competencia.getMonthValue());
        model.addAttribute("ano", competencia.getYear());
        return "index";
    }

    @GetMapping("/colaboradores/novo")
    public String novo(Model model) {
        return exibirFormulario(model, new ColaboradorForm(), false);
    }

    @PostMapping("/colaboradores")
    public String cadastrar(@Valid @ModelAttribute("form") ColaboradorForm form, BindingResult resultado,
            Model model, RedirectAttributes redirect) throws IOException {
        if (resultado.hasErrors()) {
            return exibirFormulario(model, form, false);
        }
        Colaborador colaborador;
        try {
            colaborador = form.construirColaborador();
        } catch (IllegalArgumentException erro) {
            model.addAttribute("erro", erro.getMessage());
            return exibirFormulario(model, form, false);
        }
        try {
            service.cadastrar(colaborador);
            redirect.addFlashAttribute("sucesso", "Colaborador cadastrado.");
            return "redirect:/";
        } catch (IllegalArgumentException erro) {
            resultado.rejectValue("matricula", "matricula.duplicada", erro.getMessage());
            return exibirFormulario(model, form, false);
        }
    }

    @GetMapping("/colaboradores/{matricula}/editar")
    public String editar(@PathVariable String matricula, Model model, RedirectAttributes redirect) {
        return service.buscar(matricula).map(colaborador -> exibirFormulario(model,
                ColaboradorForm.de(colaborador), true)).orElseGet(() -> {
                    redirect.addFlashAttribute("erro", "Colaborador não encontrado.");
                    return "redirect:/";
                });
    }

    @PostMapping("/colaboradores/{matricula}")
    public String atualizar(@PathVariable String matricula,
            @Valid @ModelAttribute("form") ColaboradorForm form, BindingResult resultado,
            Model model, RedirectAttributes redirect) throws IOException {
        form.setMatricula(matricula);
        if (resultado.hasErrors()) {
            return exibirFormulario(model, form, true);
        }
        Colaborador colaborador;
        try {
            colaborador = form.construirColaborador();
        } catch (IllegalArgumentException erro) {
            model.addAttribute("erro", erro.getMessage());
            return exibirFormulario(model, form, true);
        }
        try {
            service.atualizar(colaborador);
            redirect.addFlashAttribute("sucesso", "Cadastro atualizado.");
            return "redirect:/";
        } catch (IllegalArgumentException erro) {
            model.addAttribute("erro", erro.getMessage());
            return exibirFormulario(model, form, true);
        }
    }

    @PostMapping("/colaboradores/{matricula}/excluir")
    public String excluir(@PathVariable String matricula, RedirectAttributes redirect) throws IOException {
        try {
            service.excluir(matricula);
            redirect.addFlashAttribute("sucesso", "Colaborador excluído.");
        } catch (IllegalArgumentException erro) {
            redirect.addFlashAttribute("erro", erro.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/folha")
    public String folha(@RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano, Model model) {
        YearMonth competencia = competencia(mes, ano);
        if (competencia == null) {
            model.addAttribute("colaboradores", List.of());
            model.addAttribute("resumo", resumoVazio());
            model.addAttribute("erro", "Informe uma competência válida.");
            model.addAttribute("mes", mes);
            model.addAttribute("ano", ano);
            return "folha";
        }
        List<FolhaProcessada> folha = service.consultarFolha(
                competencia.getMonthValue(), competencia.getYear());
        model.addAttribute("colaboradores", folha);
        model.addAttribute("resumo", service.resumir(folha));
        model.addAttribute("mes", competencia.getMonthValue());
        model.addAttribute("ano", competencia.getYear());
        if (folha.isEmpty()) {
            model.addAttribute("aviso", "A folha de " + formatar(competencia)
                    + " ainda não foi processada.");
        }
        return "folha";
    }

    @PostMapping("/folha/processar")
    public String processarFolha(@RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano, RedirectAttributes redirect) {
        YearMonth competencia = competencia(mes, ano);
        if (competencia == null) {
            redirect.addFlashAttribute("erro", "Informe uma competência válida.");
            return "redirect:/folha";
        }
        service.processarFolha(competencia.getMonthValue(), competencia.getYear());
        redirect.addFlashAttribute("sucesso", "Folha processada para " + formatar(competencia) + ".");
        return "redirect:/folha?mes=" + competencia.getMonthValue() + "&ano=" + competencia.getYear();
    }

    @GetMapping("/folha/{matricula}")
    public String folhaIndividual(@PathVariable String matricula,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano, Model model, RedirectAttributes redirect) {
        YearMonth competencia = competencia(mes, ano);
        if (competencia == null) {
            redirect.addFlashAttribute("erro", "Informe uma competência válida.");
            return "redirect:/";
        }
        return service.buscarFolha(matricula, competencia.getMonthValue(), competencia.getYear())
                .map(folha -> {
                    model.addAttribute("folha", folha);
                    model.addAttribute("competencia", competencia);
                    model.addAttribute("colaboradorAtivo", service.buscar(matricula).isPresent());
                    return "folha-individual";
                }).orElseGet(() -> {
                    if (service.buscar(matricula).isPresent()) {
                        redirect.addFlashAttribute("erro", "A folha desta competência ainda não foi processada.");
                        return "redirect:/folha?mes=" + competencia.getMonthValue()
                                + "&ano=" + competencia.getYear();
                    }
                    redirect.addFlashAttribute("erro", "Colaborador não encontrado.");
                    return "redirect:/";
                });
    }

    private YearMonth competencia(Integer mes, Integer ano) {
        if (mes == null && ano == null) {
            return YearMonth.now();
        }
        if (mes == null || ano == null || ano < 2000 || ano > 9999) {
            return null;
        }
        try {
            return YearMonth.of(ano, mes);
        } catch (java.time.DateTimeException erro) {
            return null;
        }
    }

    private String formatar(YearMonth competencia) {
        return String.format("%02d/%d", competencia.getMonthValue(), competencia.getYear());
    }

    private ResumoFolha resumoVazio() {
        return new ResumoFolha(0, java.math.BigDecimal.ZERO, java.util.Map.of());
    }

    private String exibirFormulario(Model model, ColaboradorForm form, boolean edicao) {
        model.addAttribute("form", form);
        model.addAttribute("edicao", edicao);
        return "colaboradores/form";
    }
}
