// Regressão: CT-036 (filtro por mês e ano), reprocessamento idempotente e CT-037 (histórico após exclusão lógica)
// Cada spec usa uma competência própria: a tela só oferece "Processar folha" enquanto a competência não tem folha,
// e o Bruno (que roda antes) já processa o mês atual.
const hoje = new Date();
function competencia(deslocamentoEmMeses) {
  const d = new Date(hoje.getFullYear(), hoje.getMonth() + deslocamentoEmMeses, 1);
  return { MES: d.getMonth() + 1, ANO: d.getFullYear() };
}
const { MES, ANO } = competencia(1);   // regressão: um mês à frente
const brl = (v) => new RegExp('R\\$\\s*' + v.replace(/\./g, '\\.'));

function cadastrar(matricula, nome, salario) {
  cy.visit('/colaboradores/novo');
  cy.get('#matricula').type(matricula);
  cy.get('#nome').type(nome);
  cy.get('#salarioBase').type(salario);
  cy.contains('button', 'Cadastrar colaborador').click();
  cy.get('.notice-success').should('contain', 'Colaborador cadastrado.');
}

describe('Regressão CT-036 e CT-037', () => {
  it('filtra por competência, reprocessa sem duplicar e preserva o histórico após excluir', () => {
    cadastrar('CY036A', 'Regressao Alfa', '2000');
    cadastrar('CY036B', 'Regressao Beta', '1500');

    cy.visit(`/folha?mes=${MES}&ano=${ANO}`);
    cy.contains('button', 'Processar folha desta competência').click();
    cy.get('.notice-success').should('contain', 'Folha processada para');
    cy.contains('tr', 'CY036A').should('exist');
    cy.contains('tr', 'CY036B').should('exist');
    cy.screenshot('CT-036/passo1-competencia-processada');

    // CT-036: outro mês/ano não mostra as linhas
    cy.visit('/folha?mes=1&ano=2099');
    cy.contains('tr', 'CY036A').should('not.exist');
    cy.contains('tr', 'CY036B').should('not.exist');
    cy.screenshot('CT-036/passo2-outra-competencia-vazia');

    // A competência processada mantém uma única linha por colaborador
    cy.visit(`/folha?mes=${MES}&ano=${ANO}`);
    cy.get('tbody tr').filter(':contains("CY036A")').should('have.length', 1);

    // CT-037: exclusão lógica (o confirm() é aceito automaticamente pelo Cypress)
    cy.visit('/');
    cy.contains('tr', 'CY036A').contains('button', 'Excluir').click();
    cy.get('.notice-success').should('contain', 'Colaborador excluído.');
    cy.contains('tr', 'CY036A').should('not.exist');
    cy.screenshot('CT-037/passo1-excluido-some-do-painel');

    cy.visit(`/folha?mes=${MES}&ano=${ANO}`);
    cy.contains('a.person-link', 'Regressao Alfa').click();
    cy.get('.individual-total').invoke('text').should('match', brl('2.000,00'));
    cy.contains('Editar cadastro').should('not.exist');
    cy.screenshot('CT-037/passo2-demonstrativo-do-excluido');
  });
});
