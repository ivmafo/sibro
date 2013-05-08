-------Script numero 15 add 02/09/2011------------------------------------------

-------------------- DAVIDO (Sep 2nd 2011 10:26 am)-----------------------------
ALTER TABLE rentable_unit ADD COLUMN cost_center int default 0;
ALTER TABLE concept ADD COLUMN cost_center int default 0;
ALTER TABLE concept ADD COLUMN cost_center_type int default 1;


-------------------- JL (Sep 2nd 2011. Adicion de entidad facturadora en cuentas y de retention en retantion_rate)-----------------------------
ALTER TABLE retention_rate ADD COLUMN retantion BOOLEAN DEFAULT true NOT NULL;

ALTER TABLE retention_rate_account ADD COLUMN biller INTEGER;
ALTER TABLE retention_rate_account ADD COLUMN ACCOUNTS_RECEIVABLE_CODE INTEGER;
ALTER TABLE retention_rate_account ADD CONSTRAINT business_entity_retention_rate_account_fk
FOREIGN KEY (biller)
REFERENCES business_entity (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

INSERT INTO retention_rate(id,"name", retantion) VALUES (6,'Cuentas por cobrar',false);
INSERT INTO retention_rate(id,"name", retantion) VALUES (7,'Bancos',false);
INSERT INTO retention_rate(id,"name", retantion) VALUES (8,'Ingresos',false);
INSERT INTO retention_rate(id,"name", retantion) VALUES (9,'Castigo de cartera',false);
INSERT INTO retention_rate(id,"name", retantion) VALUES (10,'Descuentos',false);


-- ***** PASO DE DATOS DE FORMAS DE PAGO A RETENTION RATE ACCOUNT (CUENTAS) **********--
INSERT INTO retention_rate_account(biller,"name", retention_rate, account, ACCOUNTS_RECEIVABLE_CODE)
(select DISTINCT(pp.biller),pf.description,6,pf.count, pf.id from project_property as pp, payment_form as pf, concept cp where cp.project_property = pp.id AND cp.payment_form = pf.id);
  
ALTER TABLE concept_retention_rate_account ALTER COLUMN id SET DEFAULT nextval('retention_rate_account_increment_seg'::regclass);

INSERT INTO concept_retention_rate_account(concept, retention_rate_account)
(SELECT DISTINCT(cp.id), rra.id from project_property pp, retention_rate_account rra, payment_form pf, concept cp where 
	cp.payment_form = pf.id and
	pf.description = rra.name and
	pf.count = rra.account and
	cp.project_property = pp.id and
	pp.biller = rra.biller);
	
-- ***** ELMINAR PAYMENT_FORM (CUENTAS POR COBRAR) **********--
	
ALTER TABLE concept DROP CONSTRAINT payment_form_concept_fk;
ALTER TABLE concept DROP COLUMN payment_form;
DROP TABLE payment_form;

-- ***** PASO DE DATOS DE INGRESOS (CUENTAS DE INBGRESOS) **********--
INSERT INTO retention_rate_account(biller,"name", retention_rate, account)
	(select Distinct(pp.biller),'Cuenta contable ingresos', 8 ,c.accounting_credit_accounts from concept c, retention_rate_account rra, project_property pp 
		where 
			not (c.accounting_credit_accounts is null) and 
			c.project_property = pp.id
	)
;

INSERT INTO concept_retention_rate_account(concept, retention_rate_account)
	(select c.id, rra.id from concept c, retention_rate_account rra, project_property pp
		where 
			not (c.accounting_credit_accounts is null) and
			rra.name = 'Cuenta contable ingresos' and
			c.accounting_credit_accounts = rra.account and
			c.project_property = pp.id and
			pp.biller = rra.biller
	)
;

-- ***** PASO DE DATOS DE BANCOS (CUENTAS DE BANCOS) **********--
INSERT INTO retention_rate_account(biller,"name", retention_rate, account)
	(select Distinct(pp.biller),'Cuenta contable bancos',7 ,c.accounting_accounts_recover from concept c, retention_rate_account rra, project_property pp 
		where 
			not (c.accounting_accounts_recover is null) and 
			c.project_property = pp.id
	)
;

INSERT INTO concept_retention_rate_account(concept, retention_rate_account)
	(select c.id, rra.id from concept c, retention_rate_account rra, project_property pp
		where 
			not (c.accounting_accounts_recover is null) and
			rra.name = 'Cuenta contable bancos' and
			c.accounting_accounts_recover = rra.account and
			c.project_property = pp.id and
			pp.biller = rra.biller
	)
;

-- ***** PASO DE DATOS DE DESCUENTO(CUENTAS DE DESCUENTO) **********--

INSERT INTO retention_rate_account(biller,"name", retention_rate, account)
	(select Distinct(pp.biller),'Cuenta contable descuentos',10 ,trim(both ' ' from c.accounting_accounts_early_payment) from concept c, retention_rate_account rra, project_property pp 
		where 
			not (c.accounting_accounts_early_payment is null) and 
			char_length(c.accounting_accounts_early_payment) > 0 and
			c.project_property = pp.id
	)
;

INSERT INTO concept_retention_rate_account(concept, retention_rate_account)
	(select c.id, rra.id from concept c, retention_rate_account rra, project_property pp
		where 
			not (c.accounting_accounts_recover is null) and
			rra.name = 'Cuenta contable descuentos' and
			rra.account = trim(both ' ' from c.accounting_accounts_early_payment)and
			c.project_property = pp.id and
			pp.biller = rra.biller
	)
;

-- ***** ELMINAR CUENTAS DE LOS CONCEPTOS **********--

ALTER TABLE concept DROP COLUMN accounting_accounts_early_payment;
ALTER TABLE concept DROP COLUMN accounting_accounts_recover;
ALTER TABLE concept DROP COLUMN accounting_credit_accounts;

-- ***** Paso de datos de retenciones **********--
INSERT INTO retention_rate_account(biller,name, retention_rate,account, value)
	(select Distinct(pp.biller), rra.name, rra.retention_rate, rra.account, rra.value from concept c, retention_rate_account rra, concept_retention_rate_account crra, project_property pp
		where 
			crra.concept = c.id and
			crra.retention_rate_account = rra.id and
			rra.retention_rate in (1,2,4) and
			c.project_property = pp.id
	)
;

UPDATE concept_retention_rate_account
   SET retention_rate_account = (SELECT rra1.id
	FROM retention_rate_account rra, retention_rate_account rra1 , concept c, project_property pp
	WHERE 
		c.id = concept_retention_rate_account.concept 
		AND concept_retention_rate_account.retention_rate_account = rra.id
		AND c.project_property = pp.id
		AND rra1.biller = pp.biller
		AND rra1.name = rra.name
		AND rra1.retention_rate = rra.retention_rate
		AND rra1.account = rra.account
		AND rra1.value = rra.value
) WHERE concept_retention_rate_account.retention_rate_account IN (SELECT rra.id FROM retention_rate_account rra WHERE rra.retention_rate IN (1,2,4));

-- ***** Paso de datos de IVA **********--

INSERT INTO retention_rate_account(biller,name, retention_rate,account, value)
	(SELECT DISTINCT(pp.biller),rra.name, rra.retention_rate, rra.account, rra.value FROM retention_rate_account rra, concept c, project_property pp
		where 
			not (tax is null) 
			and c.project_property = pp.id 
			and rra.id = c.tax
	)
;


INSERT INTO concept_retention_rate_account(concept, retention_rate_account)
	(select c.id, rra1.id from concept c, retention_rate_account rra, project_property pp, retention_rate_account rra1
		where 
			c.tax = rra.id 
			AND c.project_property = pp.id
			AND rra1.biller = pp.biller
			AND rra1.name = rra.name
	)
;

-- ***** Paso de datos de Impuesto al timbre **********--
INSERT INTO retention_rate_account(biller,name, retention_rate,account, value)
	(SELECT DISTINCT(pp.biller),rra.name, rra.retention_rate, rra.account, rra.value FROM retention_rate_account rra, concept c, project_property pp
		where 
			not (c.stamptax is null) 
			and c.project_property = pp.id 
			and rra.id = c.stamptax
	)
;

INSERT INTO concept_retention_rate_account(concept, retention_rate_account)
	select c.id, rra1.id from concept c, retention_rate_account rra, retention_rate_account rra1, project_property pp 
	where 
		rra.id = c.stamptax 
		and rra1.name = rra.name 
		and rra1.biller = pp.biller 
		and c.project_property = pp.id 
		and rra1.retention_rate = 5
;


-- ***** ELMINAR CUENTAS DE LOS CONCEPTOS **********--

ALTER TABLE concept DROP COLUMN tax;
ALTER TABLE concept DROP COLUMN stamptax;


-------------------- DAVIDO (Sept 9th 2011 16:28 pm)-----------------------------
ALTER TABLE retention_rate_account ADD COLUMN nature_billing char(1) default 'D';
ALTER TABLE retention_rate_account ADD COLUMN nature_recover char(1) default 'D';
ALTER TABLE retention_rate_account ADD COLUMN nature_credit_note char(1) default 'D';
ALTER TABLE retention_rate_account ADD COLUMN description varchar(255);

insert into features (page) values ('RetentionRateAccount.xhtml');
insert into role_features (id, role, features) values ((select max(id) from role_features)+1, 1, (select id from features where page = 'RetentionRateAccount.xhtml'));


