ALTER TABLE colaboradores
    ADD COLUMN excluido_em TIMESTAMP WITH TIME ZONE;

UPDATE colaboradores
   SET excluido_em = atualizado_em
 WHERE ativo = FALSE;
