
----Script nuemro 5 empezado a llenar el 13 de abril de 2011



------------------------------------------------------------Matriz_Impuestos(13/04/2011) ya se corrio-----------------------------------------------------------------------------------------------
-----Llenado tabla tax configuration
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 4, NULL, 11, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 11, 11);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 11, 13);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 13, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 4, NULL, 13, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 13, 11);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 4, NULL, 13, 11);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 13, 13);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 11, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 15, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 4, NULL, 15, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 15, 11);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 4, NULL, 15, 11);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 15, 13);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 20, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 20, 11);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 20, 13);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 20, 15);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 20, 20);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 23, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 4, NULL, 23, 12);
INSERT INTO tax_configuration (name, retention_rate, taxliabilities, billed, biller) VALUES (NULL, 1, NULL, 23, 11);

------------------------------------------------------------------PaymentFormMenu(13/04/2011) ya se corrio-------------------------------------------------------------------------------------------------------
---Permiso para el administrador del menu cuentras por cobrar(PaymentForm)

SELECT pg_catalog.setval('features_id_seq', 133, true);
INSERT INTO features (id, page) VALUES (133, 'PaymentFormList.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 562, true);
INSERT INTO role_features (id, role, features) VALUES (562, 1, 133);

----------------------------------------------------------------------------Acounts(13/04/2011) Ya se corrio-----------------------------------------------------------------------------------------------------------
---Cuentas contables

ALTER TABLE concept ADD COLUMN accounting_accounts_recover VARCHAR;

ALTER TABLE concept ADD COLUMN accounting_accounts_early_payment  VARCHAR;

ALTER TABLE concept ADD COLUMN accounting_credit_accounts VARCHAR;


------------------------------------------------------------------RetentionRateAccountMenu(14/04/2011)--------------------------------------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 134, true);
INSERT INTO features (id, page) VALUES (134, 'RetentionRateAccountList.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 563, true);
INSERT INTO role_features (id, role, features) VALUES (563, 1, 134);

-----------------------------------------------------------------------Mandate(14/04/2011)-----------------------------------------------------------------------------------------------
ALTER TABLE project_property ADD COLUMN mandate BOOLEAN NOT NULL default 'false';
ALTER TABLE project_property ALTER COLUMN mandate DROP DEFAULT;




--------------------------------------------------------Script para arreglar error de llave foranea paymetnForm (15/04/2011) Ya se corrio-------------------------------------------------
ALTER TABLE concept DROP CONSTRAINT payment_form_concept_fk;
ALTER TABLE concept ADD CONSTRAINT payment_form_concept_fk FOREIGN KEY (payment_form) REFERENCES payment_form (id) ON UPDATE NO ACTION ON DELETE NO ACTION;


---------------------------------------------------------UpdateSintasisFormulas(BDTerranum) (15/04/2011) Ya se corrio----------------------------------------------------------------------------------
﻿--- Update table sintasis del nombre
UPDATE system_variable set sintax='IPCY[YEAR]' where id=1
UPDATE system_variable set sintax='IPCM[MONTH]' where id=2


---------------------------------------------------------Impuesto del Timbre(18/04/2011)----------------------------------------------------------------------------------

ALTER TABLE concept ADD COLUMN stamptax INTEGER;

ALTER TABLE concept ADD CONSTRAINT retention_rate_account_concept_fk1 FOREIGN KEY (stamptax) REFERENCES retention_rate_account (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------