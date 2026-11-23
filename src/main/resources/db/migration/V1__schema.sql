CREATE TABLE colaboradores (
    id BIGSERIAL PRIMARY KEY,
    matricula VARCHAR(40) NOT NULL UNIQUE,
    nome VARCHAR(120) NOT NULL,
    tipo_colaborador VARCHAR(20) NOT NULL
        CHECK (tipo_colaborador IN ('PADRAO', 'COMISSIONADO', 'PRODUCAO')),
    salario_base NUMERIC(12, 2) NOT NULL CHECK (salario_base >= 0),
    valor_vendas NUMERIC(12, 2) CHECK (valor_vendas IS NULL OR valor_vendas >= 0),
    percentual_comissao NUMERIC(5, 2)
        CHECK (percentual_comissao IS NULL OR percentual_comissao BETWEEN 0 AND 100),
    quantidade_produzida NUMERIC(13, 3) CHECK (quantidade_produzida IS NULL OR quantidade_produzida >= 0),
    valor_por_unidade NUMERIC(12, 2) CHECK (valor_por_unidade IS NULL OR valor_por_unidade >= 0),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_colaborador_campos_por_tipo CHECK (
        (tipo_colaborador = 'PADRAO'
            AND valor_vendas IS NULL AND percentual_comissao IS NULL
            AND quantidade_produzida IS NULL AND valor_por_unidade IS NULL)
        OR
        (tipo_colaborador = 'COMISSIONADO'
            AND valor_vendas IS NOT NULL AND percentual_comissao IS NOT NULL
            AND quantidade_produzida IS NULL AND valor_por_unidade IS NULL)
        OR
        (tipo_colaborador = 'PRODUCAO'
            AND valor_vendas IS NULL AND percentual_comissao IS NULL
            AND quantidade_produzida IS NOT NULL AND valor_por_unidade IS NOT NULL)
    )
);

CREATE TABLE folhas_processadas (
    id BIGSERIAL PRIMARY KEY,
    colaborador_id BIGINT NOT NULL REFERENCES colaboradores(id),
    matricula_snapshot VARCHAR(40) NOT NULL,
    nome_snapshot VARCHAR(120) NOT NULL,
    tipo_snapshot VARCHAR(20) NOT NULL
        CHECK (tipo_snapshot IN ('PADRAO', 'COMISSIONADO', 'PRODUCAO')),
    mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    ano INTEGER NOT NULL CHECK (ano BETWEEN 2000 AND 9999),
    salario_base_snapshot NUMERIC(12, 2) NOT NULL CHECK (salario_base_snapshot >= 0),
    adicional_calculado NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (adicional_calculado >= 0),
    salario_final NUMERIC(12, 2) NOT NULL CHECK (salario_final >= 0),
    data_processamento TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_folha_colaborador_competencia UNIQUE (colaborador_id, mes, ano)
);

CREATE INDEX idx_colaboradores_ativos_matricula
    ON colaboradores (matricula) WHERE ativo = TRUE;
CREATE INDEX idx_folhas_competencia
    ON folhas_processadas (ano, mes);
