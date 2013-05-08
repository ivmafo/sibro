-------Script numero 11 add 30/06/2011------------------------------------------

-----------------------------------------(2011-07-01)-(AC)--Adicion Permisos Pagina Ipc--------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 179, true);
INSERT INTO features (id, page) VALUES (179, 'Ipcs.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 671, true);
INSERT INTO role_features (id, role, features) VALUES (671, 1, 179);


-------------------------------------------------(2011-07-05)-(AC)--Permisos paginas faltantes------------------------------------------------------------------------

SELECT pg_catalog.setval('features_id_seq', 182, true);
INSERT INTO features (id, page) VALUES (180, 'ConcepTemplateEdit.xhtml');
INSERT INTO features (id, page) VALUES (181, 'PaymentFormEdit.xhtml');
INSERT INTO features (id, page) VALUES (182, 'RetentionRateAccountEdit.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 675, true);
INSERT INTO role_features (id, role, features) VALUES (672, 1, 180);
INSERT INTO role_features (id, role, features) VALUES (674, 1, 181);
INSERT INTO role_features (id, role, features) VALUES (675, 1, 182);

----------------------------(2011-07-07)-(MN)--Alterar la fecha de compra y contruccion del activo para que sea obligatorio------------------------------------------------------------------------
ALTER TABLE  real_property ALTER COLUMN purchase_date SET NOT NULL;
ALTER TABLE real_property ALTER COLUMN purchase_date SET DEFAULT 2010-05-10;
ALTER TABLE real_property ALTER COLUMN purchase_date DROP DEFAULT;



----------------------------(2011-07-08)-(AC)--Borrar distrito especial de la lista de regiones-----------------------------------------------------

DELETE FROM region  WHERE "name"='Distrito Especial';

-----------------------------------------------------------------------------------------------------------------------------------------