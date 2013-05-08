-----------------------------------------Script 21-----------------------------------------------------

-------------------------------------wamaya 28-03-2012--------------------------------------------------
------------------------------------se adicionan tablas para plantillas del contador--------------------

CREATE TABLE public.counter_configuration_tamplate (
                id INTEGER NOT NULL,
                name VARCHAR(100) NOT NULL,
                project_id_1 INTEGER NOT NULL,
                CONSTRAINT id PRIMARY KEY (id)
);


CREATE INDEX counter_configuration_tamplate_idx
 ON public.counter_configuration_tamplate
 ( project_id_1 );

CREATE TABLE public.counter_template_has_retention_rate_account (
                id INTEGER NOT NULL,
                counter_configuration_template_id INTEGER NOT NULL,
                retention_rate_account_id INTEGER NOT NULL,
                CONSTRAINT counter_template_has_retention_rate_account_pk PRIMARY KEY (id)
);


CREATE INDEX counter_template_has_retention_rate_account_idx
 ON public.counter_template_has_retention_rate_account
 ( counter_configuration_template_id );

CREATE INDEX counter_template_has_retention_rate_account_idx1
 ON public.counter_template_has_retention_rate_account
 ( retention_rate_account_id );


ALTER TABLE public.counter_template_has_retention_rate_account ADD CONSTRAINT retention_rate_account_counter_template_has_retention_rate_a390
FOREIGN KEY (retention_rate_account_id)
REFERENCES public.retention_rate_account (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.counter_configuration_tamplate ADD CONSTRAINT project_counter_configuration_tamplate_fk
FOREIGN KEY (project_id_1)
REFERENCES public.project (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.counter_template_has_retention_rate_account ADD CONSTRAINT counter_configuration_tamplate_counter_template_has_retentio259
FOREIGN KEY (counter_configuration_template_id)
REFERENCES public.counter_configuration_tamplate (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;



CREATE SEQUENCE counter_configuration_tamplate_id_sec
   INCREMENT 1
   START 1;



CREATE SEQUENCE counter_template_has_retention_rate_account_id_sec
   INCREMENT 1
   START 1;

ALTER TABLE counter_configuration_tamplate
   ALTER COLUMN id SET DEFAULT nextval('counter_configuration_tamplate_id_sec'::regclass);

ALTER TABLE counter_template_has_retention_rate_account
   ALTER COLUMN id SET DEFAULT nextval('counter_template_has_retention_rate_account_id_sec'::regclass);
   
   
   -------------------------------------------wamaya 29-03-2012--------------------------------------------------
   
    INSERT INTO features (id,page) VALUES ((select max(id) from features)+1,'CounterConfigurationTamplateList.xhtml');
    INSERT INTO role_features (id,role,features) VALUES ( (select max(id) from role_features) + 1, 1,  (select max(id) from features));
    INSERT INTO features (id,page) VALUES ((select max(id) from features)+1,'CounterConfigurationTamplateEdit.xhtml');
    INSERT INTO role_features (id,role,features) VALUES ( (select max(id) from role_features) + 1, 1,  (select max(id) from features));
    ALTER TABLE counter_configuration_tamplate ADD COLUMN biller INTEGER;
    INSERT INTO features (id,page) VALUES ((select max(id) from features)+1,'CounterConfigurationTamplate.xhtml');
    INSERT INTO role_features (id,role,features) VALUES ( (select max(id) from role_features) + 1, 1,  (select max(id) from features));
    
    ------------------------------------------wamaya 27-04-2012---------------------------------------------------
    
    ALTER TABLE real_property ADD COLUMN step integer not null default 0;
    UPDATE real_property SET step = 2;
     
     
---------------------------wamaya 29-03-2012----------- El siguiente Script esta comentariado, ya que fue el Script que se adjunto en la entrega del día 30-04-2012 
    --ALTER TABLE invoice_concept ADD COLUMN siigo_billing_sequence_to_credit_note int;

---------------------------------------------wamaya 10-05-2012---------------------------------------------------
CREATE SEQUENCE public.history_usury_rate_id_seq;
CREATE TABLE public.history_usury_rate (
id INTEGER NOT NULL DEFAULT nextval('public.history_usury_rate_id_seq'),
value DOUBLE PRECISION NOT NULL,
date DATE NOT NULL,
rate_used BOOLEAN,
CONSTRAINT history_usury_rate_pk PRIMARY KEY (id)
);



