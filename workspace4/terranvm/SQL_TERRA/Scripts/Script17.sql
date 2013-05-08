----------------------------Script numero 17 add 16/09/2011--------------------------------------------------------------------------------


-------------------- JLOPEZ (Sep 28st 2011 18:00)-----------------------------
ALTER TABLE concept ADD COLUMN automatic_extension BOOLEAN DEFAULT false NOT NULL;



-------------------- DAVIDO (Oct 10th 2011 17:24)-----------------------------
CREATE TABLE days_for_early_payment (
id INTEGER NOT NULL,
month varchar NOT NULL,
days INTEGER NOT NULL default 0,
PRIMARY KEY (id)
);
insert into days_for_early_payment (id, month, days) values
(0, 'ENERO', 7),
(1, 'FEBRERO', 7),
(2, 'MARZO', 7),
(3, 'ABRIL', 7),
(4, 'MAYO', 7),
(5, 'JUNIO', 7),
(6, 'JULIO', 7),
(7, 'AGOSTO', 7),
(8, 'SEPTIEMBRE', 7),
(9, 'OCTUBRE', 7),
(10, 'NOVIEMBRE', 7),
(11, 'DICIEMBRE', 17);
ALTER TABLE concept ADD COLUMN immediate_payment_state INTEGER NOT NULL DEFAULT 0;

-------------------- DAVIDO (Oct 14th 2011 10:12)-----------------------------
CREATE TABLE reports (
id SERIAL NOT NULL,
name varchar NOT NULL,
description varchar NOT NULL,
link varchar NOT NULL,
report_for_all_projects boolean NOT NULL,
PRIMARY KEY (id)
);
insert into features (id, page) values ((select max(id) from features)+1,'Reports.xhtml');
insert into role_features (id, role, features) values ((select max(id) from role_features)+1, 1, (select id from features where page = 'Reports.xhtml'));



---------------------WAMAYA (Oct 6th 2541 17:06) se crea el perfil:Perfil Creación de Hojas de Términos PEI-----------------------------
----------------------------Script numero 17 add 16/09/2011--------------------------------------------------------------------------------DAVIDO (Oct 14th 2011 10:12)-----------------------------


---------------------WAMAYA (Oct 6th 2541 17:06) se crea el perfil:Perfil Creación de Hojas de Términos PEI-----------------------------
SELECT setval('public.action_id_seq', 1071, true);
SELECT setval('public.features_id_seq', 317, true);
SELECT setval('public.role_id_seq', 50, true);
SELECT setval('public.role_features_id_seq', 1202, true);


insert into features(id,page) values (300,'Collection.xhtml');
insert into action (features,action) values(300,'search'); 
insert into action (features,action) values(300,'reset');
insert into role_features (role,features) values(1,300);
insert into role_features (role,features) values(1,300);

INSERT INTO role values(40,'Perfil Creación de Hojas de Términos PEI');
insert into features (id,page) values(301,'BusinessEntityList.xhtml');
insert into action(features,action) values(301,'create');
insert into features (id,page) values(302,'BusinessEntityList.xhtml');
insert into action(features,action) values(302,'addContactLegalRepresentative'); 
insert into action(features,action) values(302,'addphoneNumber');
insert into action(features,action) values(302,'addaddress' );                                     
insert into action(features,action) values(302,'addproject' );                                        
insert into action(features,action) values(302,'addbusinessLine');
insert into action(features,action) values(302,'addbusinessEntityContact');
insert into action(features,action) values(302,'DisableEntityTypeField');    
insert into action(features,action) values(302,'save'); 
insert into features (id,page) values(303,'BusinessEntityList.xhtml');
insert into action(features,action) values(303,'edit' );
insert into action(features,action) values(303,'done');
insert into features (id,page) values(304,'ProjectPropertyEdit.xhtml');  
insert into features (id,page) values(306,'ProjectPropertyList.xhtml');                                           
insert into action(features,action) values(306,'projectPropertyViewId');                                              
insert into action(features,action) values(306,'projectPropertyEdit');                                                   
insert into action(features,action) values(306,'nextPage');                              
insert into action(features,action) values(306,'previousPage');                                    
insert into action(features,action) values(306,'lastPage');       
insert into action(features,action) values(306,'firstPage');        
insert into action(features,action) values(306,'create');
insert into features (id,page) values(312,'ProjectProperty.xhtml');
insert into action(features,action) values(312,'edit');
insert into action(features,action) values(312,'done');
insert into features (id,page) values(307,'SalesListTab.xhtml');
insert into features (id,page) values(305,'RentableUnitOwnerLesseeList.xhtml');
insert into action(features,action) values(305,'assignment') ;
insert into features (id,page) values(308,'PhoneNumberEdit.xhtml');
insert into features (id,page) values(309,'PhoneNumber.xhtml');
insert into features (id,page) values(311,'AddressEdit.xhtml');
insert into features (id,page) values(310,'Address.xhtml');
INSERT INTO role_features (role,features) (SELECT 40,id from features where id between 301 and 312);
INSERT INTO role_features (role,features) VALUES (40,1);
INSERT INTO role_features (role,features) VALUES (40,2);

