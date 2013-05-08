
-------Script numero 6 creado despues de l aentrega del 19 de abril de 2011---creado 25/04/2011----

-------------------------------------(02/05/2011)scrip para adicionar impuesto de timbre--------------------------------------------------
INSERT INTO retention_rate(id, name)
    VALUES (5, 'Timbre');

------------------------------------------------------------prorrateo----(26/04/2011) ya se corrio-----------------------------------------------------------------------------------------------

ALTER TABLE contribution_module ADD COLUMN apportionment BOOLEAN NOT NULL default 'false';
ALTER TABLE contribution_module ALTER COLUMN apportionment DROP DEFAULT;

------------------------------------------------------------------(27/04/2011) Adicionar pagina para permisos---------------------------------------------------------------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 135, true);
INSERT INTO features (id, page) VALUES (135, 'RentableUnitOwnerLesseeList.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 564, true);
INSERT INTO role_features (id, role, features) VALUES (564, 1, 135);

----------------------------------------------(28/04/2011)Scrip para modificar iva------------------------------------------------------------------------
ALTER TABLE retention_rate_account ALTER "value" TYPE double precision;

UPDATE retention_rate_account
   SET name='Iva 8%', value=0.08   WHERE id=6;

UPDATE retention_rate_account
   SET name='Iva 10%', value=0.10  WHERE id=10;

INSERT INTO retention_rate_account(id, name, retention_rate,value)
    VALUES (192,'Excento de Iva',3,0.0);
    
    
------------------------------DAVID MODIFICACION CRON JOB (Incremento) Mayo 02 de 2011 --------------------------------------------------
ALTER TABLE invoice_concept ADD COLUMN initial_fixed_value double precision NOT NULL DEFAULT 0.0;
ALTER TABLE invoice_concept ADD COLUMN invoice_concept_type smallint NOT NULL DEFAULT 1;
ALTER TABLE invoice_concept ADD COLUMN invoice_concept_parent integer;
ALTER TABLE invoice_concept ADD CONSTRAINT invoice_concept_parent_fk FOREIGN KEY (invoice_concept_parent) REFERENCES invoice_concept (id) ON UPDATE CASCADE ON DELETE CASCADE;




--------------------------------(28/04/2011)script para modificar impuesto al timbre------------------------------------------------------------------------------------    
UPDATE retention_rate_account
   SET name='-',retention_rate=5  WHERE id=188;
    
UPDATE retention_rate_account
   SET name='Año 2005: 1,5%', value=0.015  WHERE id=189;

UPDATE retention_rate_account
   SET name='Año 2006: 1,5%', value=0.015  WHERE id=190;

UPDATE retention_rate_account
   SET name='Año 2007: 1,5%', value=0.015  WHERE id=191;

INSERT INTO retention_rate_account(id, name, retention_rate,value)
    VALUES (193,'Año 2008: 1%', 5,0.01);

INSERT INTO retention_rate_account(id, name, retention_rate,value)
    VALUES (194,'Año 2009: 0,5%', 5,0.05); 
    
---------------------------plantilla formula de concepto (28/04/2011)-------------------------------------------------------------------------

ALTER TABLE ONLY concep_template ALTER COLUMN expression TYPE VARCHAR, ALTER COLUMN expression DROP NOT NULL;


--------------------------------(29/04/2011)script para agregarpagina de facturacion------------------------------------------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 136, true);

INSERT INTO features (id, page) VALUES (136, 'BillingList.xhtml');

--------------------------------(29/04/2011)script para agregar permisos de la pagina facturacion al perfil administrador--------------------------------------------------------------
SELECT pg_catalog.setval('role_features_id_seq', 566, true);

INSERT INTO role_features (id, role, features) VALUES (566, 1, 136);


--------------------------------(29/04/2011)script para agregar permisos de los botones de la pagina facturacion--------------------------------------------------------------

SELECT pg_catalog.setval('action_id_seq', 976, true);

