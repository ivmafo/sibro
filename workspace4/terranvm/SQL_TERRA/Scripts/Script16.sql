-------Script numero 16 add 16/09/2011------------------------------------------

-------------------- DAVIDO (Sep 16th 2011 14:05)-----------------------------
ALTER TABLE project ADD COLUMN mandatory_interest boolean default true NOT NULL;

-------------------- Wamaya (Sep 16th 2011 14:05)--------------------------------
SELECT setval('public.system_variable_increment_seg', 12, true);

-------------------- JLopez (Sep 19th 2011 11:58)--------------------------------
ALTER TABLE file ADD COLUMN project_property INTEGER;
ALTER TABLE ONLY file ALTER COLUMN type TYPE VARCHAR, ALTER COLUMN type DROP NOT NULL;

ALTER TABLE file ADD CONSTRAINT project_property_file_fk
FOREIGN KEY (project_property)
REFERENCES project_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

DELETE FROM file;
ALTER TABLE file ADD COLUMN name VARCHAR NOT NULL;

-------------------- DAVIDO (Sep 19th 2011 15:27)-----------------------------
ALTER TABLE retention_rate ADD COLUMN subcategory_from integer;
ALTER TABLE retention_rate ADD CONSTRAINT "retention_rate_father_FK" FOREIGN KEY (subcategory_from) REFERENCES retention_rate (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE retention_rate ALTER "name" TYPE character varying(55);
INSERT INTO retention_rate(id, "name", retantion, subcategory_from) VALUES (11, 'Cuentas de Orden Deudora', false, null);
INSERT INTO retention_rate (id, "name", retantion, subcategory_from) VALUES
(1101, 'Cuentas por Cobrar', false, 11),
(1102, 'Cuentas de Bancos', false, 11),
(1103, 'Cuentas de Ingresos', false, 11),
(1104, 'Cuentas de IVA', true, 11),
(1105, 'Rete Fuente', true, 11),
(1106, 'Rete IVA', true, 11),
(1107, 'Rete ICA', true, 11),
(1108, 'Otros Ingresos', false, 11),
(1109, 'Deudoras de Control - Intereses Sobre Deudas Vencidas', false, 11),
(1110, 'Deudoras de Control - Por el Contrario', false, 11);

------------------- wamaya (Sep 20th 2011 08:17)-----------------------------
insert into role(id,role_name) values (13,'Aprobador Arq');
insert into role_features (role,features) values(13,103);
insert into role_features (role,features) values(13,104);
insert into role_features (role,features) values(13,118);
insert into role_features (role,features) values(13,156);
insert into role_features (role,features) values(13,157);
insert into role_features (role,features) values(13,158);
insert into role_features (role,features) values(13,159);
insert into role_features (role,features) values(13,160);
insert into role_features (role,features) values(13,161);
insert into role_features (role,features) values(13,21);
insert into role_features (role,features) values(13,44);
insert into role_features (role,features) values(13,45);
insert into role_features (role,features) values(13,46);
insert into role_features (role,features) values(13,96);
insert into role_features (role,features) values(13,98);
insert into role_features (role,features) values(13,135);
insert into role_features (role,features) values(13,170);
insert into role_features (role,features) values(13,187);
insert into role_features (role,features) values(13,188);
insert into role_features (role,features) values(13,83);
insert into role_features (role,features) values(13,85);
insert into role_features (role,features) values(13,80);
insert into features(id,page) values (192,'BusinessEntity.xhtml');
insert into action (features,action) values(192,'done');
insert into action (features,action) values(192,'cancel');
insert into action (features,action) values(192,'approved');
insert into features(id,page) values (193,'RealProperty.xhtml');
insert into role_features (role,features) values(13,192);
insert into role_features (role,features) values(13,193);


-------------------- DAVIDO (Sep 21st 2011 11:00)-----------------------------
ALTER TABLE invoice_concept ADD COLUMN penalty_date date default null;
insert into features (id, page) values ((select max(id) from features)+1,'PortafolioPenalty.xhtml');
insert into role_features (id, role, features) values ((select max(id) from role_features)+1, 1, (select id from features where page = 'PortafolioPenalty.xhtml'));

-------------------- jlopez (Sep 26th 2011 18:30) Bug alto 1545-----------------------------
UPDATE retention_rate SET "name"='Ingresos - Anticipos - Reembolsables' WHERE id = 8;



------------------- wamaya (Sep 27th 2011 08:17)-----------------------------
delete from tax_configuration;
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,11,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(2,11,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,11,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,11,13);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,13,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,13,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,13,13);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(4,13,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(4,13,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(2,13,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,15,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,15,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,15,13);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(4,15,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(4,15,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,20,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,20,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,20,13);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,20,15);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,20,20);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,23,12);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(1,23,11);
INSERT INTO tax_configuration(retention_rate,billed,biller) values(4,23,12);

INSERT INTO tax_configuration(retention_rate,billed,biller) (select 1105,billed,biller from tax_configuration where retention_rate=1);
INSERT INTO tax_configuration(retention_rate,billed,biller) (select 1106,billed,biller from tax_configuration where retention_rate=2);
INSERT INTO tax_configuration(retention_rate,billed,biller) (select 1107,billed,biller from tax_configuration where retention_rate=4);
------------bug 1315
DELETE FROM system_variable where id = 12;


----------- Bug alto JL ENVIADO POR CORREO
UPDATE increased SET periodicity_type=1 WHERE periodicity_type=0;

------------------- wamaya (Sep 27th 2011 08:17)Bug 1616-----------------------------
update retention_rate set name='Cuentas de Interes de Mora' where name='Cuentas de Orden Deudoras';
UPDATE retention_rate set name='Cuentas de Ingresos - Anticipos - Reembolsables' where name ='Ingresos';
UPDATE retention_rate set name='Cuentas de Ingresos - Anticipos - Reembolsables' where name ='Cuentas de Ingresos';

