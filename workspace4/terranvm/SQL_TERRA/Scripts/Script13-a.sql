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