INSERT INTO features (id,page)values(315,'BusinessEntity.xhtml');
INSERT INTO features (id,page)values(314,'BusinessEntityEdit.xhtml');
INSERT INTO action (features,action) values (314,'isbillerField');
INSERT INTO action (features,action) values (314,'imageField');
INSERT INTO features (id,page)values(313,'BusinessEntityList.xhtml');
INSERT INTO action (features,action) values (313,'search');
INSERT INTO action (features,action) values (313,'reset');
INSERT INTO action (features,action) values (313,'businessEntityViewId');
 	

-------------------- Jlopez (Oct 27th 2011 03:57)-----------------------------
update increased set periodicity_type = 1 where periodicity_type = 0;

 	

-------------------- DAVIDO (Oct 27th 2011 09:14)-----------------------------
ALTER TABLE group_cache DROP COLUMN business_entity;
ALTER TABLE group_cache DROP COLUMN grouped_historical;
ALTER TABLE group_cache DROP COLUMN address_billed;

-------------------- Jlopez (Oct 27th 2011 03:57)-----------------------------
update increased set periodicity_type = 1 where periodicity_type = 0;

----------------------WAMAYA (Nov 11th 2011 1:57)-----------------------------
SELECT setval('public.role_features_id_seq', 1201, true); 
INSERT INTO features values(320,'BillingList.xhtml');
INSERT INTO role_features(role,features) VALUES (14,320);
INSERT INTO role_features (role,features)(SELECT role,136 from role_features where features in(95,118));
INSERT INTO role_features (role,features)(SELECT role,320 from role_features where features in(95,118));
INSERT INTO action (features , action) values(74,'valid');
INSERT INTO action (features , action) values(74,'approved_reversal');
INSERT INTO action (features , action) values(74,'reversible');
INSERT INTO action (features , action) values(74,'unapproved_invoices');
INSERT INTO action (features , action) values(74,'reversal_request');
INSERT INTO features VALUES (321,'InvoiceList.xhtml');
INSERT INTO action (features,action)(SELECT 320,action from action where features=95 and action in('search','reset', 'Reversion', 'openModalPanelInvoice', 'openModalPanelNews', 'openModalPanelProjectClosure', 'openModalPanelGroup', 'openModalPanelInvoiceConcept', 'closeModalPanelInvoice', 'nextPage'));
INSERT INTO action (features,action)(SELECT 136,action from action where features=95 and action in('search','reset', 'Reversion', 'openModalPanelInvoice', 'openModalPanelNews', 'openModalPanelProjectClosure', 'openModalPanelGroup', 'openModalPanelInvoiceConcept', 'closeModalPanelInvoice', 'nextPage'));
INSERT INTO role_features (role,features)values (14,321);
INSERT INTO action (features ,action) values (321,'valid');

insert into features (id,page)values(323,'RealPropertyEdit.xhtml');
insert into features (id,page)values(322,'RealPropertyList.xhtml');
INSERT into role VALUES (36,'Servicios Públicos');
insert INTO role_features (features ,role)values(322,36);
insert INTO role_features (features ,role)values(323,36);
INSERT INTO action (features ,action) values (322,'search');
INSERT INTO action (features ,action) values (322,'reset');
INSERT INTO action (features ,action) values (322,'realPropertyEdit');
INSERT INTO action (features ,action) values (322,'nextPage');
INSERT INTO action (features ,action) values (322,'previousPage');
INSERT INTO action (features ,action) values (322,'lastPage');
INSERT INTO action (features ,action) values (322,'firstPage');
INSERT INTO action (features ,action) values (323,'creditNotesTab');
SELECT setval('public.features_id_seq', 324, true);
SELECT setval('public.role_id_seq', 36, true);

UPDATE retention_rate set name ='Deudoras de Control - Por Contra' where id=1110;

----------------------WAMAYA (Nov 15th 2011 2:47)-----------------------------
UPDATE project_property set number_periods_extension=1 where number_periods_extension is null and is_automatic_extension=true;
UPDATE project_property set number_periods_extension=0 where number_periods_extension is null and is_automatic_extension=false;
ALTER TABLE project_property ALTER COLUMN number_periods_extension SET DEFAULT 0;


-------------------- DAVIDO (Oct 31st 2011 08:35)-----------------------------
ALTER TABLE invoice_concept ADD COLUMN credit_note_status SMALLINT DEFAULT 0 NOT NULL;


