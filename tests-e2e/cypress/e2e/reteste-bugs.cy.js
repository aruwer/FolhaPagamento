// Reteste dos defeitos BUG-001, BUG-004, BUG-005 e BUG-006 pela interface (ver 10_Qualidade/Roteiro_Reteste_Bugs.md)
// Cada spec usa uma competência própria: a tela só oferece "Processar folha" enquanto a competência não tem folha,
// e o Bruno (que roda antes) já processa o mês atual.
const hoje = new Date();
function competencia(deslocamentoEmMeses) {
  const d = new Date(hoje.getFullYear(), hoje.getMonth() + deslocamentoEmMeses, 1);
  return { MES: d.getMonth() + 1, ANO: d.getFullYear() };
}
const { MES, ANO } = competencia(2);   // reteste: dois meses à frente
// O Java usa espaço não separável após "R$": \s casa com ele.
const brl = (v) => new RegExp('R\\$\\s*' + v.replace(/\./g, '\\.'));

describe('Reteste de defeitos', () => {
  it('BUG-001 — campos variáveis em branco valem zero (RN010)', () => {
    cy.visit('/colaboradores/novo');
    cy.get('#matricula').type('RT001');
    cy.get('#nome').type('Reteste Um');
    cy.get('#salarioBase').type('2000');
    cy.get('#tipo').select('COMISSIONADO');
    cy.get('#valorVendas').should('have.value', '');
    cy.get('#percentualComissao').should('have.value', '');
    cy.screenshot('BUG-001/passo1-formulario-em-branco');
    cy.contains('button', 'Cadastrar colaborador').click();
    cy.get('.notice-success').should('contain', 'Colaborador cadastrado.');
    cy.contains('tr', 'RT001').invoke('text').should('match', brl('2.000,00'));
    cy.screenshot('BUG-001/passo2-painel-com-RT001');
  });

  it('BUG-004 e BUG-005 — demonstrativo individual, adicional zerado, impressão e reprocessamento', () => {
    cy.visit(`/folha?mes=${MES}&ano=${ANO}`);
    cy.contains('button', 'Processar folha desta competência').click();
    cy.get('.notice-success').should('contain', 'Folha processada para');

    // BUG-005: depois de processada, a competência mostra uma única linha por colaborador, igual após recarregar
    cy.get('tbody tr').filter(':contains("RT001")').should('have.length', 1);
    cy.reload();
    cy.get('tbody tr').filter(':contains("RT001")').should('have.length', 1);
    cy.screenshot('BUG-005/folha-sem-duplicar');

    // BUG-004: demonstrativo individual
    cy.contains('a.person-link', 'Reteste Um').click();
    cy.contains('Demonstrativo individual', { matchCase: false }).should('exist');
    cy.get('.individual-line').eq(0).invoke('text').should('match', brl('2.000,00'));
    cy.get('.individual-line').eq(1).should('contain', 'Adicionais').invoke('text').should('match', brl('0,00'));
    cy.get('.individual-total').invoke('text').should('match', brl('2.000,00'));
    cy.contains('button', 'Imprimir demonstrativo').should('be.visible');
    cy.screenshot('BUG-004/passo1-demonstrativo-individual');

    // Impressão: o Cypress não abre o diálogo nativo, então confere as regras @media print da folha de estilos.
    // O PDF gerado pela impressão real (Edge) é salvo pelo script em evidencias-auto/impressao/.
    cy.document().then((doc) => {
      const regras = [];
      Array.from(doc.styleSheets).forEach((folha) => {
        try {
          Array.from(folha.cssRules).forEach((r) => {
            if (r.media && r.media.mediaText.includes('print')) {
              Array.from(r.cssRules).forEach((x) => regras.push(x.cssText));
            }
          });
        } catch (e) { /* folha de outra origem (fonte externa): ignora */ }
      });
      const css = regras.join('\n');
      cy.log('Regras @media print:\n' + css);
      ['.site-header', '.individual-actions', '.page-heading .button'].forEach((seletor) => {
        const regra = regras.find((x) => x.includes(seletor) && /display:\s*none/.test(x));
        expect(regra, `regra de impressão que oculta ${seletor}`).to.exist;
      });
    });
    cy.get('.individual-report').should('be.visible');
    cy.screenshot('BUG-004/passo2-demonstrativo-na-tela');
  });

  it('BUG-006 — competência não processada orienta a processar, não a cadastrar', () => {
    cy.visit('/folha?mes=1&ano=2099');
    cy.contains('ainda não foi processada').should('be.visible');
    cy.contains('Esta competência ainda não tem folha processada').should('be.visible');
    cy.contains('Processar folha desta competência').should('be.visible');
    cy.contains('Cadastre colaboradores para gerar o demonstrativo').should('not.exist');
    cy.screenshot('BUG-006/passo1-competencia-nao-processada');

    cy.visit('/folha?mes=13&ano=2099');
    cy.contains('Informe uma competência válida.').should('be.visible');
    cy.contains('Selecione um mês e um ano válidos').should('be.visible');
    cy.screenshot('BUG-006/passo2-competencia-invalida');
  });
});
