---------Script 1 ya ejecutado en servidor de pruebas (28/07/2011)-------
-------Script numero 11 add 30/06/2011------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 182, true);
INSERT INTO features (id, page) VALUES (180, 'ConcepTemplateEdit.xhtml');
INSERT INTO features (id, page) VALUES (181, 'PaymentFormEdit.xhtml');
INSERT INTO features (id, page) VALUES (182, 'RetentionRateAccountEdit.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 675, true);
INSERT INTO role_features (id, role, features) VALUES (672, 1, 180);
INSERT INTO role_features (id, role, features) VALUES (674, 1, 181);
INSERT INTO role_features (id, role, features) VALUES (675, 1, 182);


----------------------------(2011-07-07)-(MN)--Alterar la fecha de compra y contruccion del activo para que sea obligatorio------------------------------------------------------------------------
ALTER TABLE real_property ALTER COLUMN purchase_date SET DEFAULT '2010-05-10';
ALTER TABLE  real_property ALTER COLUMN purchase_date SET NOT NULL;
ALTER TABLE real_property ALTER COLUMN purchase_date DROP DEFAULT;



----------------------------(2011-07-08)-(AC)--Borrar distrito especial de la lista de regiones-----------------------------------------------------

DELETE FROM region  WHERE "name"='Distrito Especial';

----projec_user------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE project_user DROP CONSTRAINT fk38016131990fcf3c;
ALTER TABLE project_user DROP CONSTRAINT fk38016131ab9057dc;

------------real_property
DROP INDEX real_property_idx;
DROP INDEX real_property_idx1;

-------Script solucion de BUGS CON PRIORIDAD ALTA  ------------------------------------------

------Insert tabla system_variable  solucion del bug con ID= 1227  Jueves 28 Julio 2011-------------------------------------------
delete FROM system_variable;

INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (1, 'IPC ANUAL'  ,'IPCY[YEAR]',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (2, 'IPC MENSUAL' ,'IPCM[MONTH]',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (3, 'IPC ACUMULADO' ,'IPCA[MONTH]',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (4, 'MODULO DE CONTRIBUCION' ,'MC',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (5, 'VALOR FIJO' ,'VFIJO',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (6, 'AREA UNI.ARRENDABLE','AREA',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (7, 'AREA TP.AREA','TPAREA',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (8, 'AREA PISO','PIAREA',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (9, 'AREA EDIFICIO','EDFAREA',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values (10, 'AREA ACTIVO','RPAREA',true);
INSERT INTO system_variable (id, name,sintax,is_systemvariable) values(11, 'VENTAS','VTAS[MONTH]',true);


----------------------script para solución de bug 1228 y 1229 (Perfil creación de hojas de termino) Jueves 28 Julio 2011
SELECT pg_catalog.setval('features_id_seq', 188, true);
INSERT INTO features(id, page)VALUES (187, 'ProjectList.xhtml');

INSERT INTO "action"(features, "action")VALUES (187, 'projectViewId');

SELECT pg_catalog.setval('role_features_id_seq', 695, true);
INSERT INTO role_features (id, role, features) VALUES (694, 9, 187);

INSERT INTO "action"(features, "action")VALUES (97, 'creditNotesTab');

SELECT pg_catalog.setval('role_features_id_seq', 696, true);
INSERT INTO role_features (id, role, features) VALUES (695, 1, 97);

-------------------Actualizacion solucion de bug id= 1229 -------------------
﻿SELECT pg_catalog.setval('features_id_seq', 189, true);
INSERT INTO features(id, page)VALUES (188, 'ProjectPropertyEdit.xhtml');

UPDATE "action" SET features=97  WHERE "action"='creditNotesTab';
DELETE FROM role_features  WHERE id=695;
UPDATE role_features SET features=188 WHERE id=664;


------------------------------------------Script permisos creación de plantillas de conceptos- 3 Agosto de 2011 -----------------------
INSERT INTO features(id, page)VALUES (180, 'ConcepTemplateEdit.xhtml');
INSERT INTO role_features(id, "role", features) VALUES (672, 1, 180);

INSERT INTO features(id, page)VALUES (189, 'ConcepTemplate.xhtml');
INSERT INTO role_features(id, "role", features) VALUES (695, 1, 180);
SELECT setval('features_id_seq', 190, true);
SELECT setval('role_features_id_seq', 696, true);

----------------------------------------------------------Correccion permiso Linea del ver en plantillas de conceptos (4 Agosto 2011)---------
UPDATE role_features SET features=189 where id=695;


----------------------------------------------------------Se adiciona el permiso de adición de direcciones y áreas al perfil creador de entidades de negocio JL (19 Agosto 2011)---------
INSERT INTO role_features(id, "role", features) VALUES (696, 9, 1);
INSERT INTO role_features(id, "role", features) VALUES (697, 9, 2);

SELECT pg_catalog.setval('role_features_id_seq', 698, true);
