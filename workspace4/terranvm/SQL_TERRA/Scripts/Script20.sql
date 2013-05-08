
------------------------Script20 add 16/01/2012-----------------------------------

----------------16-01-2012---------------WAMAYA-----------------------------------
 ALTER TABLE concept ADD column discount_mounths int DEFAULT 0;
ALTER TABLE invoice_concept ADD column billed_period varchar;
ALTER TABLE invoice_concept ADD column discount_mounths int DEFAULT 0;
ALTER TABLE concept ADD column show__billed_period boolean DEFAULT true;

----------------25-01-2012---------------Jlopez-------------------------------------- Se envía por correo la ejecución de este script
CREATE SEQUENCE concept_retention_rate_account_increment_seg
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

SELECT setval('concept_retention_rate_account_increment_seg', currval('retention_rate_account_increment_seg'));

ALTER TABLE concept_retention_rate_account ALTER COLUMN id SET DEFAULT nextval('concept_retention_rate_account_increment_seg'::regclass);

----------------01-02-2012---------------Jlopez-------------------------------------- Creado en Release_25012012
delete from invoice_concept where calculated_cost = 0;
delete from invoice i where 0 = (select count(*) from invoice_concept ic where ic.invoice = i.id);