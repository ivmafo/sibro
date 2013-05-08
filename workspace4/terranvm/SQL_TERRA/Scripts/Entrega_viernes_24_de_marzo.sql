--Borrar description en concept
ALTER TABLE concept DROP COLUMN description;

--Eliminar end_date en billing_resolution
ALTER TABLE billing_resolution ADD COLUMN end_date DATE NOT NULL default '2020-01-01';
ALTER TABLE billing_resolution ALTER COLUMN end_date DROP DEFAULT;

--Adicion de la tabla de sales y period_sales
CREATE SEQUENCE sales_id_seq START 1;
CREATE TABLE sales (
                id INTEGER NOT NULL DEFAULT nextval('sales_id_seq'),
                value DOUBLE PRECISION NOT NULL,
                rentable_unit INTEGER NOT NULL,
                sales_period INTEGER NOT NULL,
                CONSTRAINT sales_pk PRIMARY KEY (id)
);
ALTER SEQUENCE sales_id_seq OWNED BY sales.id;

CREATE SEQUENCE sales_period_id_seq START 1;
CREATE TABLE sales_period (
                id INTEGER NOT NULL DEFAULT nextval('sales_period_id_seq'),
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                real_property INTEGER NOT NULL,
                CONSTRAINT sales_period_pk PRIMARY KEY (id)
);
ALTER SEQUENCE sales_period_id_seq OWNED BY sales_period.id;

