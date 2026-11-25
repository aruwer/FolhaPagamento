-- RN005 do cliente: o percentual de comissao so nao pode ser negativo.
-- O teto de 100% da V1 era uma restricao adicional sem respaldo no requisito (BUG-007).
ALTER TABLE colaboradores
    DROP CONSTRAINT IF EXISTS colaboradores_percentual_comissao_check;

ALTER TABLE colaboradores
    ADD CONSTRAINT ck_colaborador_percentual_nao_negativo
    CHECK (percentual_comissao IS NULL OR percentual_comissao >= 0);
