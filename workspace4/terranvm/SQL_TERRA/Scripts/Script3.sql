---Abril 6 de 20﻿11 

-------------------------------------------------------------------------Concept_Templates--------------------------------------------------------------------------------------------
CREATE SEQUENCE concep_template_id_seq;

CREATE TABLE concep_template (
                id INTEGER NOT NULL DEFAULT nextval('public.concep_template_id_seq'),
                name VARCHAR,
                print_description VARCHAR,
                expression VARCHAR,
                is_contribution_module BOOLEAN,
                is_early_payment BOOLEAN,
                document_type INTEGER,
                CONSTRAINT concep_template_pk PRIMARY KEY (id)
);


ALTER SEQUENCE concep_template_id_seq OWNED BY concep_template.id;
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------DeleteAreaTypes------------------------------------------------------------------------------------------------
 update area
   SET type = 2
 WHERE type= 1;

 update area 
   SET type = 2
 WHERE type= 4;

 update area 
   SET type = 2
 WHERE type= 5;

 update area
   SET type = 2
 WHERE type= 6;

 update area 
   SET type = 2
 WHERE type= 7;

 update area 
   SET type = 2
 WHERE type= 9;

delete FROM area_type
 WHERE id=1;

 DELETE FROM area_type
 WHERE id=4;

 DELETE FROM area_type
 WHERE id=5;

 DELETE FROM area_type
 WHERE id=6;

 DELETE FROM area_type
 WHERE id=7;

 DELETE FROM area_type
 WHERE id=8;

 DELETE FROM area_type
 WHERE id=9;
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------prefijo_projecto------------------------------------------------------------------------------------------------

ALTER TABLE project ADD COLUMN project_prefix VARCHAR DEFAULT 'AA'NOT NULL;

--------------------------------------------------------------------------------priority--------------------------------------------------------------------------------------------------
ALTER TABLE concept ADD COLUMN priority INTEGER NOT NULL DEFAULT 1;
ALTER TABLE concept ALTER COLUMN priority DROP DEFAULT;

---------------------------------------------------------------------------------recover---------------------------------------------------------------------------------------------------

ALTER TABLE invoice_concept ADD COLUMN balance DOUBLE PRECISION;
UPDATE invoice_concept SET balance = (calculated_cost+calculated_tax);
ALTER TABLE invoice_concept ALTER COLUMN balance SET NOT NULL;

CREATE SEQUENCE recover_id_seq;

CREATE TABLE recover (
                id INTEGER NOT NULL DEFAULT nextval('recover_id_seq'),
                value DOUBLE PRECISION NOT NULL,
                date DATE NOT NULL,
                CONSTRAINT recover_pk PRIMARY KEY (id)
);

ALTER SEQUENCE recover_id_seq OWNED BY recover.id;

CREATE SEQUENCE recover_concept_id_seq;

CREATE TABLE recover_concept (
                id INTEGER NOT NULL DEFAULT nextval('recover_concept_id_seq'),
                invoice_concept INTEGER NOT NULL,
                recover INTEGER NOT NULL,
                value DOUBLE PRECISION NOT NULL,
                CONSTRAINT recover_concept_pk PRIMARY KEY (id)
);

ALTER SEQUENCE recover_concept_id_seq OWNED BY recover_concept.id;

