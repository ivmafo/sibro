-------Script numero 7 ---creado 02/05/2011--------------------------------------------
------tabla tipo de avaluo(02/052011)--------------------------
CREATE SEQUENCE avaluation_type_id_seq;

CREATE TABLE avaluation_type (
                id INTEGER NOT NULL DEFAULT nextval('avaluation_type_id_seq'),
                name VARCHAR NOT NULL,
                CONSTRAINT avaluation_type_id PRIMARY KEY (id)
);




------------------------------------LLenado de tabla typo de avaluos(02/052011)-------------------
INSERT INTO avaluation_type(id,name)
    VALUES (1,'Comercial');

INSERT INTO avaluation_type(id,name)
    VALUES (2,'Reconstruccion');    

  SELECT setval('public.avaluation_type_id_seq', 2, true);

-----------------------------------tabla avaluos (02/052011)---------------------------
ALTER TABLE avaluation DROP COLUMN activation;
ALTER TABLE avaluation DROP COLUMN date;
ALTER TABLE avaluation DROP COLUMN bulding_value;
ALTER TABLE avaluation DROP COLUMN terrain_value;
ALTER TABLE avaluation ADD COLUMN activation_date DATE;
ALTER TABLE avaluation ADD COLUMN avaluation_type INTEGER NOT NULL;
ALTER TABLE avaluation ADD COLUMN construction INTEGER;
ALTER TABLE avaluation ADD COLUMN effective_end_date DATE;
ALTER TABLE ONLY avaluation ALTER COLUMN real_property TYPE INTEGER, ALTER COLUMN real_property DROP NOT NULL;
ALTER TABLE avaluation ADD COLUMN value_1 DOUBLE PRECISION;
ALTER TABLE avaluation ADD COLUMN value_2 DOUBLE PRECISION;
ALTER TABLE avaluation ADD COLUMN value_3 DOUBLE PRECISION;	
ALTER TABLE avaluation  ADD COLUMN scope_avaluation character varying(1000);

