-----------------------------------------Script 23-----------------------------------------------------

-------------------------------------wamaya 31-05-2012--------------------------------------------------

ALTER TABLE invoice_concept ADD COLUMN new_balance double precision;

CREATE OR REPLACE VIEW adjustments_to_balance as  SELECT rc.id,rc.value as balance, r.date , 0 as tipo_transaccion , rc.invoice_concept from recover_concept rc join recover r on rc.recover = r.id UNION ALL SELECT id, balance, last_liquidation_date as date , 1 as tipo_transaccion ,invoice_concept_parent as invoice_concept from invoice_concept where invoice_concept_type = 4;

-------------------------------------wamaya 07-06-2012--------------------------------------------------
UPDATE user_terranvm SET password = md5(password);

INSERT INTO system_configuration VALUES (3,'mostrar_boton_actualizar_new_balance','1');