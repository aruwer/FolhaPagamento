// CT-034 (campos conforme a categoria) e CT-035 (validação compreensível) — ACE-1 e ACE-2
describe('CT-034 — campos conforme a categoria selecionada', () => {
  beforeEach(() => cy.visit('/colaboradores/novo'));

  it('Padrão: só dados cadastrais; sem campos de comissão ou produção', () => {
    cy.get('#tipo').select('PADRAO');
    cy.get('[data-type-hint="PADRAO"]').should('be.visible');
    cy.get('#valorVendas').should('not.be.visible');
    cy.get('#percentualComissao').should('not.be.visible');
    cy.get('#quantidadeProduzida').should('not.be.visible');
    cy.get('#valorPorUnidade').should('not.be.visible');
    cy.screenshot('CT-034/passo1-padrao');
  });

  it('Comissionado: vendas e comissão visíveis; produção oculta', () => {
    cy.get('#tipo').select('COMISSIONADO');
    cy.get('#valorVendas').should('be.visible');
    cy.get('#percentualComissao').should('be.visible');
    cy.get('#quantidadeProduzida').should('not.be.visible');
    cy.get('#valorPorUnidade').should('not.be.visible');
    cy.screenshot('CT-034/passo2-comissionado');
  });

  it('Produção: quantidade e valor por unidade visíveis; comissão oculta', () => {
    cy.get('#tipo').select('PRODUCAO');
    cy.get('#quantidadeProduzida').should('be.visible');
    cy.get('#valorPorUnidade').should('be.visible');
    cy.get('#valorVendas').should('not.be.visible');
    cy.get('#percentualComissao').should('not.be.visible');
    cy.screenshot('CT-034/passo3-producao');
  });
});

describe('CT-035 — validação compreensível dos campos obrigatórios', () => {
  it('bloqueia o envio com nome e salário em branco e mantém o formulário', () => {
    cy.visit('/colaboradores/novo');
    cy.get('#matricula').type('CY035');
    cy.contains('button', 'Cadastrar colaborador').click();
    cy.location('pathname').should('eq', '/colaboradores/novo');
    cy.get('#nome:invalid').should('exist');
    cy.get('#salarioBase:invalid').should('exist');
    cy.get('#nome').then(($el) => expect($el[0].validationMessage).to.not.be.empty);
    cy.screenshot('CT-035/passo1-campos-obrigatorios');
  });

  it('CT-010 (UI): o campo matrícula descarta pontuação digitada', () => {
    cy.visit('/colaboradores/novo');
    cy.get('#matricula').type('CY-0.10 x').should('have.value', 'CY010x');
    cy.screenshot('CT-010/matricula-sem-pontuacao');
  });
});