ALTER TABLE avaluation ADD CONSTRAINT avaluation_type_avaluation_fk
FOREIGN KEY (avaluation_type)
REFERENCES avaluation_type (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE avaluation ADD CONSTRAINT construction_avaluation_fk
FOREIGN KEY (construction)
REFERENCES construction (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

------------------------------------------------(02/05/2011 Variables Globales )--------------------------------------------------------

ALTER TABLE system_variable ADD COLUMN project INTEGER;
ALTER TABLE system_variable ADD COLUMN value VARCHAR;

ALTER TABLE system_variable ADD CONSTRAINT project_system_variable_fk FOREIGN KEY (project) REFERENCES project (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


------------------------------------------------(03/05/2011 Fecha Preaviso en hoja de termino )--------------------------------------------------------

ALTER TABLE project_property ADD COLUMN notice_date DATE;
    
------------------------------------------------(05/05/2011 Nuevo tipo de Contrato)--------------------------------------------------------

SELECT pg_catalog.setval('contract_type_id_seq', 5, true);

INSERT INTO contract_type (id, name, description) VALUES (5, 'Concesión', 'contrato de concesión');

------------------------------------------------(05/05/2011 Nuevo tipo de Contrato)--------------------------------------------------------
ALTER TABLE invoice_concept ADD COLUMN concept_serializable BYTEA;
--------------------------------------------------------------------------(06/05/2011)Relacion de activo y proyecto--------------------------------------------------------------------------
CREATE SEQUENCE project_has_realproperty_seq;

CREATE TABLE project_has_realproperty (
                id INTEGER NOT NULL,
                project INTEGER,
                realproperty INTEGER,
                CONSTRAINT project_has_realproperty_pk PRIMARY KEY (id)
				);
				
ALTER TABLE project_has_realproperty ADD CONSTRAINT project_project_has_realproperty_fk
FOREIGN KEY (project)
REFERENCES project (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE project_has_realproperty ADD CONSTRAINT real_property_project_has_realproperty_fk
FOREIGN KEY (realproperty)
REFERENCES real_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;	

--ALTER TABLE project_has_realproperty DROP CONSTRAINT fk7874a75ee908c726;
--ALTER TABLE project_has_realproperty DROP CONSTRAINT fk7874a75e1f333652;

--CREATE SEQUENCE project_has_realproperty_seq
  -- INCREMENT 1
  -- START 1;
--ALTER TABLE project_has_realproperty_seq OWNER TO postgres;

ALTER TABLE project_has_realproperty
   ALTER COLUMN id SET DEFAULT nextval('project_has_realproperty_seq'::regclass);


----------------------------------------------------(06/5/2011) Campos Prorroga Automatica--------------------------------------------------
ALTER TABLE project_property ADD COLUMN number_periods_extension INTEGER;

ALTER TABLE project_property ADD COLUMN periodicity_extension INTEGER;

ALTER TABLE project_property ADD COLUMN periodicity_type_extension INTEGER;


----------------------------------------------------INVOICE CONCEPT May 9  2011,  8:46 am  (DAVID)--------------------------------------------------------------------------------


--ALTER TABLE invoice_concept DROP COLUMN initial_fixed_value;
ALTER TABLE concept ADD COLUMN seed boolean;
ALTER TABLE concept  ALTER COLUMN seed SET DEFAULT false;
ALTER TABLE invoice_concept ADD COLUMN seed boolean;
ALTER TABLE invoice_concept  ALTER COLUMN seed SET DEFAULT false;

update invoice_concept set seed = false;
update concept set seed = false;


ALTER TABLE invoice_concept ADD COLUMN invoice_concept_parent integer;
ALTER TABLE invoice_concept ADD COLUMN invoice_concept_type smallint NOT NULL DEFAULT 1;
ALTER TABLE invoice_concept ADD CONSTRAINT invoice_concept_parent_fk FOREIGN KEY (invoice_concept_parent) REFERENCES invoice_concept (id) ON UPDATE CASCADE ON DELETE CASCADE;


----------------------------------------------------------------(10/05/2011)Modificacion columna consecutivo Resolucion------------------------------------------------------------------------

--ALTER TABLE ONLY project_property DROP COLUMN  acountsbilling;

ALTER TABLE ONLY project_property ADD COLUMN acountsbilling INTEGER NULL default 1;
ALTER TABLE ONLY project_property ALTER COLUMN acountsbilling DROP DEFAULT;

ALTER TABLE project_property ADD CONSTRAINT consecutive_accounts_billing_project_property_fk FOREIGN KEY (acountsbilling) REFERENCES consecutive_accounts_billing (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


--------------------------------------------(10/05/2011)Boleano que dice si tiene o no interes de mora------------------------------------------------------
ALTER TABLE concept ADD COLUMN interest_arrears BOOLEAN NOT NULL default 'true';
ALTER TABLE concept ALTER COLUMN interest_arrears DROP DEFAULT;

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------


--------------------------------------------(10/05/2011) Creacion de Tabla taza de usura---------------------------------------------------

CREATE SEQUENCE usury_rate_id_seq;

CREATE TABLE usury_rate (
                id INTEGER NOT NULL DEFAULT nextval('usury_rate_id_seq'),
                value DOUBLE PRECISION NOT NULL,
                date DATE NOT NULL,
                CONSTRAINT usury_rate_pk PRIMARY KEY (id)
);


ALTER SEQUENCE usury_rate_id_seq OWNED BY usury_rate.id;

------------------------------------------------(11/05/2011) Permisos para administrador paginas taza de usura y variables del sistema-----------------------------------

SELECT pg_catalog.setval('features_id_seq', 138, true);
INSERT INTO features (id, page) VALUES (137, 'SystemVariableList.xhtml');
INSERT INTO features (id, page) VALUES (138, 'UsuryRateEdit.xhtml');


SELECT pg_catalog.setval('role_features_id_seq', 568, true);
INSERT INTO role_features (id, role, features) VALUES (567, 1, 137);
INSERT INTO role_features (id, role, features) VALUES (568, 1, 138);


-------------------------------------------------------(11/05/2011)-Campo en Invoice Concept de tasa aplicada---------------------------------- 
ALTER TABLE invoice_concept ADD COLUMN applied_rate DOUBLE PRECISION;


--------------------------------------PERMISO PARA VENTA--------------------------------------------------------------------------------------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 139, true);
INSERT INTO features (id, page) VALUES (139, 'SalesListTab.xhtml');



SELECT pg_catalog.setval('role_features_id_seq', 569, true);
INSERT INTO role_features (id, role, features) VALUES (569, 1, 139);
-------------------