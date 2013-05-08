-------Script numero 12 add 08/07/2011------------------------------------------

----IPC ACUMULADO----------------------------------------------
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values(12, 'IPC ACUMULADO','IPCA[MONTH]',true);

------------------Cambiar tipo en una tabla uditada billing resolution y business entitty
ALTER TABLE history_billing_resolution_aud ALTER observation TYPE text;
ALTER TABLE history_business_entity_aud ALTER biller_observation TYPE text;

--------------------------------------------(2011-07-11)----System_Configuration (AC)--------------------------------------------------------------------------------

CREATE SEQUENCE system_configuration_id_seq;

CREATE TABLE system_configuration (
                id INTEGER NOT NULL DEFAULT nextval('system_configuration_id_seq'),
                name VARCHAR NOT NULL,
                value VARCHAR NOT NULL,
                CONSTRAINT system_configuration_pk PRIMARY KEY (id)
);


ALTER SEQUENCE system_configuration_id_seq OWNED BY system_configuration.id;



----------------------------------(AC)-Cambiar nombre del rol Activo 2--(2011)--------------------------------------------

UPDATE "role" SET role_name= 'Activo PEI'  WHERE id=8;

----------------------------------(AC)-Eliminar permisos Naturaleza Juridica del Activo Perfil 8 Activo PEI--(2011)--------------------------------------------
DELETE FROM role_features WHERE id = 640;
DELETE FROM role_features WHERE id = 641;
DELETE FROM role_features WHERE id = 642;

----------------------------------(AC)-Eliminar permisos Asignar propietario y arrendatario a unidad arrendable Perfil 8 Activo PEI--(2011)--------------------------------------------
DELETE FROM role_features WHERE id = 643;

----------------------------------(AC)-Actualizar-Eliminar permisos Proyecto Perfil 8 Activo PEI--(2011)--------------------------------------------
UPDATE role_features SET features=118 WHERE id = 650;

----------------------------------(AC)-Eliminar permisos Linea de Negocio Perfil 9 Creacion Hoja de Termino--(2011)--------------------------------------------
DELETE FROM role_features WHERE id = 655;
DELETE FROM role_features WHERE id = 656;
DELETE FROM role_features WHERE id = 657;

----------------------------------(AC)-Eliminar permisos Proyecto Perfil 9 Creacion Hoja de Termino--(2011)--------------------------------------------
DELETE FROM role_features WHERE id = 658;
DELETE FROM role_features WHERE id = 659;
DELETE FROM role_features WHERE id = 667;


---------------------------------------------(2011-07-12)-Consecutivo Diario Siigo-(AC)------------------------------------------------------------------------------------------------------

CREATE SEQUENCE consecutive_daily_siigo_id_seq;

CREATE TABLE consecutive_daily_siigo (
                id INTEGER NOT NULL DEFAULT nextval('consecutive_daily_siigo_id_seq'),
                project INTEGER NOT NULL,
                consecutive INTEGER NOT NULL,
                date DATE NOT NULL,
                type VARCHAR NOT NULL,
                CONSTRAINT consecutive_daily_siigo_pk PRIMARY KEY (id)
);


ALTER SEQUENCE consecutive_daily_siigo_id_seq OWNED BY consecutive_daily_siigo.id;



--------------------------------------------(AC)-(2011-07-14)--Perfiles Nuevos PEI---------------------------------------------------------------

SELECT pg_catalog.setval('role_id_seq', 11, true);
INSERT INTO role (id, role_name) VALUES (10, 'Consulta PEI');
INSERT INTO role (id, role_name) VALUES (11, 'Aprobaciones PEI');


-----------------------------------------Paginas Nuevas  Rol ConsultaPEI---------------------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 186, true);
INSERT INTO features (id, page) VALUES (183, 'BusinessEntityList.xhtml');
INSERT INTO features (id, page) VALUES (184, 'RealProperty.xhtml');
INSERT INTO features (id, page) VALUES (185, 'ProjectProperty.xhtml');
INSERT INTO features (id, page) VALUES (186, 'RentableUnitOwnerLesseeList.xhtml');


-----------------------------------------Permisos Paginas Nuevas Rol ConsultaPEI---------------------------------------------------------------

SELECT pg_catalog.setval('role_features_id_seq', 693, true);

INSERT INTO role_features (id, role, features) VALUES (676, 10, 183);
INSERT INTO role_features (id, role, features) VALUES (677, 10, 168);
INSERT INTO role_features (id, role, features) VALUES (678, 10, 126);
INSERT INTO role_features (id, role, features) VALUES (679, 10, 184);
INSERT INTO role_features (id, role, features) VALUES (680, 10, 127);
INSERT INTO role_features (id, role, features) VALUES (682, 10, 185);


INSERT INTO role_features (id, role, features) VALUES (683, 11, 183);
INSERT INTO role_features (id, role, features) VALUES (684, 11, 168);
INSERT INTO role_features (id, role, features) VALUES (685, 11, 126);
INSERT INTO role_features (id, role, features) VALUES (686, 11, 184);
INSERT INTO role_features (id, role, features) VALUES (687, 11, 127);
INSERT INTO role_features (id, role, features) VALUES (688, 11, 185);
INSERT INTO role_features (id, role, features) VALUES (689, 11, 83);
INSERT INTO role_features (id, role, features) VALUES (690, 11, 80);
INSERT INTO role_features (id, role, features) VALUES (691, 11, 85);

INSERT INTO role_features (id, role, features) VALUES (692, 10, 186);
INSERT INTO role_features (id, role, features) VALUES (693, 11, 186);



-----------------------------------------Permisos acciones Paginas Nuevas Rol ConsultaPEI---------------------------------------------------------------

SELECT pg_catalog.setval('action_id_seq', 1029, true);

INSERT INTO action (id, features, action) VALUES (1013, 183, 'search');
INSERT INTO action (id, features, action) VALUES (1014, 183, 'reset');
INSERT INTO action (id, features, action) VALUES (1016, 183, 'businessEntityViewId');
INSERT INTO action (id, features, action) VALUES (1017, 183, 'firstPage');
INSERT INTO action (id, features, action) VALUES (1018, 183, 'previousPage');
INSERT INTO action (id, features, action) VALUES (1019, 183, 'nextPage');
INSERT INTO action (id, features, action) VALUES (1020, 183, 'lastPage');
INSERT INTO action (id, features, action) VALUES (1021, 184, 'floorlist');
INSERT INTO action (id, features, action) VALUES (1022, 99, 'addconstruction');
INSERT INTO action (id, features, action) VALUES (1023, 99, 'edit');
INSERT INTO action (id, features, action) VALUES (1024, 99, 'done');
INSERT INTO action (id, features, action) VALUES (1025, 167, 'edit');
INSERT INTO action (id, features, action) VALUES (1026, 167, 'done');
INSERT INTO action (id, features, action) VALUES (1027, 168, 'approved');
INSERT INTO action (id, features, action) VALUES (1028, 168, 'cancel');

INSERT INTO action (id, features, action) VALUES (1029, 135, 'assignment');



----------------------------------------------------------------------------------------------------------------------------------