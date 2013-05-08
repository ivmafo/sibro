-------Script numero 10-1 add 29/06/2011------------------------------------------

----------------------------------------------(2011-06-29)--(AC)--Perfiles Nuevos--------------------------------------------------
---------------------------------------------------role-------------------------------------------
SELECT pg_catalog.setval('role_id_seq', 9, true);

INSERT INTO role (id, role_name) VALUES (8, 'Activo2');
INSERT INTO role (id, role_name) VALUES (9, 'Creacion Hojas de Terminos');

--------------------------------------------------------------------------------(Usuarios De Prueba)------------------------------------------------------------------
SELECT pg_catalog.setval('user_terranvm_id_seq', 35, true);

INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (34, 'activo2', 'activo2', '3000-01-01', 1, 'qwe@ert.com', 'activo2', 8);
INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (35, 'htermino', 'htermino', '3000-01-01', 1, 'qwe@ert.com', 'Creacion Hoja termino', 9);


----------------------------------------------------role_features-------------------------------------------

SELECT pg_catalog.setval('role_features_id_seq', 670, true);

INSERT INTO role_features (id, role, features) VALUES (668, 8, 99);
INSERT INTO role_features (id, role, features) VALUES (669, 8, 103);
INSERT INTO role_features (id, role, features) VALUES (670, 8, 104);
INSERT INTO role_features (id, role, features) VALUES (640, 8, 75);
INSERT INTO role_features (id, role, features) VALUES (641, 8, 76);
INSERT INTO role_features (id, role, features) VALUES (642, 8, 77);
INSERT INTO role_features (id, role, features) VALUES (643, 8, 135);
INSERT INTO role_features (id, role, features) VALUES (644, 8, 156);
INSERT INTO role_features (id, role, features) VALUES (645, 8, 157);
INSERT INTO role_features (id, role, features) VALUES (646, 8, 158);
INSERT INTO role_features (id, role, features) VALUES (647, 8, 159);
INSERT INTO role_features (id, role, features) VALUES (648, 8, 160);
INSERT INTO role_features (id, role, features) VALUES (649, 8, 161);
INSERT INTO role_features (id, role, features) VALUES (651, 9, 135);
INSERT INTO role_features (id, role, features) VALUES (652, 9, 16);
INSERT INTO role_features (id, role, features) VALUES (653, 9, 170);
INSERT INTO role_features (id, role, features) VALUES (654, 9, 21);
INSERT INTO role_features (id, role, features) VALUES (655, 9, 25);
INSERT INTO role_features (id, role, features) VALUES (656, 9, 26);
INSERT INTO role_features (id, role, features) VALUES (657, 9, 27);
INSERT INTO role_features (id, role, features) VALUES (658, 9, 93);
INSERT INTO role_features (id, role, features) VALUES (659, 9, 94);
INSERT INTO role_features (id, role, features) VALUES (660, 9, 44);
INSERT INTO role_features (id, role, features) VALUES (661, 9, 45);
INSERT INTO role_features (id, role, features) VALUES (662, 9, 46);
INSERT INTO role_features (id, role, features) VALUES (663, 9, 96);
INSERT INTO role_features (id, role, features) VALUES (664, 9, 97);
INSERT INTO role_features (id, role, features) VALUES (665, 9, 98);
INSERT INTO role_features (id, role, features) VALUES (650, 8, 95);
INSERT INTO role_features (id, role, features) VALUES (667, 9, 95);


------------------------------Action que se utiliza en el sistema para no mostar un atributo de la lista tipo de tercer en entidad de negocio--------------------------------------------
SELECT pg_catalog.setval('action_id_seq', 1012, true);
INSERT INTO action (id, features, action) VALUES (1011, 170, 'DisableEntityTypeField');
INSERT INTO action (id, features, action) VALUES (1012, 20, 'isbillerField');


---------------------------Cambiar tipo de dato en resoluciones de facturación--------------------------------------------------
ALTER TABLE billing_resolution ALTER observation TYPE text;

-----------------------------cambiar tipo de dato en businessEntity
ALTER TABLE business_entity ALTER biller_observation TYPE text;