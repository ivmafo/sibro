-------Script numero 8 add 12/05/2011--------------------------------------------


CREATE SEQUENCE pay_form_type_id_seq;

CREATE TABLE pay_form_type (
                id INTEGER NOT NULL DEFAULT nextval('pay_form_type_id_seq'),
                name VARCHAR NOT NULL,
                CONSTRAINT pay_form_type_pk PRIMARY KEY (id)
);


ALTER SEQUENCE pay_form_type_id_seq OWNED BY pay_form_type.id;

ALTER TABLE recover ADD COLUMN pay_form_type INTEGER NOT NULL;

ALTER TABLE recover ADD CONSTRAINT pay_form_type_recover_fk
FOREIGN KEY (pay_form_type)
REFERENCES pay_form_type (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

SELECT setval('pay_form_type_id_seq', 3, true);
INSERT INTO pay_form_type (id, name) VALUES (1, 'Disponible');
INSERT INTO pay_form_type (id, name) VALUES (2, 'Cheque');

------- JL: 12/05/2011: Adición del logo--------------------------------------------

ALTER TABLE business_entity ADD COLUMN logo BYTEA;

------- JL: 13/05/2011: Adición de páginas home y login al perfil admin--------------------------------------------
INSERT INTO features(id,page)
    VALUES (140,'home.xhtml');

INSERT INTO features(id,page)
    VALUES (141,'login.xhtml');


INSERT INTO role_features(
            "role", features)
    VALUES (1, 140);

INSERT INTO role_features(
            "role", features)
    VALUES (1, 141);

---------------------------- May 13th 2011 12:44 pm (DAVID) -------------------------------------------------

ALTER TABLE invoice_concept  ALTER COLUMN applied_rate SET DEFAULT 0.0;
UPDATE invoice_concept set applied_rate = 0.0;

---------------------------- May 16th 2011 12:00 m (JL)  Adición de la tabla -------------------------------------------------

CREATE SEQUENCE recover_closure_id_seq;

CREATE TABLE recover_closure (
                id INTEGER NOT NULL DEFAULT nextval('recover_closure_id_seq'),
                date DATE NOT NULL,
                project INTEGER NOT NULL,
                CONSTRAINT recover_closure_pk PRIMARY KEY (id)
);


ALTER SEQUENCE recover_closure_id_seq OWNED BY recover_closure.id;


ALTER TABLE recover_closure ADD CONSTRAINT project_recover_closuere_fk
FOREIGN KEY (project)
REFERENCES project (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE recover_closure ADD COLUMN month INTEGER NOT NULL;

-------------------------------------------------------(AC)-------(16-05-2011) Modificacion Permisos en el menu para Credit Notes------------------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 142, true);
INSERT INTO features (id, page) VALUES (142, 'CreditNotes.xhtml');


SELECT pg_catalog.setval('role_features_id_seq', 572, true);
INSERT INTO role_features (id, role, features) VALUES (572, 1, 142);
--------------------------------------------------------------May 17th 2011 4:00 pm (JL)  Adición de usuarios en el menú------------------------------------------------------------
INSERT INTO features(id,page)
    VALUES (143,'UserTerranvmList.xhtml');
    
INSERT INTO features(id,page)
    VALUES (144,'UserTerranvmEdit.xhtml');
    
INSERT INTO features(id,page)
    VALUES (145,'UserTerranvm.xhtml');

INSERT INTO role_features(
            "role", features)
    VALUES (1, 143);

INSERT INTO role_features(
            "role", features)
    VALUES (1, 144);
    
INSERT INTO role_features(
            "role", features)
    VALUES (1, 145);

---------------------------------------------------------(18-05-2011) Permisos para Ipc--(AC)----------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 148, true);

INSERT INTO features (id, page) VALUES (146, 'IpcMonthlyEdit.xhtml');
INSERT INTO features (id, page) VALUES (147, 'IpcYearlyEdit.xhtml');
INSERT INTO features (id, page) VALUES (148, 'IpcAccumulatedEdit.xhtml');


SELECT pg_catalog.setval('role_features_id_seq', 578, true);
INSERT INTO role_features (id, role, features) VALUES (576, 1, 146);
INSERT INTO role_features (id, role, features) VALUES (577, 1, 147);
INSERT INTO role_features (id, role, features) VALUES (578, 1, 148);

SELECT pg_catalog.setval('features_id_seq', 152, true);

INSERT INTO features (id, page) VALUES (150, 'IpcMonthly.xhtml');
INSERT INTO features (id, page) VALUES (151, 'IpcYearly.xhtml');
INSERT INTO features (id, page) VALUES (152, 'IpcAccumulated.xhtml');