ALTER TABLE recover_concept ADD CONSTRAINT invoice_concept_recover_concept_fk
FOREIGN KEY (invoice_concept)
REFERENCES invoice_concept (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE recover_concept ADD CONSTRAINT recover_recover_concept_fk
FOREIGN KEY (recover)
REFERENCES recover (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
-------------------------------------------------------------------------AddRealProperty------------------------------------------------------------------------------------------------

--------------------------Agregar_Columna_area_total_arrendable

ALTER TABLE rentable_unit ADD COLUMN total_rentable_area DOUBLE PRECISION NOT NULL  DEFAULT 0.0;

ALTER TABLE floor ADD COLUMN total_rentable_area DOUBLE PRECISION NOT NULL DEFAULT 0.0;

ALTER TABLE construction ADD COLUMN total_rentable_area DOUBLE PRECISION NOT NULL DEFAULT 0.0;

ALTER TABLE real_property ADD COLUMN total_rentable_area DOUBLE PRECISION NOT NULL DEFAULT 0.0;

ALTER TABLE area ADD COLUMN total_rentable_area DOUBLE PRECISION NOT NULL DEFAULT 0.0;

--Quitar el valor por defecto del area total arrendable

ALTER TABLE real_property  ALTER COLUMN total_rentable_area DROP DEFAULT;

ALTER TABLE rentable_unit  ALTER COLUMN total_rentable_area DROP DEFAULT;

ALTER TABLE floor  ALTER COLUMN total_rentable_area DROP DEFAULT;

ALTER TABLE area  ALTER COLUMN total_rentable_area DROP DEFAULT;

ALTER TABLE construction  ALTER COLUMN total_rentable_area DROP DEFAULT;

--------------------------------------------------------------------------BusinessEntity------------------------------------------------------------------------------------------------
create UNIQUE INDEX business_entity_idx
 ON business_entity
 ( id_number );

-----------------------------------------------------------------------------CreateNew--------------------------------------------------------------------------------------------------
ALTER TABLE real_property DROP COLUMN construction_license;


ALTER TABLE real_property DROP COLUMN fund;


CREATE TABLE construction_licenses (
                id INTEGER NOT NULL,
                number VARCHAR NOT NULL,
                type VARCHAR NOT NULL,
                date_license DATE NOT NULL,
                number_curating INTEGER NOT NULL,
                city_license INTEGER NOT NULL,
                description VARCHAR,
                realproperty INTEGER NOT NULL,
                CONSTRAINT construction_licenses_pk PRIMARY KEY (id)
);


CREATE TABLE segment_stage (
                id INTEGER NOT NULL,
                tramo VARCHAR NOT NULL,
                percentage DOUBLE PRECISION NOT NULL,
                date_purchase DATE NOT NULL,
                realproperty INTEGER NOT NULL,
                CONSTRAINT segment_stage_pk PRIMARY KEY (id)
);



ALTER TABLE construction_licenses ADD CONSTRAINT city_construction_licenses_fk
FOREIGN KEY (city_license)
REFERENCES public.city (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE construction_licenses ADD CONSTRAINT real_property_construction_licenses_fk
FOREIGN KEY (realproperty)
REFERENCES public.real_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


ALTER TABLE segment_stage ADD CONSTRAINT real_property_segment_stage_fk
FOREIGN KEY (realproperty)
REFERENCES real_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--------------------------------------------------------------------------RentableUnit------------------------------------------------------------------------------------------------
ALTER TABLE rentable_unit ADD COLUMN building_permit VARCHAR;

ALTER TABLE rentable_unit ADD COLUMN construcion_enrollment VARCHAR;

------------------------------------------------------------------------version_hojaTerminos----------------------------------------------------------------------------------------------

ALTER TABLE project_property ADD COLUMN version INTEGER;

-----------------------------------------------------------------------------Iteracion_11------------------------------------------------------------------------------------------------
ALTER TABLE city ADD column siigo_code varchar(50);

--TABLAS DE INCREMENTOS----------------
CREATE SEQUENCE system_variable_increment_seg START 1;

CREATE TABLE system_variable(
	id integer DEFAULT nextval('system_variable_increment_seg'::regclass) NOT NULL,
	name varchar(50),
	sintax varchar(50),
	is_systemVariable boolean,
	PRIMARY KEY (id)
);

CREATE SEQUENCE ipc_yearly_increment_seg START 1;

CREATE TABLE ipc_yearly(
	id integer DEFAULT nextval('ipc_yearly_increment_seg'::regclass) NOT NULL,
	ipc_yearly integer REFERENCES system_variable(id),
	year integer,
	value real,
	PRIMARY KEY (id)
);


ALTER TABLE ipc_yearly ADD CONSTRAINT  ipc_yearly_constrain FOREIGN KEY (ipc_yearly) REFERENCES system_variable(id);

CREATE SEQUENCE ipc_monthly_increment_seg START 1;


CREATE TABLE ipc_monthly(
	id integer DEFAULT nextval('ipc_monthly_increment_seg'::regclass) NOT NULL,
	ipc_monthly integer REFERENCES system_variable(id),
	year integer,
	monthly integer,
	value real,
	PRIMARY KEY (id)
);

ALTER TABLE ipc_monthly ADD CONSTRAINT  ipc_monthly_constrain FOREIGN KEY (ipc_monthly) REFERENCES system_variable(id);

CREATE SEQUENCE increased_increment_seg START 1;

CREATE TABLE increased(
	id integer DEFAULT nextval('increased_increment_seg'::regclass) NOT NULL,
	periodicity_type integer NOT NULL,
	periodicity integer NOT NULL,
	next_increased date,
	expression varchar(2000),
	PRIMARY KEY(id)
);

ALTER TABLE concept ADD COLUMN fixed_value double precision;

ALTER TABLE concept ADD COLUMN increased integer REFERENCES increased(id);

ALTER TABLE concept ADD CONSTRAINT  increased_constrain FOREIGN KEY (increased)  REFERENCES increased(id);


---INSERTS---------------
DELETE FROM system_variable;
DELETE FROM ipc_monthly;
DELETE FROM ipc_yearly;
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('IPC ANUAL'  ,'IPCY',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('IPC MENSUAL' ,'IPCM',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('VENTAS' ,'VTAS',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('MODULO DE CONTRIBUCION' ,'MC',true);
INSERT INTO system_variable (name,sintax,is_systemvariable) values ('VALOR FIJO' ,'VFIJO',true);

INSERT INTO ipc_monthly (ipc_monthly,year,monthly,value) values ((select max(id) from system_variable where name ='IPC MENSUAL'),2011,5,10.2);
INSERT INTO ipc_yearly(ipc_yearly,year,value) values ((select max(id) from system_variable where name ='IPC ANUAL'),2011,10.2);


---------------------------------------------------------------------RecaudoMenus-----------------------------------------------------------------------------------------------------------

INSERT INTO features(id, page)VALUES (130, 'RecoverManual.xhtml');
INSERT INTO role_features(id, "role", features) VALUES (553, 1, 130);

---------------------------------------------------------------------paymentAllowNull-------------------------------------------------------------------------------------------------------

ALTER TABLE ONLY concept ALTER COLUMN payment_form TYPE INTEGER, ALTER COLUMN payment_form DROP NOT NULL;

---------------------------------------------------------------------notice_date------------------------------------------------------------------------------------------------------------

ALTER TABLE project_property ADD COLUMN notice_date DATE NOT NULL default '2020-01-01';
ALTER TABLE project_property ALTER COLUMN notice_date DROP DEFAULT;


INSERT INTO features(id, page)VALUES (129, 'ConcepTemplateList.xhtml');

INSERT INTO role_features(id, "role", features) VALUES (552, 1, 129);





