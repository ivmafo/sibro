-------Script numero 13 add 15/07/2011------------------------------------------

-----------------------------------(AC)-(2011-07-19)--Modificaciones Matriz Impuestos--------------------------------------------------
UPDATE tax_configuration SET retention_rate=1, billed=11, biller=12 WHERE id=11;
UPDATE tax_configuration SET retention_rate=4, billed=11, biller=12 WHERE id=12;
UPDATE tax_configuration SET retention_rate=1, billed=11, biller=11 WHERE id=13;
UPDATE tax_configuration SET retention_rate=1, billed=11, biller=13 WHERE id=14;


INSERT INTO tax_configuration (id, name, retention_rate, taxliabilities, billed, biller) VALUES (35, NULL, 2, NULL, 15, 11);

-----------------------------------------------------------(Agosto 5 2011 Permiso para creación de Cuentas COntables)------------------------------------------------------------------------------
SELECT pg_catalog.setval('features_id_seq', 191, true);
INSERT INTO features (id, page) VALUES (190, 'RetentionRateAccountEdit.xhtml');
INSERT INTO features (id, page) VALUES (191, 'RetentionRateAccount.xhtml');

SELECT pg_catalog.setval('role_features_id_seq', 697, true);
INSERT INTO role_features (id, role, features) VALUES (696, 1, 190);
INSERT INTO role_features (id, role, features) VALUES (697, 1, 191);