SELECT pg_catalog.setval('role_features_id_seq', 582, true);
INSERT INTO role_features (id, role, features) VALUES (580, 1, 150);
INSERT INTO role_features (id, role, features) VALUES (581, 1, 151);
INSERT INTO role_features (id, role, features) VALUES (582, 1, 152);



SELECT pg_catalog.setval('features_id_seq', 149, true);
INSERT INTO features (id, page) VALUES (149, 'UsuryRate.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 579, true);
INSERT INTO role_features (id, role, features) VALUES (579, 1, 149);
--------------------------------------------------------------------(19-05-2011) 10:50AM Se quita la obligatoriedad a la fecha-------------------------------------------------------------------------------

ALTER TABLE user_terranvm ALTER COLUMN effective_date DROP NOT NULL;

---------------------------------------------------------------------Update  tabla retention_rate_account --------------------------------------- 
UPDATE retention_rate_account
   SET value=11  WHERE name='Honorarios 11%';

UPDATE retention_rate_account
   SET value=4  WHERE name='Servicios 4%';

UPDATE retention_rate_account
   SET value=10  WHERE name='Iva 10%';

UPDATE retention_rate_account
   SET value=16  WHERE name='Iva 16%';

UPDATE retention_rate_account
   SET value=6.9  WHERE name='Servicios Construcción y Consultoria Profesional 6.9% por mil';

UPDATE retention_rate_account
   SET value=1.5  WHERE name='Tarifa Aplicable Año 2005 1.5%';

UPDATE retention_rate_account
   SET value=1.5  WHERE name='Tarifa Aplicable Año 2006 1.5%';   

UPDATE retention_rate_account
   SET value=1.5  WHERE name='Tarifa Aplicable Año 2007 1.5%';   

UPDATE retention_rate_account
   SET value=1  WHERE name='Tarifa Aplicable Año 2008 1%';   

UPDATE retention_rate_account
   SET value=0.5  WHERE name='Tarifa Aplicable Año 2009 0.5%';   

UPDATE retention_rate_account
   SET value=50  WHERE name='Rte Iva 50%';   

UPDATE retention_rate_account
   SET value=9.66  WHERE name='Demás Actividades de Comercio 9.66% por mil';  

UPDATE retention_rate_account
   SET value=8  WHERE name='Servicios 8% por mil';                                 

UPDATE retention_rate_account
   SET value=1.104  WHERE name='Otras Actividades de Comercio 1.104%';                                 

UPDATE retention_rate_account
   SET value=3.5  WHERE name='Compras 3.5%';                                 

UPDATE retention_rate_account
   SET value=1  WHERE name='Construcción 1%';                                 

UPDATE retention_rate_account
   SET value=4  WHERE name='Alquiler 4%';                                 

UPDATE retention_rate_account
   SET value=7  WHERE name='Retenciones Rendimiento Finacieros 7%';                                 
                                  
UPDATE retention_rate_account
   SET value=3.5  WHERE name='Arrendamientos 3.5%';                                 

UPDATE retention_rate_account
   SET value=11  WHERE name='Comisiones 11%';                                 

UPDATE retention_rate_account
   SET value=2  WHERE name='Vigilancia 2%';             
                       
--------------------------------------------------------scripts de JUan------------------------------------------
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('AREA UNI.ARRENDABLE','AREA',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('AREA TP.AREA','TPAREA',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('AREA PISO','PIAREA',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('AREA EDIFICIO','EDFAREA',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('AREA ACTIVO','RPAREA',true);
DELETE FROM system_variable where name ='VENTAS';
INSERT INTO system_variable (name,sintax,is_systemvariable) values('VENTAS','VTAS[MONTH]',true);

--MODIFICACION ENTIDAD CONCEPTO------------
alter table concept add column dependent BOOLEAN  NOT NULL default 'false';
ALTER TABLE concept ALTER COLUMN dependent DROP DEFAULT;

--------------------------------Permiso para crear variables del sistema  Mayo 19 de 2011----------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 154, true);

INSERT INTO features (id, page) VALUES (153, 'SystemVariableEdit.xhtml');
INSERT INTO features (id, page) VALUES (154, 'SystemVariable.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 584, true);
INSERT INTO role_features (id, role, features) VALUES (583, 1, 153);
INSERT INTO role_features (id, role, features) VALUES (584, 1, 154);

---------------------------------Drop SEQUENCE system_variable (Mayo 19 de 2011) --------
Drop SEQUENCE system_variable_increment_id_seq;




