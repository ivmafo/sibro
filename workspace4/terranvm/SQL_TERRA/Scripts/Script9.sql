-------Script numero 9 add 19/05/2011--------------------------------------------

---------------------------------Adición de campo descripción en makerchecker y la asociación con projectos (Mayo 19 de 2011) JL --------

ALTER TABLE maker_checker ADD COLUMN description VARCHAR;

CREATE SEQUENCE maker_checker_x_project_id_seq;

CREATE TABLE maker_checker_x_project (
                id INTEGER NOT NULL DEFAULT nextval('maker_checker_x_project_id_seq'),
                maker_ckecker INTEGER NOT NULL,
                project INTEGER NOT NULL,
                CONSTRAINT maker_checker_x_project_pk PRIMARY KEY (id)
);


ALTER SEQUENCE maker_checker_x_project_id_seq OWNED BY maker_checker_x_project.id;

ALTER TABLE maker_checker_x_project ADD CONSTRAINT maker_checker_maker_checker_x_project_fk
FOREIGN KEY (maker_ckecker)
REFERENCES maker_checker (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE maker_checker_x_project ADD CONSTRAINT project_maker_checker_x_project_fk
FOREIGN KEY (project)
REFERENCES project (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

---------------------------------Tablas para manipuación de Asobancaria (Mayo 24 de 2011) JL --------

CREATE SEQUENCE asobancaria_field_id_seq;

CREATE TABLE asobancaria_field (
                id INTEGER NOT NULL DEFAULT nextval('asobancaria_field_id_seq'),
                name VARCHAR NOT NULL,
                CONSTRAINT asobancaria_field_pk PRIMARY KEY (id)
);


ALTER SEQUENCE asobancaria_field_id_seq OWNED BY asobancaria_field.id;

CREATE SEQUENCE asobancaria_format_id_seq;

CREATE TABLE asobancaria_format (
                id INTEGER NOT NULL DEFAULT nextval('asobancaria_format_id_seq'),
                name VARCHAR NOT NULL,
                separator VARCHAR,
                CONSTRAINT asobancaria_format_pk PRIMARY KEY (id)
);


ALTER SEQUENCE asobancaria_format_id_seq OWNED BY asobancaria_format.id;

CREATE SEQUENCE asobancaria_items_id_seq;

CREATE TABLE asobancaria_items (
                id INTEGER NOT NULL DEFAULT nextval('asobancaria_items_id_seq'),
                asobancaria_field INTEGER NOT NULL,
                asobancaria_format INTEGER NOT NULL,
                ini_pos INTEGER NOT NULL,
                end_pos INTEGER,
                CONSTRAINT asobancaria_items_pk PRIMARY KEY (id)
);


ALTER SEQUENCE asobancaria_items_id_seq OWNED BY asobancaria_items.id;

ALTER TABLE asobancaria_items ADD CONSTRAINT asobancaria_field_asobancaria_items_fk
FOREIGN KEY (asobancaria_field)
REFERENCES asobancaria_field (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
ALTER TABLE asobancaria_items ADD CONSTRAINT asobancaria_format_asobancaria_items_fk
FOREIGN KEY (asobancaria_format)
REFERENCES asobancaria_format (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE asobancaria_items ADD COLUMN pos INTEGER;
ALTER TABLE asobancaria_format ADD COLUMN check_identification VARCHAR NOT NULL;

ALTER TABLE asobancaria_format ADD COLUMN number_of_footer_lines INTEGER NOT NULL;
ALTER TABLE asobancaria_format ADD COLUMN number_of_header_lines INTEGER NOT NULL;

---------------------------------Se crea permisos para ingresar a la página de recaudo automático (Mayo 24 de 2011) JL --------

INSERT INTO features(id,page) VALUES (155,'RecoverAutomatic.xhtml');

INSERT INTO role_features("role", features) VALUES (1,155);

---------------------------------Se adicionan campos en el recaudo para el manejo de recaudos automáticos (Mayo 26 de 2011) JL --------

ALTER TABLE recover ADD COLUMN office VARCHAR;

ALTER TABLE recover ADD COLUMN pay_identification VARCHAR;

ALTER TABLE recover ADD COLUMN payer_identification VARCHAR;

-------------------------------------------------------------------------------------------------------------------------
--------------------pemisos para las paginas de Licencias de construcion y Tramos (26/05/2009)------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 161, true);
INSERT INTO features (id, page) VALUES (156, 'ConstructionLicensesList.xhtml');
INSERT INTO features (id, page) VALUES (157, 'ConstructionLicensesEdit.xhtml');
INSERT INTO features (id, page) VALUES (158, 'ConstructionLicenses.xhtml');

INSERT INTO features (id, page) VALUES (159, 'SegmentStageList.xhtml');
INSERT INTO features (id, page) VALUES (160, 'SegmentStageEdit.xhtml');
INSERT INTO features (id, page) VALUES (161, 'SegmentStage.xhtml');



SELECT pg_catalog.setval('role_features_id_seq', 591, true);
INSERT INTO role_features (id, role, features) VALUES (586, 1, 156);
INSERT INTO role_features (id, role, features) VALUES (587, 1, 157);
INSERT INTO role_features (id, role, features) VALUES (588, 1, 158);

INSERT INTO role_features (id, role, features) VALUES (589, 1, 159);
INSERT INTO role_features (id, role, features) VALUES (590, 1, 160);
INSERT INTO role_features (id, role, features) VALUES (591, 1, 161);

--------------------------------------------------------------------Agregar columas en Unidad arrendable (26/05/2011)------------------------------------------
ALTER TABLE rentable_unit ADD COLUMN address_owner INTEGER;
ALTER TABLE rentable_unit ADD COLUMN address_lessee INTEGER;
ALTER TABLE rentable_unit ADD COLUMN phonenumbe_owner INTEGER;
ALTER TABLE rentable_unit ADD COLUMN phonenumber_lessee INTEGER;


ALTER TABLE rentable_unit ADD CONSTRAINT address_rentable_unit_fk
FOREIGN KEY (address_owner)
REFERENCES address (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE rentable_unit ADD CONSTRAINT address_rentable_unit_fk1
FOREIGN KEY (address_lessee)
REFERENCES address (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE rentable_unit ADD CONSTRAINT phone_number_rentable_unit_fk
FOREIGN KEY (phonenumbe_owner)
REFERENCES phone_number (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE rentable_unit ADD CONSTRAINT phone_number_rentable_unit_fk1
FOREIGN KEY (phonenumber_lessee)
REFERENCES phone_number (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

-----------------------------------------------Tabla Consecutivo de Cuentas de Recaudo (30/05/2011)---------------------------------------------
CREATE SEQUENCE consecutive_collection_account_id_seq;


CREATE TABLE terranvm_db.public.consecutive_collection_account (
                id INTEGER NOT NULL,
                suffix VARCHAR,
                prefix VARCHAR,
                siigo_code VARCHAR NOT NULL,
                min INTEGER NOT NULL,
                business_entity INTEGER NOT NULL,
                CONSTRAINT consecutive_collection_account_id PRIMARY KEY (id)
);

ALTER SEQUENCE consecutive_collection_account_id_seq OWNED BY consecutive_collection_account.id;


ALTER TABLE terranvm_db.public.consecutive_collection_account ADD CONSTRAINT business_entity_consecutive_collection_account_fk
FOREIGN KEY (business_entity)
REFERENCES terranvm_db.public.business_entity (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

----------------------------------------------Permisos para las paginas de cuentas de recaudo (30/05/10)---------------------------
SELECT pg_catalog.setval('features_id_seq', 166, true);
INSERT INTO features (id, page) VALUES (164, 'ConsecutiveCollectionAccountEdit.xhtml');
INSERT INTO features (id, page) VALUES (165, 'ConsecutiveCollectionAccount.xhtml');
INSERT INTO features (id, page) VALUES (166, 'ConsecutiveCollectionAccountList.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 594, true);
INSERT INTO role_features (id, role, features) VALUES (594, 1, 164);
INSERT INTO role_features (id, role, features) VALUES (595, 1, 165);
INSERT INTO role_features (id, role, features) VALUES (596, 1, 166);

-------------------------------------------------(30-05-2011)Descuento---(AC)----------------------------------------
--Indica si la nota credito va por descuento o no.

ALTER TABLE terranvm_db.public.invoice_concept ADD COLUMN discount BOOLEAN;


-----------------------------------------------------------------------(30-05-2011)CreditNoteReason-(AC)------------------------------------------------------------------------

ALTER TABLE terranvm_db.public.invoice_concept ADD COLUMN reason VARCHAR;

-----------------------------------------------------------------------(31-05-2011) Llenado de los campos necesrios para los formatos de Asobancarios-(JL)------------------------------------------------------------------------
INSERT INTO asobancaria_field (id, name) VALUES (1, 'Identificación del pagador');
INSERT INTO asobancaria_field (id, name) VALUES (2, 'Oficina de Recaudo');
INSERT INTO asobancaria_field (id, name) VALUES (3, 'Número de la cuenta a debitar');
INSERT INTO asobancaria_field (id, name) VALUES (4, 'Valor de la transacción');
INSERT INTO asobancaria_field (id, name) VALUES (5, 'Fecha de vencimiento o aplicación');
INSERT INTO asobancaria_field (id, name) VALUES (6, 'Tipo de transacción');
-----------------------------------------------------------------------(31-05-2011) Llenado para el formato asobancacrio de Bancolombia-(JL)------------------------------------------------------------------------
INSERT INTO asobancaria_format (name, separator, check_identification, number_of_footer_lines, number_of_header_lines) VALUES ('Bancolombia', NULL, '89', 1, 1)

INSERT INTO asobancaria_items (asobancaria_field, asobancaria_format, ini_pos, end_pos, pos) VALUES (1, (SELECT id FROM asobancaria_format WHERE name = 'Bancolombia'), 2, 14, 2);
INSERT INTO asobancaria_items (asobancaria_field, asobancaria_format, ini_pos, end_pos, pos) VALUES (2, (SELECT id FROM asobancaria_format WHERE name = 'Bancolombia'), 191, 194, 19);
INSERT INTO asobancaria_items (asobancaria_field, asobancaria_format, ini_pos, end_pos, pos) VALUES (3, (SELECT id FROM asobancaria_format WHERE name = 'Bancolombia'), 44, 60, 5);
INSERT INTO asobancaria_items (asobancaria_field, asobancaria_format, ini_pos, end_pos, pos) VALUES (4, (SELECT id FROM asobancaria_format WHERE name = 'Bancolombia'), 63, 79, 7);
INSERT INTO asobancaria_items (asobancaria_field, asobancaria_format, ini_pos, end_pos, pos) VALUES (5, (SELECT id FROM asobancaria_format WHERE name = 'Bancolombia'), 175, 182, 14);
INSERT INTO asobancaria_items (asobancaria_field, asobancaria_format, ini_pos, end_pos, pos) VALUES (6, (SELECT id FROM asobancaria_format WHERE name = 'Bancolombia'), 61, 62, 6);



---------------------------------------------------------(2011-06-02)---Borrado Departamento Repetido(Caldas)--(AC)------------------------------------------------------------------------------
DELETE FROM region WHERE id = 667;


--------------------------------------------------------------(2011-06-02)---Nuevos Roles---(AC)---------------------------------------------------------------------------------
SELECT pg_catalog.setval('role_id_seq', 7, true);

INSERT INTO role (id, role_name) VALUES (6, 'Activo');
INSERT INTO role (id, role_name) VALUES (7, 'Aprobador Activo');

--------------------------------------------------------------(2011-06-02)---Nuevos Usuarios---(AC)---------------------------------------------------------------------------------

SELECT pg_catalog.setval('user_terranvm_id_seq', 28, true);

INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (26, 'activo', 'activo', '3000-01-01', 1, 'user5@asdf.com', 'Nombre6', 6);
INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (28, 'aprovador', 'aprovador', '3000-01-01', 1, 'usesr6@asdf.com', 'Nombre7', 7);


-------------------------------------------(2011-06-02)---features----Nuevas Pagina de BusinessEntityEdit con nuevos permisos---(AC)--------------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 170, true);

INSERT INTO features (id, page) VALUES (167, 'RealProperty.xhtml');
INSERT INTO features (id, page) VALUES (168, 'BusinessEntity.xhtml');
INSERT INTO features (id, page) VALUES (170, 'BusinessEntityEdit.xhtml');


-------------------------------------------(2011-06-02)---Action---Permisos de los botones de BusinessEntityEdit---(AC)------------------------------------------------------------------------

SELECT pg_catalog.setval('action_id_seq', 1010, true);

INSERT INTO action (id, features, action) VALUES (978, 170, 'addContactLegalRepresentative');
INSERT INTO action (id, features, action) VALUES (980, 170, 'save');
INSERT INTO action (id, features, action) VALUES (981, 170, 'update');
INSERT INTO action (id, features, action) VALUES (982, 170, 'delete');
INSERT INTO action (id, features, action) VALUES (983, 170, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (984, 170, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (985, 170, 'xxxx');
INSERT INTO action (id, features, action) VALUES (986, 170, 'addphoneNumber');
INSERT INTO action (id, features, action) VALUES (988, 170, 'addaddress');
INSERT INTO action (id, features, action) VALUES (989, 170, 'addproject');
INSERT INTO action (id, features, action) VALUES (990, 170, 'addbusinessLine');
INSERT INTO action (id, features, action) VALUES (991, 170, 'addbusinessEntityContact');
INSERT INTO action (id, features, action) VALUES (992, 20, 'addconsecutiveCreditNotes');
INSERT INTO action (id, features, action) VALUES (993, 20, 'addconsecutiveCollectionAccount');
INSERT INTO action (id, features, action) VALUES (994, 20, 'consecutiveCollectionAccountsTab');
INSERT INTO action (id, features, action) VALUES (995, 20, 'consecutiveCreditNotesesTab');
INSERT INTO action (id, features, action) VALUES (996, 20, 'billingResolutionsTab');
INSERT INTO action (id, features, action) VALUES (997, 20, 'consecutiveAccountsBillingsTab');
INSERT INTO action (id, features, action) VALUES (998, 20, 'withRenableUnitType');
INSERT INTO action (id, features, action) VALUES (999, 99, 'AddconstructionLicenses');
INSERT INTO action (id, features, action) VALUES (1000, 99, 'AddsegmentStage');
INSERT INTO action (id, features, action) VALUES (1001, 99, 'editconstruction');
INSERT INTO action (id, features, action) VALUES (1002, 99, 'createfloor');
INSERT INTO action (id, features, action) VALUES (1003, 99, 'createarea');
INSERT INTO action (id, features, action) VALUES (1004, 99, 'createRentableUnit');
INSERT INTO action (id, features, action) VALUES (1005, 16, 'AddphoneNumber');
INSERT INTO action (id, features, action) VALUES (1006, 16, 'Addaddress');
INSERT INTO action (id, features, action) VALUES (1007, 16, 'AddbillingResolution');
INSERT INTO action (id, features, action) VALUES (1008, 16, 'AddconsecutiveAccountsBilling');
INSERT INTO action (id, features, action) VALUES (1009, 16, 'AddconsecutiveCreditNotes');
INSERT INTO action (id, features, action) VALUES (1010, 16, 'AddconsecutiveCollectionAccount');

--INSERT INTO action (id, features, action) VALUES (160, 20, 'addconsecutiveAccountsBilling');
--INSERT INTO action (id, features, action) VALUES (161, 20, 'addbillingResolution');



-------------------------------------------(2011-06-02)---Asignacion de las paginas permitidas para el nuevo rol---(AC)------------------------------------------------------------------------


SELECT pg_catalog.setval('role_features_id_seq', 630, true);

INSERT INTO role_features (id, role, features) VALUES (597, 6, 99);
INSERT INTO role_features (id, role, features) VALUES (598, 6, 103);
INSERT INTO role_features (id, role, features) VALUES (599, 6, 104);
INSERT INTO role_features (id, role, features) VALUES (600, 6, 75);
INSERT INTO role_features (id, role, features) VALUES (601, 6, 76);
INSERT INTO role_features (id, role, features) VALUES (602, 6, 77);
INSERT INTO role_features (id, role, features) VALUES (603, 6, 135);
INSERT INTO role_features (id, role, features) VALUES (604, 6, 156);
INSERT INTO role_features (id, role, features) VALUES (605, 6, 157);
INSERT INTO role_features (id, role, features) VALUES (606, 6, 158);
INSERT INTO role_features (id, role, features) VALUES (607, 6, 159);
INSERT INTO role_features (id, role, features) VALUES (608, 6, 160);
INSERT INTO role_features (id, role, features) VALUES (609, 6, 161);
INSERT INTO role_features (id, role, features) VALUES (611, 6, 16);
INSERT INTO role_features (id, role, features) VALUES (613, 6, 21);
INSERT INTO role_features (id, role, features) VALUES (614, 6, 25);
INSERT INTO role_features (id, role, features) VALUES (615, 6, 26);
INSERT INTO role_features (id, role, features) VALUES (616, 6, 27);
INSERT INTO role_features (id, role, features) VALUES (610, 6, 95);
INSERT INTO role_features (id, role, features) VALUES (617, 6, 93);
INSERT INTO role_features (id, role, features) VALUES (618, 6, 94);
INSERT INTO role_features (id, role, features) VALUES (619, 6, 44);
INSERT INTO role_features (id, role, features) VALUES (620, 6, 45);
INSERT INTO role_features (id, role, features) VALUES (621, 6, 46);
INSERT INTO role_features (id, role, features) VALUES (622, 6, 96);
INSERT INTO role_features (id, role, features) VALUES (624, 6, 98);
INSERT INTO role_features (id, role, features) VALUES (623, 6, 97);
INSERT INTO role_features (id, role, features) VALUES (625, 7, 85);
INSERT INTO role_features (id, role, features) VALUES (626, 7, 80);
INSERT INTO role_features (id, role, features) VALUES (627, 7, 83);
INSERT INTO role_features (id, role, features) VALUES (628, 7, 167);
INSERT INTO role_features (id, role, features) VALUES (629, 7, 168);
INSERT INTO role_features (id, role, features) VALUES (612, 6, 170);


------------------------------------------------(2011-06-03)-Permisos Nueva Pagina de Recaudo: Collection---(AC)-----------------------------------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 171, true);
INSERT INTO features (id, page) VALUES (171, 'Collection.xhtml');


SELECT pg_catalog.setval('role_features_id_seq', 631, true);
INSERT INTO role_features (id, role, features) VALUES (631, 1, 171);

-------------------------------------------------(2011-06-07)-Adicion de campos a Unidad Arrendable--(AC)--------------------------------------------------------------------------------

ALTER TABLE terranvm_db.public.rentable_unit ADD COLUMN concept VARCHAR;

ALTER TABLE terranvm_db.public.rentable_unit ADD COLUMN periodicity VARCHAR;

ALTER TABLE terranvm_db.public.rentable_unit ADD COLUMN value_concept INTEGER;

-----------------------------------------------------------------------------------------------------------------------------------------------------------------

---------------------(junio 3 relacion de cuenta de recaudo con la HT)--------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE project_property ADD COLUMN collection_accounts INTEGER;

ALTER TABLE project_property ADD CONSTRAINT consecutive_collection_account_project_property_fk
FOREIGN KEY (collection_accounts)
REFERENCES consecutive_collection_account (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

---------------------------(Junio 3 columas para guardar la informacion del consecutivo de recaudo-------------------------------------------------------------------------------------------------------------

ALTER TABLE recover ADD COLUMN prefix_collection_accounts VARCHAR;

ALTER TABLE recover ADD COLUMN suffix_collection_accounts VARCHAR;

ALTER TABLE recover ADD COLUMN min_collection_accounts INTEGER;

-----------------------------------------------------------------------(7-06-2011) SE ADIOCNA FECHA DE FIN DE RECAUDO-(JL)------------------------------------------------------------------------
ALTER TABLE recover_closure ADD COLUMN closure_date DATE NOT NULL;
--------------------------------------------------(2011-06-07)-Columna de Relacion de Arrendatario con Ventas--(AC)-------------------------------------------------------------------

ALTER TABLE terranvm_db.public.sales ADD COLUMN lessee INTEGER;


ALTER TABLE terranvm_db.public.sales ADD CONSTRAINT business_entity_sales_fk
FOREIGN KEY (lessee)
REFERENCES terranvm_db.public.business_entity (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

﻿
------------------------------------------------------------(2011-06-08)-Razon en nota credito---(AC)----------------------------------------------------------------------------------

ALTER TABLE credit_note ADD COLUMN reason VARCHAR NOT NULL default 'Razon 1';

ALTER TABLE credit_note ALTER COLUMN reason DROP DEFAULT;



-------------------------------------------------------------(2011-06-10)-Permisos Nuevas Pagias Aprobacion Nota Credito---(AC)----------------------------------------------------------------------


SELECT pg_catalog.setval('features_id_seq', 173, true);

INSERT INTO features (id, page) VALUES (172, 'MakerCheckerCreditNotesList.xhtml');
INSERT INTO features (id, page) VALUES (173, 'CreditNotesView.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 633, true);
INSERT INTO role_features (id, role, features) VALUES (632, 1, 172);
INSERT INTO role_features (id, role, features) VALUES (633, 1, 173); 

-----------------------------------------------------------------------(10-06-2011) Se adicona la tabla de Archivos-(JL)------------------------------------------------------------------------
CREATE SEQUENCE file_id_seq;

CREATE TABLE file (
                id INTEGER NOT NULL DEFAULT nextval('terranvm_db.public.file_id_seq'),
                date DATE NOT NULL,
                data BYTEA NOT NULL,
                type VARCHAR NOT NULL,
                CONSTRAINT file_pk PRIMARY KEY (id)
);


ALTER SEQUENCE file_id_seq OWNED BY file.id;


----------------------------------------------------------------(2011-06-13)---(AC)--Copias de la factura-----------------------------------------------------------------------------
ALTER TABLE terranvm_db.public.business_entity ADD COLUMN first_copy VARCHAR;

ALTER TABLE terranvm_db.public.business_entity ADD COLUMN original VARCHAR;

ALTER TABLE terranvm_db.public.business_entity ADD COLUMN second_copy VARCHAR;


-------------------------------------------------------(2011-06-13)----(AC)-Permisos Ipc Acumulado-------------------------------------------------------------------------


SELECT pg_catalog.setval('features_id_seq', 174, true);
INSERT INTO features (id, page) VALUES (174, 'IpcAccumulated.xhtml');


SELECT pg_catalog.setval('role_features_id_seq', 634, true);
INSERT INTO role_features (id, role, features) VALUES (634, 1, 174);


-------------------------------------------(2011-06-16)----Configuracion telefono facturador---(AC)-----------------------------------------------------------------------------------
ALTER TABLE project_property ADD COLUMN phone_biller INTEGER NOT NULL default '4' ;
ALTER TABLE project_property ALTER COLUMN phone_biller DROP DEFAULT;

ALTER TABLE project_property ADD CONSTRAINT phone_number_project_property_fk1
FOREIGN KEY (phone_biller)
REFERENCES phone_number (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

-------------------------------------------------------(2011-06-20)---(AC)-----Secuencia Siigo de facturacion----------------------------------------------------------------------

ALTER TABLE invoice_concept ADD COLUMN siigo_billing_sequence INTEGER

--------------------------------------------------(2011-06-20)--(AC)-Prefijo y sufijo del numero de la factura----------------------------------------------------------------------------------

ALTER TABLE invoice ADD COLUMN prefix VARCHAR;
ALTER TABLE invoice ADD COLUMN suffix VARCHAR;

--------------------------------------------------(2011-06-21)--(AC)-Tasa de urusa usada o no-------------------------------------------------------------------------------
ALTER TABLE usury_rate ADD COLUMN rate_used BOOLEAN;

------------------------------------------------(2011-06-21)--(AC)--Consecutivo hoja dew terminos-----------------------------------------------------------------------------
ALTER TABLE project_property ADD COLUMN consecutive_terms_sheet INTEGER;


------------------------------------------------------(2011-06-22)-(AC)-Permisos ipc---------------------------------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 176, true);
INSERT INTO features (id, page) VALUES (175, 'IpcMonthly.xhtml');
INSERT INTO features (id, page) VALUES (176, 'IpcYearly.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 636, true);
INSERT INTO role_features (id, role, features) VALUES (635, 1, 175);
INSERT INTO role_features (id, role, features) VALUES (636, 1, 176);

-------------------------------------------------------------------------------------------------------------------------------------