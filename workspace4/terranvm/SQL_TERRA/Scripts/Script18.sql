
------------------------Script18 add 02/11/2011-----------------------------------

-----------------04-11-2011---------------WAMAYA--------------------------------------
ALTER TABLE concept ADD COLUMN bulk_load BOOLEAN DEFAULT false;


-----------------17-11-2011---------------JLOPEZ--------------------------------------
UPDATE retention_rate_account SET accounts_receivable_code = 0 WHERE accounts_receivable_code IS null;
ALTER TABLE retention_rate_account ALTER COLUMN accounts_receivable_code SET DEFAULT 0;


-----------------22-11-2011---------------WAMAYA--------------------------------------
SELECT setval('public.real_property_use_type_id_seq', 12, true);	
INSERT INTO features VALUES (324,'RealPropertyUseType.xhtml');
INSERT INTO role_features (role,features) values (1,324);
SELECT setval('public.role_features_id_seq', 1250, true);


-----------------------------DAVIDO Viernes 2 de Diciembre 2011  9:52 AM------------------
insert into features (id, page) values ((select max(id) from features)+1,'InvoiceProcesorManual.xhtml');
insert into role_features (id, role, features) values ((select max(id) from role_features)+1, 1, (select id from features where page = 'InvoiceProcesorManual.xhtml'));

----------------14-12-2011---------------WAMAYA-------------------------------------- 
ALTER TABLE invoice_concept ADD COLUMN credit_note_status SMALLINT DEFAULT 0 NOT NULL;