ALTER TABLE sales_period ADD CONSTRAINT real_property_sales_period_fk
FOREIGN KEY (real_property)
REFERENCES real_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE sales ADD CONSTRAINT rentable_unit_sales_fk
FOREIGN KEY (rentable_unit)
REFERENCES rentable_unit (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE sales ADD CONSTRAINT sales_period_sales_fk
FOREIGN KEY (sales_period)
REFERENCES sales_period (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

-- Eliminación de la tabla clause
ALTER TABLE clause DROP CONSTRAINT project_property_clause_fk;
DROP TABLE clause;
ALTER TABLE ONLY project_property ALTER COLUMN subject_contrat TYPE TEXT, ALTER COLUMN subject_contrat SET NOT NULL;

---------------------------------------------------------------------Matriz de Impuestos ------------------------------------------------------------------
--Se eliman tablas
drop table concept_retention_rate_account;
DROP SEQUENCE concept_retention_rate_account_increment_seg;
DROP TABLE tax_configuration;
DROP SEQUENCE tax_configuration_increment_seg;
DROP table retention_rate_account;
DROP SEQUENCE retention_rate_account_increment_seg;
DROP TABLE retention_rate ;
DROP SEQUENCE retention_rate_increment_seg;
DROP TABLE taxliabilities;
DROP SEQUENCE taxliabilities_increment_seg;

CREATE SEQUENCE taxliabilities_increment_seg START 1;

create table taxliabilities(
   id INTEGER DEFAULT   nextval('taxliabilities_increment_seg'::regclass) NOT NULL,
   name varchar(50),
   PRIMARY KEY(id)
);

CREATE SEQUENCE retention_rate_increment_seg START 1;

create table retention_rate (
   id integer  DEFAULT nextval('retention_rate_increment_seg'::regclass) NOT NULL,
   name varchar(50),
   PRIMARY KEY(id)

);


CREATE SEQUENCE retention_rate_account_increment_seg START 1;

create table retention_rate_account(
   id integer  DEFAULT nextval('retention_rate_account_increment_seg'::regclass) NOT NULL,
   retention_rate integer REFERENCES retention_rate(id),
   name varchar(50),
   account varchar(100),
   value integer,	
   PRIMARY KEY(id)

);

ALTER TABLE  retention_rate_account ADD CONSTRAINT  retention_rate_constrain FOREIGN KEY (retention_rate) REFERENCES retention_rate (id);

CREATE SEQUENCE tax_configuration_increment_seg START 1;

create table tax_configuration (
   id integer  DEFAULT nextval('tax_configuration_increment_seg'::regclass) NOT NULL,
   retention_rate integer REFERENCES retention_rate(id),
   billed integer REFERENCES taxliabilities(id),
   biller integer REFERENCES taxliabilities(id),
   PRIMARY KEY(id)
);


ALTER TABLE tax_configuration ADD CONSTRAINT  retention_rate_tx_constrain FOREIGN KEY (retention_rate) REFERENCES retention_rate (id);

ALTER TABLE tax_configuration ADD CONSTRAINT  billed_constrain FOREIGN KEY (billed) REFERENCES taxliabilities (id);

ALTER TABLE tax_configuration ADD CONSTRAINT  biller_constrain FOREIGN KEY (biller) REFERENCES taxliabilities (id);

CREATE SEQUENCE concept_retention_rate_account_increment_seg START 1;

create table concept_retention_rate_account (
   id integer  DEFAULT nextval('concept_retention_rate_account_increment_seg'::regclass) NOT NULL,
   retention_rate_account integer REFERENCES retention_rate_account(id),
   concept integer REFERENCES concept(id),
   PRIMARY KEY(id)
);

ALTER TABLE concept_retention_rate_account ADD CONSTRAINT  concept_retention_rate_account_constrain FOREIGN KEY (retention_rate_account) REFERENCES retention_rate_account (id);
ALTER TABLE concept_retention_rate_account ADD CONSTRAINT  concept_constrain FOREIGN KEY (concept) REFERENCES concept (id);

--DRAFT
INSERT INTO retention_rate (name) values ('ReteFuente');

INSERT INTO retention_rate_account (retention_rate,name)  values ((select max(id) from retention_rate where name ='ReteFuente'),'-');

INSERT INTO retention_rate_account (retention_rate,name,account)  values ((select max(id) from retention_rate where name ='ReteFuente'),'Retenecion del 10 %','1902029020209');

INSERT INTO retention_rate (name) values ('ReteIva');

INSERT INTO retention_rate_account (retention_rate,name)  values ((select max(id) from retention_rate where name ='ReteIva'),'-');

INSERT INTO retention_rate_account (retention_rate,name,account)  values ((select max(id) from retention_rate where name ='ReteFuente'),'Retenecion del 10 %','209029209202');

INSERT INTO retention_rate (name) values ('Iva');

INSERT INTO retention_rate_account (retention_rate,name)  values ((select max(id) from retention_rate where name ='Iva'),'-');

INSERT INTO retention_rate_account (retention_rate,name,account,value)  values ((select max(id) from retention_rate where name ='Iva'),'Iva del 16%','209029209202',16);

INSERT INTO retention_rate_account (retention_rate,name,account)  values ((select max(id) from retention_rate where name ='Iva'),'Iva del 16%','20902920920455');


INSERT INTO retention_rate (name) values ('ReteIca');

INSERT INTO retention_rate_account (retention_rate,name)  values ((select max(id) from retention_rate where name ='ReteIca'),'-');

INSERT INTO retention_rate_account (retention_rate,name,account,value)  values ((select max(id) from retention_rate where name ='ReteIca'),'ReteICa del 10%','209029209202',16);



INSERT INTO taxliabilities (id,name) values (11,'Venta Regimen Comun');
INSERT INTO taxliabilities (id,name) values (12,'Venta Regimen Simplificado');
INSERT INTO taxliabilities (id,name) values (13,'Gran Contribuyente');
INSERT INTO taxliabilities (id,name) values (15,'Autoretenedor');
INSERT INTO taxliabilities (id, name)  VALUES (20, 'Empresas del estado');
INSERT INTO taxliabilities(id, name) VALUES (21, 'Régimen simplificado no residente en el pais');
INSERT INTO taxliabilities(id, name)  VALUES (22, 'No residente en el país');
INSERT INTO taxliabilities(id, name) VALUES (23, 'Sin Régimen');

--DRAFT---
INSERT INTO tax_configuration (retention_rate,billed,biller) values (1,11,13);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (3,11,13);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (2,11,12);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (1,11,15);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (2,11,15);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (3,11,15);

INSERT INTO tax_configuration (retention_rate,billed,biller) values (4,11,15);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (4,11,13);
INSERT INTO tax_configuration (retention_rate,billed,biller) values (4,11,12);

ALTER TABLE project_property ALTER COLUMN real_property  DROP not null;

--AGREGACION DE CIUDAD A ENTIDAD DE NEGOCIO,

ALTER TABLE business_entity add column city integer REFERENCES city(id);
ALTER TABLE business_entity ADD  CONSTRAINT city_constrain  FOREIGN KEY (city) REFERENCES city(id);
----------------------------------------------------------------------------------------------------------------------------------------------------------


-- Modificación del historial de project_property para que acepte grandes volumenes de texto en la descripción
ALTER TABLE history_project_property_aud DROP COLUMN subject_contrat;
ALTER TABLE history_project_property_aud ADD COLUMN subject_contrat text;
ALTER TABLE history_project_property_aud ALTER COLUMN subject_contrat SET STORAGE EXTENDED;
