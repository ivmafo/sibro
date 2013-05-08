-------Script numero 14 add 16/08/2011------------------------------------------

----------------------------(WA)-(2011-08-24)--Modificación del campo  value_concept
--- de integer a double en la tabla rentable_unit--------------------------------------------------
-------Limpiado de maker_checker y maker_checker_x_project ---------------------------------------
ALTER TABLE rentable_unit ALTER COLUMN value_concept TYPE DOUBLE precision;
delete from maker_checker_x_project where maker_ckecker in (select id from maker_checker where class_name ='org.koghi.terranvm.entity.RealProperty');
delete from maker_checker where class_name ='org.koghi.terranvm.entity.RealProperty';



-------Script numero 14 add 17/08/2011------------------------------------------

----------------------------(WA)-(2011-08-17)--creacion de un nuevo perfil con los permisos de 
--activo PEI y de Creacion Hojas de Terminos--------------------------------------------------



insert into role(role_name) values ('Creación Hojas de Terminos Arq');
insert into role_features (role,features) values(12,99);
insert into role_features (role,features) values(12,103);
insert into role_features (role,features) values(12,104);
insert into role_features (role,features) values(12,118);
insert into role_features (role,features) values(12,156);
insert into role_features (role,features) values(12,157);
insert into role_features (role,features) values(12,158);
insert into role_features (role,features) values(12,159);
insert into role_features (role,features) values(12,160);
insert into role_features (role,features) values(12,161);
insert into role_features (role,features) values(12,16);
insert into role_features (role,features) values(12,21);
insert into role_features (role,features) values(12,44);
insert into role_features (role,features) values(12,45);
insert into role_features (role,features) values(12,46);
insert into role_features (role,features) values(12,96);
insert into role_features (role,features) values(12,98);
insert into role_features (role,features) values(12,135);
insert into role_features (role,features) values(12,170);
insert into role_features (role,features) values(12,187);
insert into role_features (role,features) values(12,188);

----------------------------(WA)-(2011-08-24)--Modificación del campo  print_description
--- habilitando los valores null---------------------------------------------------------

ALTER TABLE concept ALTER COLUMN print_description DROP NOT NULL;


----------------------------(WA)-(2011-08-24)--
--- solucion del bug 1388 ---------------------------------------------------------------

insert into role_features(id,role,features) values (729,7,96);
insert into role_features(id,role,features) values (730,7,93);
insert into role_features(id,role,features) values (731,11,96);
insert into role_features(id,role,features) values (732,11,93);

-------------------------------------------------------------------------------------------
-----se actualiza public.rentable_unit_id_seq, ya que esta genera problemas por carga masiva(no deja guardar unidades arrendables)
---------------------------(WA)-(2011-08-24)--Modificación del campo  print_description

SELECT setval('public.rentable_unit_id_seq', 578, true);
