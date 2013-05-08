-----------------------------------------Script 24-----------------------------------------------------

-------------------------------------wamaya 14-06-2012--------------------------------------------------
CREATE TABLE minimum_wage
(
   id integer NOT NULL, 
   "year" integer NOT NULL, 
   "value" double precision, 
   "date" date,
   CONSTRAINT id PRIMARY KEY (id)    
) 
;
CREATE SEQUENCE minimum_wage_id_seq
   INCREMENT 1
   START 1;
ALTER SEQUENCE minimum_wage_id_seq OWNED BY minimum_wage.id;


-------------------------------------wamaya 19-06-2012--------------------------------------------------
INSERT INTO system_variable(name,sintax,is_systemvariable) VALUES ('SALARIO MINIMO','SM[YEAR]',true);

--------------------------------------csolano 21-06-2012------------------------------------------------

insert into features (id, page) values((select max(id) from features)+1,'MinimunWageList.xhtml');
insert into features (id, page) values((select max(id) from features)+1,'MinimunWageEdit.xhtml');
insert into features (id, page) values((select max(id) from features)+1,'MinimunWage.xhtml');

insert into role_features (id, role, features) values ((select max(id) from role_features)+1,1,419);
insert into role_features (id, role, features) values ((select max(id) from role_features)+1,1,420);
insert into role_features (id, role, features) values ((select max(id) from role_features)+1,1,421);


-------------------------------------wamaya 22-06-2012--------------------------------------------------
alter table real_property add pending_approve boolean default true not null ;
alter table rentable_unit add pending_approve boolean  default true not null;

-------------------------------------csolano 25-06-12------------------------------------------------------------------

alter table real_property add deactivate boolean default false not null ;
alter table real_property add motive varchar(1000);

alter table construction add deactivate boolean  default false not null;
alter table construction add motive varchar(500);

alter table floor add deactivate boolean  default false not null;
alter table floor add motive varchar(500);

alter table rentable_unit add deactivate boolean  default false not null;
alter table rentable_unit add motive varchar(500);

alter table area add deactivate boolean  default false not null;
alter table area add motive varchar(500);