INSERT INTO action (id, features, action) VALUES (971, 136, 'search');
INSERT INTO action (id, features, action) VALUES (972, 136, 'reset');
INSERT INTO action (id, features, action) VALUES (973, 136, 'firstPage');
INSERT INTO action (id, features, action) VALUES (974, 136, 'previousPage');
INSERT INTO action (id, features, action) VALUES (975, 136, 'nextPage');
INSERT INTO action (id, features, action) VALUES (976, 136, 'lastPage');

---------------------------------------(02/05/2011 (RTE FUENTE))------------------------------------------------------------
UPDATE retention_rate_account
   SET name='Honorarios 11%', account=13551501, value=0.11 
 WHERE id=2;

UPDATE retention_rate_account
   SET name='Servicios 4%', account=13551502, value=0.04 
 WHERE id=4;

 INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Compras 3.5%', 1, 13551503, 0.035);

 INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Construcci�n 1%', 1, 13551504, 0.01);

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Alquiler 4%', 1, 13551505, 0.04);

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Otros 0%', 1, 13551506, 0.00);

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Retenciones Exterior 0%', 1, 13551507, 0.00);  

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Retenciones Rendimiento Finacieros 7%', 1, 13551508, 0.07);   

--------------------------------(2/05/2011)script creacion tabla Ipc_Acumulado--------------------------------------------------------------

CREATE SEQUENCE public.ipc_accumulated_id_seq;

CREATE TABLE ipc_accumulated (
                id INTEGER NOT NULL DEFAULT nextval('ipc_accumulated_id_seq'),
                ipc_accumulated INTEGER,
                year INTEGER,
                monthly INTEGER,
                value REAL,
                CONSTRAINT ipc_accumulated_pk PRIMARY KEY (id)
);


ALTER SEQUENCE ipc_accumulated_id_seq OWNED BY ipc_accumulated.id;


ALTER TABLE ipc_accumulated ADD CONSTRAINT system_variable_ipc_accumulated_fk FOREIGN KEY (ipc_accumulated) REFERENCES system_variable (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--------------------------------------------------------------------------------------------------------------------------------------------
INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Arrendamientos 3.5%', 1, 13551509, 0.035);   

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Comisiones 11%', 1, 13551510, 0.11);   

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Vigilancia 2%', 1, 13551511, 0.02);      


----------------------------------------------------------------(02/05/2011 RTE IVA)----------------------------------------------------------    
INSERT INTO retention_rate_account(retention_rate, name, account, value)
    VALUES (2, 'Rte Iva 50%',13551701,0.5);
    
-----------------------------------------(02/05/2011 RTE ICA )------------------------------------------------------------------------
ALTER TABLE retention_rate_account ALTER "name" TYPE character varying(100);

UPDATE retention_rate_account
   SET name='Servicios Construcción y Consultoria Profesional 6.9% por mil', account=13551801, value=0.069
 WHERE id=9;

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Demás Actividades de Comercio 9.66% por mil', 4, 13551802, 0.0966); 

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Servicios 8% por mil', 4, 13551803, 0.08);

INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Otras Actividades de Comercio 1.104%', 4, 13551809, 0.0104);   
    

 ------------------------------------------------(02/05/2011 IMPUESTO AL TIMBRE)--------------------------------------------------------  
 UPDATE retention_rate_account
   SET name='Tarifa Aplicable Año 2005 1.5%', account=2305060107
 WHERE id=12;

 UPDATE retention_rate_account
   SET name='Tarifa Aplicable Año 2006 1.5%', account=2305060107
 WHERE id=13;

  UPDATE retention_rate_account
   SET name='Tarifa Aplicable Año 2007 1.5%', account=2305060107
 WHERE id=14;

 UPDATE retention_rate_account
   SET name='Tarifa Aplicable Año 2008 1%', account=2305060107
 WHERE id=15;

  UPDATE retention_rate_account
   SET name='Tarifa Aplicable Año 2009 0.5%', account=2305060107
 WHERE id=16;

 INSERT INTO retention_rate_account(name, retention_rate, account, value)
    VALUES ('Tarifa Aplicable Año 2010 y Posteriores 0.0%', 5, 2305060107, 0.0); 

    

