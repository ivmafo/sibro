
----Script Abril 19 del 2011
------------------------------------------------------------------RetentionRateAccountMenu(14/04/2011)--------------------------------------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 134, true);
INSERT INTO features (id, page) VALUES (134, 'RetentionRateAccountList.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 563, true);
INSERT INTO role_features (id, role, features) VALUES (563, 1, 134);


-----------------------------------------------------------------------Mandate(14/04/2011)-----------------------------------------------------------------------------------------------
ALTER TABLE project_property ADD COLUMN mandate BOOLEAN NOT NULL default 'false';
ALTER TABLE project_property ALTER COLUMN mandate DROP DEFAULT;


---------------------------------------------------------Impuesto del Timbre(18/04/2011)----------------------------------------------------------------------------------

ALTER TABLE concept ADD COLUMN stamptax INTEGER;

ALTER TABLE concept ADD CONSTRAINT retention_rate_account_concept_fk1 FOREIGN KEY (stamptax) REFERENCES retention_rate_account (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


---------------------------------------------------------script David (19/04/2011)-------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE invoice_concept ADD COLUMN ini_period_date date NOT NULL default '3000-01-01';
ALTER TABLE invoice_concept ADD COLUMN end_period_date date NOT NULL default '3000-01-01' ;

ALTER TABLE invoice_concept ALTER COLUMN ini_period_date DROP DEFAULT;
ALTER TABLE invoice_concept ALTER COLUMN end_period_date DROP DEFAULT;


ALTER TABLE invoice_concept ADD COLUMN expression_concept character varying NOT NULL default '4000';
ALTER TABLE invoice_concept ALTER COLUMN expression_concept DROP DEFAULT;

ALTER TABLE invoice_concept ADD COLUMN expression_increment character varying;

