// BUG-007 — RN005: o percentual de comissão só não pode ser negativo (sem teto de 100%)
describe('BUG-007 — comissão acima de 100% (RN005)', () => {
  it('aceita 150% no formulário e mostra o colaborador cadastrado com salário final R$ 4.000,00', () => {
    cy.visit('/colaboradores/novo');
    cy.get('#matricula').type('CY007');
    cy.get('#nome').type('Comissao Cento e Cinquenta');
    cy.get('#salarioBase').type('1000');
    cy.get('#tipo').select('COMISSIONADO');
    cy.get('#valorVendas').type('2000');
    cy.get('#percentualComissao').type('150').should('have.value', '150');
    cy.get('#percentualComissao:invalid').should('not.exist');
    cy.screenshot('BUG-007/passo1-formulario-150');
    cy.contains('button', 'Cadastrar colaborador').click();
    cy.location('pathname').should('not.eq', '/colaboradores/novo');
    cy.contains('CY007').should('be.visible');
    cy.contains(/R\$\s*4\.000,00/).should('be.visible');
    cy.screenshot('BUG-007/passo2-cadastro-aceito');
  });

  it('continua rejeitando percentual negativo (o campo não aceita menos que zero)', () => {
    cy.visit('/colaboradores/novo');
    cy.get('#tipo').select('COMISSIONADO');
    cy.get('#percentualComissao').type('-1');
    cy.get('#percentualComissao:invalid').should('exist');
    cy.screenshot('BUG-007/passo3-negativo-rejeitado');
  });
});
