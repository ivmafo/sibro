-----------------------------------------------------miercoles 16 de marzo de 2011------------------------﻿--------------------------------------



--------------------------------------------------Creacion object_of_contract----------------------------------------------﻿--------------------------------------
CREATE SEQUENCE public.object_of_contract_id_seq;

CREATE TABLE public.object_of_contract (
                id INTEGER NOT NULL DEFAULT nextval('public.object_of_contract_id_seq'),
                description VARCHAR NOT NULL,
                CONSTRAINT object_of_contract_pk PRIMARY KEY (id)
);


ALTER SEQUENCE public.object_of_contract_id_seq OWNED BY public.object_of_contract.id;


-------------------------------------------------------------Creacion de Tabla Clause----------------------------------﻿--------------------------------------
CREATE SEQUENCE public.clause_id_seq;

CREATE TABLE public.clause (
                id INTEGER NOT NULL DEFAULT nextval('public.clause_id_seq'),
                clause_number VARCHAR,
                description VARCHAR,
                project_property INTEGER,
                CONSTRAINT clause_id PRIMARY KEY (id)
);

ALTER SEQUENCE public.clause_id_seq OWNED BY public.clause.id;

ALTER TABLE public.clause ADD CONSTRAINT project_property_clause_fk
FOREIGN KEY (project_property)
REFERENCES public.project_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


--------------------------------------------------------Creacion de Role------------------------﻿--------------------------------------
CREATE SEQUENCE public.role_id_seq;

CREATE TABLE public.role (
                id INTEGER NOT NULL DEFAULT nextval('public.role_id_seq'),
                role_name VARCHAR NOT NULL,
                CONSTRAINT role_pk PRIMARY KEY (id)
);


ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;



--------------------------------------------------------Creacion de features------------------------﻿--------------------------------------

CREATE SEQUENCE public.features_id_seq;

CREATE TABLE public.features (
                id INTEGER NOT NULL DEFAULT nextval('public.features_id_seq'),
                page VARCHAR NOT NULL,
                CONSTRAINT features_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.features_id_seq OWNED BY public.features.id;



--------------------------------------------------Creacion Role_Features-------------------------------------------------------﻿--------------------------------------


CREATE SEQUENCE public.role_features_id_seq;

CREATE TABLE public.role_features (
                id INTEGER NOT NULL DEFAULT nextval('public.role_features_id_seq'),
                role INTEGER NOT NULL,
                features INTEGER NOT NULL,
                CONSTRAINT role_features_pk PRIMARY KEY (id)
);



ALTER TABLE public.role_features ADD CONSTRAINT features_rol_features_fk
FOREIGN KEY (features)
REFERENCES public.features (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


ALTER TABLE public.role_features ADD CONSTRAINT rol_rol_features_fk
FOREIGN KEY (role)
REFERENCES public.role (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--------------------------------------------------Creacion Tabla Action-------------------------------------------------------﻿--------------------------------------

CREATE SEQUENCE public.action_id_seq;

CREATE TABLE public.action (
                id INTEGER NOT NULL DEFAULT nextval('public.action_id_seq'),
                features INTEGER NOT NULL,
                action VARCHAR NOT NULL,
                CONSTRAINT action_pk PRIMARY KEY (id)
);


ALTER SEQUENCE public.action_id_seq OWNED BY public.action.id;

ALTER TABLE public.action ADD CONSTRAINT features_action_fk
FOREIGN KEY (features)
REFERENCES public.features (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


--------------------------------------Insertar paginas del proyecto, Features---------------------------------


ALTER SEQUENCE features_id_seq RESTART WITH 129;

INSERT INTO features (id, page) VALUES (113, 'RentableUnitContributionEdit.xhtml');
INSERT INTO features (id, page) VALUES (17, 'BusinessEntityContact.xhtml');
INSERT INTO features (id, page) VALUES (4, 'Area.xhtml');
INSERT INTO features (id, page) VALUES (5, 'AreaEdit.xhtml');
INSERT INTO features (id, page) VALUES (6, 'AreaList.xhtml');
INSERT INTO features (id, page) VALUES (7, 'AreaType.xhtml');
INSERT INTO features (id, page) VALUES (8, 'AreaTypeEdit.xhtml');
INSERT INTO features (id, page) VALUES (9, 'AreaTypeList.xhtml');
INSERT INTO features (id, page) VALUES (10, 'Avaluation.xhtml');
INSERT INTO features (id, page) VALUES (11, 'AvaluationEdit.xhtml');
INSERT INTO features (id, page) VALUES (12, 'AvaluationList.xhtml');
INSERT INTO features (id, page) VALUES (13, 'BillingResolution.xhtml');
INSERT INTO features (id, page) VALUES (14, 'BillingResolutionEdit.xhtml');
INSERT INTO features (id, page) VALUES (15, 'BillingResolutionList.xhtml');
INSERT INTO features (id, page) VALUES (16, 'BusinessEntity.xhtml');
INSERT INTO features (id, page) VALUES (18, 'BusinessEntityContactEdit.xhtml');
INSERT INTO features (id, page) VALUES (20, 'BusinessEntityEdit.xhtml');
INSERT INTO features (id, page) VALUES (21, 'BusinessEntityList.xhtml');
INSERT INTO features (id, page) VALUES (22, 'BusinessEntityType.xhtml');
INSERT INTO features (id, page) VALUES (23, 'BusinessEntityTypeEdit.xhtml');
INSERT INTO features (id, page) VALUES (24, 'BusinessEntityTypeList.xhtml');
INSERT INTO features (id, page) VALUES (25, 'BusinessLine.xhtml');
INSERT INTO features (id, page) VALUES (26, 'BusinessLineEdit.xhtml');
INSERT INTO features (id, page) VALUES (27, 'BusinessLineList.xhtml');
INSERT INTO features (id, page) VALUES (28, 'City.xhtml');
INSERT INTO features (id, page) VALUES (29, 'CityEdit.xhtml');
INSERT INTO features (id, page) VALUES (30, 'CityList.xhtml');
INSERT INTO features (id, page) VALUES (31, 'Concept.xhtml');
INSERT INTO features (id, page) VALUES (32, 'ConceptEdit.xhtml');
INSERT INTO features (id, page) VALUES (33, 'ConceptList.xhtml');
INSERT INTO features (id, page) VALUES (34, 'confirm.xhtml');
INSERT INTO features (id, page) VALUES (35, 'ConsecutiveAccountsBilling.xhtml');
INSERT INTO features (id, page) VALUES (36, 'ConsecutiveAccountsBillingEdit.xhtml');
INSERT INTO features (id, page) VALUES (37, 'ConsecutiveAccountsBillingList.xhtml');
INSERT INTO features (id, page) VALUES (38, 'ConsecutiveCreditNotes.xhtml');
INSERT INTO features (id, page) VALUES (39, 'ConsecutiveCreditNotesEdit.xhtml');
INSERT INTO features (id, page) VALUES (40, 'ConsecutiveCreditNotesList.xhtml');
INSERT INTO features (id, page) VALUES (41, 'Construction.xhtml');
INSERT INTO features (id, page) VALUES (42, 'ConstructionEdit.xhtml');
INSERT INTO features (id, page) VALUES (43, 'ConstructionList.xhtml');
INSERT INTO features (id, page) VALUES (44, 'Contact.xhtml');
INSERT INTO features (id, page) VALUES (45, 'ContactEdit.xhtml');
INSERT INTO features (id, page) VALUES (46, 'ContactList.xhtml');
INSERT INTO features (id, page) VALUES (47, 'ContactType.xhtml');
INSERT INTO features (id, page) VALUES (48, 'ContactTypeEdit.xhtml');
INSERT INTO features (id, page) VALUES (49, 'ContactTypeList.xhtml');
INSERT INTO features (id, page) VALUES (50, 'ContractType.xhtml');
INSERT INTO features (id, page) VALUES (51, 'ContractTypeEdit.xhtml');
INSERT INTO features (id, page) VALUES (52, 'ContractTypeList.xhtml');
INSERT INTO features (id, page) VALUES (53, 'ContributionModule.xhtml');
INSERT INTO features (id, page) VALUES (54, 'ContributionModuleEdit.xhtml');
INSERT INTO features (id, page) VALUES (55, 'ContributionModuleList.xhtml');
INSERT INTO features (id, page) VALUES (56, 'ContributionModuleTap.xhtml');
INSERT INTO features (id, page) VALUES (57, 'Country.xhtml');
INSERT INTO features (id, page) VALUES (58, 'CountryEdit.xhtml');
INSERT INTO features (id, page) VALUES (59, 'CountryList.xhtml');
INSERT INTO features (id, page) VALUES (60, 'EconomicActivity.xhtml');
INSERT INTO features (id, page) VALUES (61, 'EconomicActivityEdit.xhtml');
INSERT INTO features (id, page) VALUES (62, 'EconomicActivityList.xhtml');
INSERT INTO features (id, page) VALUES (63, 'EconomicSector.xhtml');
INSERT INTO features (id, page) VALUES (64, 'EconomicSectorEdit.xhtml');
INSERT INTO features (id, page) VALUES (65, 'EconomicSectorList.xhtml');
INSERT INTO features (id, page) VALUES (66, 'EntityType.xhtml');
INSERT INTO features (id, page) VALUES (67, 'EntityTypeEdit.xhtml');
INSERT INTO features (id, page) VALUES (68, 'EntityTypeList.xhtml');
INSERT INTO features (id, page) VALUES (69, 'error.xhtml');
INSERT INTO features (id, page) VALUES (70, 'Floor.xhtml');
INSERT INTO features (id, page) VALUES (71, 'FloorEdit.xhtml');
INSERT INTO features (id, page) VALUES (72, 'FloorList.xhtml');
INSERT INTO features (id, page) VALUES (73, 'home.xhtml');
INSERT INTO features (id, page) VALUES (74, 'InvoiceList.xhtml');
INSERT INTO features (id, page) VALUES (75, 'LegalNatureOfProperty.xhtml');
INSERT INTO features (id, page) VALUES (76, 'LegalNatureOfPropertyEdit.xhtml');
INSERT INTO features (id, page) VALUES (77, 'LegalNatureOfPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (78, 'login.xhtml');
INSERT INTO features (id, page) VALUES (79, 'MakerChecker.xhtml');
INSERT INTO features (id, page) VALUES (80, 'MakerCheckerBusinessEntityList.xhtml');
INSERT INTO features (id, page) VALUES (81, 'MakerCheckerBusinessLineList.xhtml');
INSERT INTO features (id, page) VALUES (82, 'MakerCheckerEdit.xhtml');
INSERT INTO features (id, page) VALUES (83, 'MakerCheckerList.xhtml');
INSERT INTO features (id, page) VALUES (84, 'MakerCheckerProjectList.xhtml');
INSERT INTO features (id, page) VALUES (85, 'MakerCheckerRealPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (86, 'PaymentForm.xhtml');
INSERT INTO features (id, page) VALUES (87, 'PhoneNumber.xhtml');
INSERT INTO features (id, page) VALUES (88, 'PhoneNumbreEdit.xhtml');
INSERT INTO features (id, page) VALUES (89, 'PhoneNumbreList.xhtml');
INSERT INTO features (id, page) VALUES (90, 'Plugin.xhtml');
INSERT INTO features (id, page) VALUES (92, 'PluginList.xhtml');
INSERT INTO features (id, page) VALUES (91, 'PluginEdit.xhtml');
INSERT INTO features (id, page) VALUES (93, 'Project.xhtml');
INSERT INTO features (id, page) VALUES (94, 'ProjectEdit.xhtml');
INSERT INTO features (id, page) VALUES (95, 'ProjectList.xhtml');
INSERT INTO features (id, page) VALUES (96, 'ProjectProperty.xhtml');
INSERT INTO features (id, page) VALUES (97, 'ProjectPropertyEdit.xhtml');
INSERT INTO features (id, page) VALUES (98, 'ProjectPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (99, 'RealProperty.xhtml');
INSERT INTO features (id, page) VALUES (100, 'RealPropertyContact.xhtml');
INSERT INTO features (id, page) VALUES (101, 'RealPropertyContactEdit.xhtml');
INSERT INTO features (id, page) VALUES (102, 'RealPropertyContactList.xhtml');
INSERT INTO features (id, page) VALUES (103, 'RealPropertyEdit.xhtml');
INSERT INTO features (id, page) VALUES (104, 'RealPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (105, 'RealPropetyUseType.xhtml');
INSERT INTO features (id, page) VALUES (106, 'RealPropertyUseTypeEdit.xhtml');
INSERT INTO features (id, page) VALUES (107, 'RealPropertyUseTypeList.xhtml');
INSERT INTO features (id, page) VALUES (108, 'Region.xhtml');
INSERT INTO features (id, page) VALUES (109, 'RegionEdit.xhtml');
INSERT INTO features (id, page) VALUES (110, 'RegionList.xhtml');
INSERT INTO features (id, page) VALUES (111, 'RentableUnit.xhtml');
INSERT INTO features (id, page) VALUES (112, 'RentableUnitContribution.xhtml');
INSERT INTO features (id, page) VALUES (114, 'RentableUnitContributionList.xhtml');
INSERT INTO features (id, page) VALUES (115, 'RentableUnitEdit.xhtml');
INSERT INTO features (id, page) VALUES (116, 'RentableUnitList.xhtml');
INSERT INTO features (id, page) VALUES (1, 'Address.xhtml');
INSERT INTO features (id, page) VALUES (2, 'AddressEdit.xhtml');
INSERT INTO features (id, page) VALUES (3, 'AddressList.xhtml');
INSERT INTO features (id, page) VALUES (117, 'MakerCheckerProjectPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (118, 'ProjectList.xhtml');
INSERT INTO features (id, page) VALUES (119, 'BusinessLineList.xhtml');
INSERT INTO features (id, page) VALUES (120, 'AddressList.xhtml');
INSERT INTO features (id, page) VALUES (121, 'AreaList.xhtml');
INSERT INTO features (id, page) VALUES (122, 'AreaTypeList.xhtml');
INSERT INTO features (id, page) VALUES (123, 'AvaluationList.xhtml');
INSERT INTO features (id, page) VALUES (124, 'BillingResolutionList.xhtml');
INSERT INTO features (id, page) VALUES (125, 'BusinessEntityContactList.xhtml');
INSERT INTO features (id, page) VALUES (19, 'BusinessEntityContactList.xhtml');
INSERT INTO features (id, page) VALUES (126, 'RealPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (127, 'ProjectPropertyList.xhtml');
INSERT INTO features (id, page) VALUES (128, 'MakerCheckerProjectPropertyList.xhtml');


--------------------------------------------------------------------------------------------------------------------------




--------------------------------------------------Creacion User_Terranvm-------------------------------------------------------﻿--------------------------------------
CREATE SEQUENCE public.user_terranvm_id_seq;

CREATE TABLE public.user_terranvm (
                id INTEGER NOT NULL DEFAULT nextval('public.user_terranvm_id_seq'),
                login VARCHAR NOT NULL,
                password VARCHAR NOT NULL,
                effective_date DATE NOT NULL,
                state INTEGER NOT NULL,
                mail VARCHAR NOT NULL,
                nombre VARCHAR NOT NULL,
                role INTEGER NOT NULL,
                CONSTRAINT user_terranvm_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.user_terranvm_id_seq OWNED BY public.user_terranvm.id;

ALTER TABLE public.user_terranvm ADD CONSTRAINT rol_user_fk
FOREIGN KEY (role)
REFERENCES public.role (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

---------------------------------------------------------Creacion tabla Project_user-----------------------------------------﻿--------------------------------------
CREATE SEQUENCE public.project_user_id_seq;

CREATE TABLE public.project_user (
                id INTEGER NOT NULL DEFAULT nextval('public.project_user_id_seq'),
                user_terranvm INTEGER NOT NULL,
                project INTEGER NOT NULL,
                CONSTRAINT project_user_pk PRIMARY KEY (id)
);


ALTER SEQUENCE public.project_user_id_seq OWNED BY public.project_user.id;

ALTER TABLE public.project_user ADD CONSTRAINT user_proyect_user_fk
FOREIGN KEY (user_terranvm)
REFERENCES public.user_terranvm (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;


-------------------------------creacion de un rol tipo administrador con todos los permisos, un usuario con dicho tipo----------------------------------------------
--ALTER SEQUENCE role_id_seq RESTART WITH 2;
--SELECT pg_catalog.setval('role_id_seq', 1, false);
--INSERT INTO role (id, role_name) VALUES (1, 'administrador');

ALTER SEQUENCE role_id_seq RESTART WITH 6;

INSERT INTO role (id, role_name) VALUES (1, 'Administrador');
INSERT INTO role (id, role_name) VALUES (2, 'Comercial');
INSERT INTO role (id, role_name) VALUES (3, 'Gerente');
INSERT INTO role (id, role_name) VALUES (4, 'Contador');
INSERT INTO role (id, role_name) VALUES (5, 'Facturador');



--ALTER SEQUENCE user_terranvm_id_seq RESTART WITH 2;
--INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (1, 'admin', 'admin', '3000-01-01', 1, 'admin@asdf.com', 'Nombre1', 1);


ALTER SEQUENCE user_terranvm_id_seq RESTART WITH 6;

INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (1, 'admin', 'admin', '3000-01-01', 1, 'admin@asdf.com', 'Nombre1', 1);
INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (2, 'comercial', 'comercial', '3000-01-01', 1, 'user1@asdf.com', 'Nombre2', 2);
INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (3, 'gerente', 'gerente', '3000-01-01', 1, 'user2@asdf.com', 'Nombre3', 3);
INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (4, 'contador', 'contador', '3000-01-01', 1, 'user3@asdf.com', 'Nombre4', 4);
INSERT INTO user_terranvm (id, login, password, effective_date, state, mail, nombre, role) VALUES (5, 'facturador', 'facturador', '3000-01-01', 1, 'user4@asd.com', 'Nombre5', 5);


-------------------------------paginas permitidas por tipo de rol (administrador) -------------------------------------

ALTER SEQUENCE role_features_id_seq RESTART WITH 552;

INSERT INTO role_features (id, role, features) VALUES (147, 2, 31);
INSERT INTO role_features (id, role, features) VALUES (148, 2, 32);
INSERT INTO role_features (id, role, features) VALUES (149, 2, 33);
INSERT INTO role_features (id, role, features) VALUES (151, 2, 34);
INSERT INTO role_features (id, role, features) VALUES (152, 2, 35);
INSERT INTO role_features (id, role, features) VALUES (1, 1, 1);
INSERT INTO role_features (id, role, features) VALUES (2, 1, 2);
INSERT INTO role_features (id, role, features) VALUES (3, 1, 3);
INSERT INTO role_features (id, role, features) VALUES (4, 1, 4);
INSERT INTO role_features (id, role, features) VALUES (5, 1, 5);
INSERT INTO role_features (id, role, features) VALUES (6, 1, 6);
INSERT INTO role_features (id, role, features) VALUES (7, 1, 7);
INSERT INTO role_features (id, role, features) VALUES (8, 1, 8);
INSERT INTO role_features (id, role, features) VALUES (9, 1, 9);
INSERT INTO role_features (id, role, features) VALUES (10, 1, 10);
INSERT INTO role_features (id, role, features) VALUES (11, 1, 11);
INSERT INTO role_features (id, role, features) VALUES (12, 1, 12);
INSERT INTO role_features (id, role, features) VALUES (13, 1, 13);
INSERT INTO role_features (id, role, features) VALUES (14, 1, 14);
INSERT INTO role_features (id, role, features) VALUES (15, 1, 15);
INSERT INTO role_features (id, role, features) VALUES (16, 1, 16);
INSERT INTO role_features (id, role, features) VALUES (17, 1, 17);
INSERT INTO role_features (id, role, features) VALUES (18, 1, 18);
INSERT INTO role_features (id, role, features) VALUES (19, 1, 19);
INSERT INTO role_features (id, role, features) VALUES (20, 1, 20);
INSERT INTO role_features (id, role, features) VALUES (21, 1, 21);
INSERT INTO role_features (id, role, features) VALUES (22, 1, 22);
INSERT INTO role_features (id, role, features) VALUES (23, 1, 23);
INSERT INTO role_features (id, role, features) VALUES (24, 1, 24);
INSERT INTO role_features (id, role, features) VALUES (25, 1, 25);
INSERT INTO role_features (id, role, features) VALUES (26, 1, 26);
INSERT INTO role_features (id, role, features) VALUES (27, 1, 27);
INSERT INTO role_features (id, role, features) VALUES (28, 1, 28);
INSERT INTO role_features (id, role, features) VALUES (29, 1, 29);
INSERT INTO role_features (id, role, features) VALUES (30, 1, 30);
INSERT INTO role_features (id, role, features) VALUES (31, 1, 31);
INSERT INTO role_features (id, role, features) VALUES (32, 1, 32);
INSERT INTO role_features (id, role, features) VALUES (33, 1, 33);
INSERT INTO role_features (id, role, features) VALUES (34, 1, 34);
INSERT INTO role_features (id, role, features) VALUES (35, 1, 35);
INSERT INTO role_features (id, role, features) VALUES (36, 1, 36);
INSERT INTO role_features (id, role, features) VALUES (37, 1, 37);
INSERT INTO role_features (id, role, features) VALUES (38, 1, 38);
INSERT INTO role_features (id, role, features) VALUES (39, 1, 39);
INSERT INTO role_features (id, role, features) VALUES (40, 1, 40);
INSERT INTO role_features (id, role, features) VALUES (41, 1, 41);
INSERT INTO role_features (id, role, features) VALUES (42, 1, 42);
INSERT INTO role_features (id, role, features) VALUES (43, 1, 43);
INSERT INTO role_features (id, role, features) VALUES (44, 1, 44);
INSERT INTO role_features (id, role, features) VALUES (45, 1, 45);
INSERT INTO role_features (id, role, features) VALUES (46, 1, 46);
INSERT INTO role_features (id, role, features) VALUES (47, 1, 47);
INSERT INTO role_features (id, role, features) VALUES (48, 1, 48);
INSERT INTO role_features (id, role, features) VALUES (49, 1, 49);
INSERT INTO role_features (id, role, features) VALUES (50, 1, 50);
INSERT INTO role_features (id, role, features) VALUES (51, 1, 51);
INSERT INTO role_features (id, role, features) VALUES (52, 1, 52);
INSERT INTO role_features (id, role, features) VALUES (53, 1, 53);
INSERT INTO role_features (id, role, features) VALUES (54, 1, 54);
INSERT INTO role_features (id, role, features) VALUES (55, 1, 55);
INSERT INTO role_features (id, role, features) VALUES (56, 1, 56);
INSERT INTO role_features (id, role, features) VALUES (57, 1, 57);
INSERT INTO role_features (id, role, features) VALUES (58, 1, 58);
INSERT INTO role_features (id, role, features) VALUES (59, 1, 59);
INSERT INTO role_features (id, role, features) VALUES (60, 1, 60);
INSERT INTO role_features (id, role, features) VALUES (61, 1, 61);
INSERT INTO role_features (id, role, features) VALUES (62, 1, 62);
INSERT INTO role_features (id, role, features) VALUES (63, 1, 63);
INSERT INTO role_features (id, role, features) VALUES (64, 1, 64);
INSERT INTO role_features (id, role, features) VALUES (65, 1, 65);
INSERT INTO role_features (id, role, features) VALUES (66, 1, 66);
INSERT INTO role_features (id, role, features) VALUES (67, 1, 67);
INSERT INTO role_features (id, role, features) VALUES (68, 1, 68);
INSERT INTO role_features (id, role, features) VALUES (69, 1, 69);
INSERT INTO role_features (id, role, features) VALUES (70, 1, 70);
INSERT INTO role_features (id, role, features) VALUES (71, 1, 71);
INSERT INTO role_features (id, role, features) VALUES (72, 1, 72);
INSERT INTO role_features (id, role, features) VALUES (73, 1, 73);
INSERT INTO role_features (id, role, features) VALUES (74, 1, 74);
INSERT INTO role_features (id, role, features) VALUES (75, 1, 75);
INSERT INTO role_features (id, role, features) VALUES (76, 1, 76);
INSERT INTO role_features (id, role, features) VALUES (77, 1, 77);
INSERT INTO role_features (id, role, features) VALUES (78, 1, 78);
INSERT INTO role_features (id, role, features) VALUES (79, 1, 79);
INSERT INTO role_features (id, role, features) VALUES (80, 1, 80);
INSERT INTO role_features (id, role, features) VALUES (81, 1, 81);
INSERT INTO role_features (id, role, features) VALUES (82, 1, 82);
INSERT INTO role_features (id, role, features) VALUES (83, 1, 83);
INSERT INTO role_features (id, role, features) VALUES (84, 1, 84);
INSERT INTO role_features (id, role, features) VALUES (85, 1, 85);
INSERT INTO role_features (id, role, features) VALUES (86, 1, 86);
INSERT INTO role_features (id, role, features) VALUES (87, 1, 87);
INSERT INTO role_features (id, role, features) VALUES (88, 1, 88);
INSERT INTO role_features (id, role, features) VALUES (89, 1, 89);
INSERT INTO role_features (id, role, features) VALUES (90, 1, 90);
INSERT INTO role_features (id, role, features) VALUES (91, 1, 91);
INSERT INTO role_features (id, role, features) VALUES (92, 1, 92);
INSERT INTO role_features (id, role, features) VALUES (93, 1, 93);
INSERT INTO role_features (id, role, features) VALUES (94, 1, 94);
INSERT INTO role_features (id, role, features) VALUES (95, 1, 95);
INSERT INTO role_features (id, role, features) VALUES (96, 1, 96);
INSERT INTO role_features (id, role, features) VALUES (97, 1, 97);
INSERT INTO role_features (id, role, features) VALUES (98, 1, 98);
INSERT INTO role_features (id, role, features) VALUES (99, 1, 99);
INSERT INTO role_features (id, role, features) VALUES (100, 1, 100);
INSERT INTO role_features (id, role, features) VALUES (101, 1, 101);
INSERT INTO role_features (id, role, features) VALUES (102, 1, 102);
INSERT INTO role_features (id, role, features) VALUES (103, 1, 103);
INSERT INTO role_features (id, role, features) VALUES (104, 1, 104);
INSERT INTO role_features (id, role, features) VALUES (105, 1, 105);
INSERT INTO role_features (id, role, features) VALUES (106, 1, 106);
INSERT INTO role_features (id, role, features) VALUES (107, 1, 107);
INSERT INTO role_features (id, role, features) VALUES (108, 1, 108);
INSERT INTO role_features (id, role, features) VALUES (109, 1, 109);
INSERT INTO role_features (id, role, features) VALUES (110, 1, 110);
INSERT INTO role_features (id, role, features) VALUES (111, 1, 111);
INSERT INTO role_features (id, role, features) VALUES (112, 1, 112);
INSERT INTO role_features (id, role, features) VALUES (113, 1, 113);
INSERT INTO role_features (id, role, features) VALUES (114, 1, 114);
INSERT INTO role_features (id, role, features) VALUES (115, 1, 115);
INSERT INTO role_features (id, role, features) VALUES (116, 1, 116);
INSERT INTO role_features (id, role, features) VALUES (117, 1, 117);
INSERT INTO role_features (id, role, features) VALUES (118, 2, 1);
INSERT INTO role_features (id, role, features) VALUES (119, 2, 2);
INSERT INTO role_features (id, role, features) VALUES (120, 2, 3);
INSERT INTO role_features (id, role, features) VALUES (121, 2, 4);
INSERT INTO role_features (id, role, features) VALUES (122, 2, 5);
INSERT INTO role_features (id, role, features) VALUES (123, 2, 6);
INSERT INTO role_features (id, role, features) VALUES (124, 2, 7);
INSERT INTO role_features (id, role, features) VALUES (125, 2, 8);
INSERT INTO role_features (id, role, features) VALUES (126, 2, 9);
INSERT INTO role_features (id, role, features) VALUES (127, 2, 10);
INSERT INTO role_features (id, role, features) VALUES (128, 2, 11);
INSERT INTO role_features (id, role, features) VALUES (129, 2, 12);
INSERT INTO role_features (id, role, features) VALUES (130, 2, 13);
INSERT INTO role_features (id, role, features) VALUES (131, 2, 14);
INSERT INTO role_features (id, role, features) VALUES (132, 2, 15);
INSERT INTO role_features (id, role, features) VALUES (133, 2, 16);
INSERT INTO role_features (id, role, features) VALUES (134, 2, 17);
INSERT INTO role_features (id, role, features) VALUES (135, 2, 18);
INSERT INTO role_features (id, role, features) VALUES (136, 2, 19);
INSERT INTO role_features (id, role, features) VALUES (137, 2, 20);
INSERT INTO role_features (id, role, features) VALUES (138, 2, 21);
INSERT INTO role_features (id, role, features) VALUES (139, 2, 22);
INSERT INTO role_features (id, role, features) VALUES (140, 2, 23);
INSERT INTO role_features (id, role, features) VALUES (141, 2, 24);
INSERT INTO role_features (id, role, features) VALUES (142, 2, 25);
INSERT INTO role_features (id, role, features) VALUES (144, 2, 28);
INSERT INTO role_features (id, role, features) VALUES (145, 2, 29);
INSERT INTO role_features (id, role, features) VALUES (146, 2, 30);
INSERT INTO role_features (id, role, features) VALUES (153, 2, 36);
INSERT INTO role_features (id, role, features) VALUES (154, 2, 37);
INSERT INTO role_features (id, role, features) VALUES (155, 2, 38);
INSERT INTO role_features (id, role, features) VALUES (156, 2, 39);
INSERT INTO role_features (id, role, features) VALUES (157, 2, 40);
INSERT INTO role_features (id, role, features) VALUES (158, 2, 41);
INSERT INTO role_features (id, role, features) VALUES (159, 2, 42);
INSERT INTO role_features (id, role, features) VALUES (160, 2, 43);
INSERT INTO role_features (id, role, features) VALUES (161, 2, 44);
INSERT INTO role_features (id, role, features) VALUES (162, 2, 45);
INSERT INTO role_features (id, role, features) VALUES (163, 2, 46);
INSERT INTO role_features (id, role, features) VALUES (164, 2, 47);
INSERT INTO role_features (id, role, features) VALUES (165, 2, 48);
INSERT INTO role_features (id, role, features) VALUES (166, 2, 49);
INSERT INTO role_features (id, role, features) VALUES (167, 2, 50);
INSERT INTO role_features (id, role, features) VALUES (168, 2, 51);
INSERT INTO role_features (id, role, features) VALUES (169, 2, 52);
INSERT INTO role_features (id, role, features) VALUES (170, 2, 53);
INSERT INTO role_features (id, role, features) VALUES (171, 2, 54);
INSERT INTO role_features (id, role, features) VALUES (172, 2, 55);
INSERT INTO role_features (id, role, features) VALUES (173, 2, 56);
INSERT INTO role_features (id, role, features) VALUES (174, 2, 57);
INSERT INTO role_features (id, role, features) VALUES (175, 2, 58);
INSERT INTO role_features (id, role, features) VALUES (176, 2, 59);
INSERT INTO role_features (id, role, features) VALUES (177, 2, 63);
INSERT INTO role_features (id, role, features) VALUES (178, 2, 64);
INSERT INTO role_features (id, role, features) VALUES (179, 2, 65);
INSERT INTO role_features (id, role, features) VALUES (180, 2, 66);
INSERT INTO role_features (id, role, features) VALUES (181, 2, 67);
INSERT INTO role_features (id, role, features) VALUES (182, 2, 68);
INSERT INTO role_features (id, role, features) VALUES (183, 2, 69);
INSERT INTO role_features (id, role, features) VALUES (184, 2, 70);
INSERT INTO role_features (id, role, features) VALUES (185, 2, 71);
INSERT INTO role_features (id, role, features) VALUES (186, 2, 72);
INSERT INTO role_features (id, role, features) VALUES (187, 2, 73);
INSERT INTO role_features (id, role, features) VALUES (188, 2, 74);
INSERT INTO role_features (id, role, features) VALUES (189, 2, 78);
INSERT INTO role_features (id, role, features) VALUES (190, 2, 86);
INSERT INTO role_features (id, role, features) VALUES (191, 2, 87);
INSERT INTO role_features (id, role, features) VALUES (192, 2, 88);
INSERT INTO role_features (id, role, features) VALUES (193, 2, 89);
INSERT INTO role_features (id, role, features) VALUES (194, 2, 90);
INSERT INTO role_features (id, role, features) VALUES (195, 2, 91);
INSERT INTO role_features (id, role, features) VALUES (196, 2, 92);
INSERT INTO role_features (id, role, features) VALUES (197, 2, 93);
INSERT INTO role_features (id, role, features) VALUES (200, 2, 96);
INSERT INTO role_features (id, role, features) VALUES (201, 2, 97);
INSERT INTO role_features (id, role, features) VALUES (143, 2, 119);
INSERT INTO role_features (id, role, features) VALUES (202, 2, 98);
INSERT INTO role_features (id, role, features) VALUES (203, 2, 99);
INSERT INTO role_features (id, role, features) VALUES (204, 2, 100);
INSERT INTO role_features (id, role, features) VALUES (205, 2, 101);
INSERT INTO role_features (id, role, features) VALUES (206, 2, 102);
INSERT INTO role_features (id, role, features) VALUES (207, 2, 103);
INSERT INTO role_features (id, role, features) VALUES (208, 2, 104);
INSERT INTO role_features (id, role, features) VALUES (209, 2, 108);
INSERT INTO role_features (id, role, features) VALUES (210, 2, 109);
INSERT INTO role_features (id, role, features) VALUES (211, 2, 110);
INSERT INTO role_features (id, role, features) VALUES (212, 2, 111);
INSERT INTO role_features (id, role, features) VALUES (213, 2, 112);
INSERT INTO role_features (id, role, features) VALUES (214, 2, 113);
INSERT INTO role_features (id, role, features) VALUES (215, 2, 114);
INSERT INTO role_features (id, role, features) VALUES (216, 2, 115);
INSERT INTO role_features (id, role, features) VALUES (217, 2, 116);
INSERT INTO role_features (id, role, features) VALUES (198, 2, 118);
INSERT INTO role_features (id, role, features) VALUES (218, 3, 1);
INSERT INTO role_features (id, role, features) VALUES (219, 3, 2);
INSERT INTO role_features (id, role, features) VALUES (221, 3, 4);
INSERT INTO role_features (id, role, features) VALUES (223, 3, 5);
INSERT INTO role_features (id, role, features) VALUES (225, 3, 7);
INSERT INTO role_features (id, role, features) VALUES (226, 3, 8);
INSERT INTO role_features (id, role, features) VALUES (228, 3, 10);
INSERT INTO role_features (id, role, features) VALUES (229, 3, 11);
INSERT INTO role_features (id, role, features) VALUES (231, 3, 13);
INSERT INTO role_features (id, role, features) VALUES (232, 3, 14);
INSERT INTO role_features (id, role, features) VALUES (236, 3, 16);
INSERT INTO role_features (id, role, features) VALUES (237, 3, 17);
INSERT INTO role_features (id, role, features) VALUES (238, 3, 18);
INSERT INTO role_features (id, role, features) VALUES (240, 3, 20);
INSERT INTO role_features (id, role, features) VALUES (241, 3, 21);
INSERT INTO role_features (id, role, features) VALUES (242, 3, 22);
INSERT INTO role_features (id, role, features) VALUES (243, 3, 23);
INSERT INTO role_features (id, role, features) VALUES (244, 3, 24);
INSERT INTO role_features (id, role, features) VALUES (245, 3, 25);
INSERT INTO role_features (id, role, features) VALUES (246, 3, 26);
INSERT INTO role_features (id, role, features) VALUES (248, 3, 28);
INSERT INTO role_features (id, role, features) VALUES (249, 3, 29);
INSERT INTO role_features (id, role, features) VALUES (250, 3, 30);
INSERT INTO role_features (id, role, features) VALUES (252, 3, 32);
INSERT INTO role_features (id, role, features) VALUES (253, 3, 33);
INSERT INTO role_features (id, role, features) VALUES (251, 3, 31);
INSERT INTO role_features (id, role, features) VALUES (254, 3, 34);
INSERT INTO role_features (id, role, features) VALUES (255, 3, 35);
INSERT INTO role_features (id, role, features) VALUES (256, 3, 36);
INSERT INTO role_features (id, role, features) VALUES (257, 3, 37);
INSERT INTO role_features (id, role, features) VALUES (258, 3, 38);
INSERT INTO role_features (id, role, features) VALUES (259, 3, 39);
INSERT INTO role_features (id, role, features) VALUES (262, 3, 41);
INSERT INTO role_features (id, role, features) VALUES (260, 3, 40);
INSERT INTO role_features (id, role, features) VALUES (263, 3, 42);
INSERT INTO role_features (id, role, features) VALUES (264, 3, 43);
INSERT INTO role_features (id, role, features) VALUES (265, 3, 44);
INSERT INTO role_features (id, role, features) VALUES (266, 3, 45);
INSERT INTO role_features (id, role, features) VALUES (267, 3, 46);
INSERT INTO role_features (id, role, features) VALUES (268, 3, 47);
INSERT INTO role_features (id, role, features) VALUES (269, 3, 48);
INSERT INTO role_features (id, role, features) VALUES (270, 3, 49);
INSERT INTO role_features (id, role, features) VALUES (271, 3, 50);
INSERT INTO role_features (id, role, features) VALUES (272, 3, 51);
INSERT INTO role_features (id, role, features) VALUES (273, 3, 52);
INSERT INTO role_features (id, role, features) VALUES (274, 3, 53);
INSERT INTO role_features (id, role, features) VALUES (275, 3, 54);
INSERT INTO role_features (id, role, features) VALUES (276, 3, 55);
INSERT INTO role_features (id, role, features) VALUES (277, 3, 56);
INSERT INTO role_features (id, role, features) VALUES (278, 3, 57);
INSERT INTO role_features (id, role, features) VALUES (279, 3, 58);
INSERT INTO role_features (id, role, features) VALUES (280, 3, 59);
INSERT INTO role_features (id, role, features) VALUES (281, 3, 60);
INSERT INTO role_features (id, role, features) VALUES (282, 3, 61);
INSERT INTO role_features (id, role, features) VALUES (283, 3, 62);
INSERT INTO role_features (id, role, features) VALUES (284, 3, 63);
INSERT INTO role_features (id, role, features) VALUES (285, 3, 64);
INSERT INTO role_features (id, role, features) VALUES (286, 3, 65);
INSERT INTO role_features (id, role, features) VALUES (287, 3, 66);
INSERT INTO role_features (id, role, features) VALUES (288, 3, 67);
INSERT INTO role_features (id, role, features) VALUES (289, 3, 68);
INSERT INTO role_features (id, role, features) VALUES (290, 3, 69);
INSERT INTO role_features (id, role, features) VALUES (291, 3, 70);
INSERT INTO role_features (id, role, features) VALUES (292, 3, 71);
INSERT INTO role_features (id, role, features) VALUES (293, 3, 72);
INSERT INTO role_features (id, role, features) VALUES (294, 3, 73);
INSERT INTO role_features (id, role, features) VALUES (295, 3, 74);
INSERT INTO role_features (id, role, features) VALUES (296, 3, 75);
INSERT INTO role_features (id, role, features) VALUES (297, 3, 76);
INSERT INTO role_features (id, role, features) VALUES (298, 3, 77);
INSERT INTO role_features (id, role, features) VALUES (299, 3, 78);
INSERT INTO role_features (id, role, features) VALUES (300, 3, 79);
INSERT INTO role_features (id, role, features) VALUES (301, 3, 80);
INSERT INTO role_features (id, role, features) VALUES (302, 3, 81);
INSERT INTO role_features (id, role, features) VALUES (303, 3, 82);
INSERT INTO role_features (id, role, features) VALUES (304, 3, 83);
INSERT INTO role_features (id, role, features) VALUES (305, 3, 84);
INSERT INTO role_features (id, role, features) VALUES (306, 3, 85);
INSERT INTO role_features (id, role, features) VALUES (307, 3, 86);
INSERT INTO role_features (id, role, features) VALUES (308, 3, 87);
INSERT INTO role_features (id, role, features) VALUES (309, 3, 88);
INSERT INTO role_features (id, role, features) VALUES (310, 3, 89);
INSERT INTO role_features (id, role, features) VALUES (311, 3, 90);
INSERT INTO role_features (id, role, features) VALUES (312, 3, 91);
INSERT INTO role_features (id, role, features) VALUES (314, 3, 93);
INSERT INTO role_features (id, role, features) VALUES (313, 3, 92);
INSERT INTO role_features (id, role, features) VALUES (315, 3, 94);
INSERT INTO role_features (id, role, features) VALUES (317, 3, 96);
INSERT INTO role_features (id, role, features) VALUES (318, 3, 97);
INSERT INTO role_features (id, role, features) VALUES (320, 3, 99);
INSERT INTO role_features (id, role, features) VALUES (321, 3, 100);
INSERT INTO role_features (id, role, features) VALUES (322, 3, 101);
INSERT INTO role_features (id, role, features) VALUES (323, 3, 102);
INSERT INTO role_features (id, role, features) VALUES (324, 3, 103);
INSERT INTO role_features (id, role, features) VALUES (326, 3, 105);
INSERT INTO role_features (id, role, features) VALUES (327, 3, 106);
INSERT INTO role_features (id, role, features) VALUES (328, 3, 107);
INSERT INTO role_features (id, role, features) VALUES (329, 3, 108);
INSERT INTO role_features (id, role, features) VALUES (330, 3, 109);
INSERT INTO role_features (id, role, features) VALUES (331, 3, 110);
INSERT INTO role_features (id, role, features) VALUES (332, 3, 111);
INSERT INTO role_features (id, role, features) VALUES (333, 3, 112);
INSERT INTO role_features (id, role, features) VALUES (334, 3, 113);
INSERT INTO role_features (id, role, features) VALUES (335, 3, 114);
INSERT INTO role_features (id, role, features) VALUES (336, 3, 115);
INSERT INTO role_features (id, role, features) VALUES (337, 3, 116);
INSERT INTO role_features (id, role, features) VALUES (316, 3, 118);
INSERT INTO role_features (id, role, features) VALUES (247, 3, 119);
INSERT INTO role_features (id, role, features) VALUES (220, 3, 120);
INSERT INTO role_features (id, role, features) VALUES (224, 3, 121);
INSERT INTO role_features (id, role, features) VALUES (227, 3, 122);
INSERT INTO role_features (id, role, features) VALUES (230, 3, 123);
INSERT INTO role_features (id, role, features) VALUES (233, 3, 124);
INSERT INTO role_features (id, role, features) VALUES (239, 3, 125);
INSERT INTO role_features (id, role, features) VALUES (325, 3, 126);
INSERT INTO role_features (id, role, features) VALUES (319, 3, 127);
INSERT INTO role_features (id, role, features) VALUES (340, 4, 1);
INSERT INTO role_features (id, role, features) VALUES (341, 4, 2);
INSERT INTO role_features (id, role, features) VALUES (342, 4, 3);
INSERT INTO role_features (id, role, features) VALUES (343, 4, 4);
INSERT INTO role_features (id, role, features) VALUES (344, 4, 5);
INSERT INTO role_features (id, role, features) VALUES (345, 4, 6);
INSERT INTO role_features (id, role, features) VALUES (346, 4, 7);
INSERT INTO role_features (id, role, features) VALUES (347, 4, 8);
INSERT INTO role_features (id, role, features) VALUES (348, 4, 9);
INSERT INTO role_features (id, role, features) VALUES (349, 4, 10);
INSERT INTO role_features (id, role, features) VALUES (350, 4, 11);
INSERT INTO role_features (id, role, features) VALUES (351, 4, 12);
INSERT INTO role_features (id, role, features) VALUES (352, 4, 13);
INSERT INTO role_features (id, role, features) VALUES (353, 4, 14);
INSERT INTO role_features (id, role, features) VALUES (354, 4, 15);
INSERT INTO role_features (id, role, features) VALUES (355, 4, 16);
INSERT INTO role_features (id, role, features) VALUES (356, 4, 17);
INSERT INTO role_features (id, role, features) VALUES (357, 4, 18);
INSERT INTO role_features (id, role, features) VALUES (358, 4, 19);
INSERT INTO role_features (id, role, features) VALUES (359, 4, 20);
INSERT INTO role_features (id, role, features) VALUES (360, 4, 21);
INSERT INTO role_features (id, role, features) VALUES (361, 4, 22);
INSERT INTO role_features (id, role, features) VALUES (362, 4, 23);
INSERT INTO role_features (id, role, features) VALUES (363, 4, 24);
INSERT INTO role_features (id, role, features) VALUES (364, 4, 25);
INSERT INTO role_features (id, role, features) VALUES (365, 4, 119);
INSERT INTO role_features (id, role, features) VALUES (366, 4, 28);
INSERT INTO role_features (id, role, features) VALUES (367, 4, 29);
INSERT INTO role_features (id, role, features) VALUES (368, 4, 30);
INSERT INTO role_features (id, role, features) VALUES (369, 4, 31);
INSERT INTO role_features (id, role, features) VALUES (370, 4, 32);
INSERT INTO role_features (id, role, features) VALUES (371, 4, 33);
INSERT INTO role_features (id, role, features) VALUES (372, 4, 34);
INSERT INTO role_features (id, role, features) VALUES (373, 4, 35);
INSERT INTO role_features (id, role, features) VALUES (374, 4, 36);
INSERT INTO role_features (id, role, features) VALUES (375, 4, 37);
INSERT INTO role_features (id, role, features) VALUES (376, 4, 38);
INSERT INTO role_features (id, role, features) VALUES (377, 4, 39);
INSERT INTO role_features (id, role, features) VALUES (378, 4, 40);
INSERT INTO role_features (id, role, features) VALUES (379, 4, 41);
INSERT INTO role_features (id, role, features) VALUES (380, 4, 42);
INSERT INTO role_features (id, role, features) VALUES (381, 4, 43);
INSERT INTO role_features (id, role, features) VALUES (382, 4, 44);
INSERT INTO role_features (id, role, features) VALUES (383, 4, 45);
INSERT INTO role_features (id, role, features) VALUES (384, 4, 46);
INSERT INTO role_features (id, role, features) VALUES (385, 4, 47);
INSERT INTO role_features (id, role, features) VALUES (386, 4, 48);
INSERT INTO role_features (id, role, features) VALUES (387, 4, 49);
INSERT INTO role_features (id, role, features) VALUES (388, 4, 50);
INSERT INTO role_features (id, role, features) VALUES (389, 4, 51);
INSERT INTO role_features (id, role, features) VALUES (390, 4, 52);
INSERT INTO role_features (id, role, features) VALUES (391, 4, 53);
INSERT INTO role_features (id, role, features) VALUES (392, 4, 54);
INSERT INTO role_features (id, role, features) VALUES (393, 4, 55);
INSERT INTO role_features (id, role, features) VALUES (395, 4, 56);
INSERT INTO role_features (id, role, features) VALUES (396, 4, 57);
INSERT INTO role_features (id, role, features) VALUES (397, 4, 58);
INSERT INTO role_features (id, role, features) VALUES (398, 4, 59);
INSERT INTO role_features (id, role, features) VALUES (399, 4, 63);
INSERT INTO role_features (id, role, features) VALUES (400, 4, 64);
INSERT INTO role_features (id, role, features) VALUES (401, 4, 65);
INSERT INTO role_features (id, role, features) VALUES (402, 4, 66);
INSERT INTO role_features (id, role, features) VALUES (403, 4, 67);
INSERT INTO role_features (id, role, features) VALUES (404, 4, 68);
INSERT INTO role_features (id, role, features) VALUES (405, 4, 69);
INSERT INTO role_features (id, role, features) VALUES (406, 4, 70);
INSERT INTO role_features (id, role, features) VALUES (407, 4, 71);
INSERT INTO role_features (id, role, features) VALUES (408, 4, 72);
INSERT INTO role_features (id, role, features) VALUES (409, 4, 73);
INSERT INTO role_features (id, role, features) VALUES (410, 4, 74);
INSERT INTO role_features (id, role, features) VALUES (411, 4, 78);
INSERT INTO role_features (id, role, features) VALUES (522, 5, 86);
INSERT INTO role_features (id, role, features) VALUES (523, 5, 87);
INSERT INTO role_features (id, role, features) VALUES (524, 5, 88);
INSERT INTO role_features (id, role, features) VALUES (412, 4, 86);
INSERT INTO role_features (id, role, features) VALUES (413, 4, 87);
INSERT INTO role_features (id, role, features) VALUES (415, 4, 88);
INSERT INTO role_features (id, role, features) VALUES (416, 4, 89);
INSERT INTO role_features (id, role, features) VALUES (417, 4, 90);
INSERT INTO role_features (id, role, features) VALUES (419, 4, 91);
INSERT INTO role_features (id, role, features) VALUES (420, 4, 92);
INSERT INTO role_features (id, role, features) VALUES (421, 4, 93);
INSERT INTO role_features (id, role, features) VALUES (422, 4, 118);
INSERT INTO role_features (id, role, features) VALUES (423, 4, 96);
INSERT INTO role_features (id, role, features) VALUES (424, 4, 97);
INSERT INTO role_features (id, role, features) VALUES (425, 4, 98);
INSERT INTO role_features (id, role, features) VALUES (426, 4, 99);
INSERT INTO role_features (id, role, features) VALUES (427, 4, 100);
INSERT INTO role_features (id, role, features) VALUES (428, 4, 101);
INSERT INTO role_features (id, role, features) VALUES (431, 4, 102);
INSERT INTO role_features (id, role, features) VALUES (432, 4, 103);
INSERT INTO role_features (id, role, features) VALUES (433, 4, 104);
INSERT INTO role_features (id, role, features) VALUES (434, 4, 108);
INSERT INTO role_features (id, role, features) VALUES (435, 4, 109);
INSERT INTO role_features (id, role, features) VALUES (436, 4, 110);
INSERT INTO role_features (id, role, features) VALUES (437, 4, 111);
INSERT INTO role_features (id, role, features) VALUES (438, 4, 112);
INSERT INTO role_features (id, role, features) VALUES (439, 4, 113);
INSERT INTO role_features (id, role, features) VALUES (440, 4, 114);
INSERT INTO role_features (id, role, features) VALUES (441, 4, 115);
INSERT INTO role_features (id, role, features) VALUES (442, 4, 116);
INSERT INTO role_features (id, role, features) VALUES (443, 4, 128);
INSERT INTO role_features (id, role, features) VALUES (444, 5, 1);
INSERT INTO role_features (id, role, features) VALUES (445, 5, 2);
INSERT INTO role_features (id, role, features) VALUES (446, 5, 3);
INSERT INTO role_features (id, role, features) VALUES (447, 5, 4);
INSERT INTO role_features (id, role, features) VALUES (448, 5, 5);
INSERT INTO role_features (id, role, features) VALUES (449, 5, 6);
INSERT INTO role_features (id, role, features) VALUES (450, 5, 7);
INSERT INTO role_features (id, role, features) VALUES (451, 5, 8);
INSERT INTO role_features (id, role, features) VALUES (452, 5, 9);
INSERT INTO role_features (id, role, features) VALUES (453, 5, 10);
INSERT INTO role_features (id, role, features) VALUES (454, 5, 11);
INSERT INTO role_features (id, role, features) VALUES (455, 5, 12);
INSERT INTO role_features (id, role, features) VALUES (456, 5, 13);
INSERT INTO role_features (id, role, features) VALUES (457, 5, 14);
INSERT INTO role_features (id, role, features) VALUES (458, 5, 15);
INSERT INTO role_features (id, role, features) VALUES (459, 5, 16);
INSERT INTO role_features (id, role, features) VALUES (460, 5, 17);
INSERT INTO role_features (id, role, features) VALUES (461, 5, 18);
INSERT INTO role_features (id, role, features) VALUES (462, 5, 19);
INSERT INTO role_features (id, role, features) VALUES (463, 5, 20);
INSERT INTO role_features (id, role, features) VALUES (464, 5, 21);
INSERT INTO role_features (id, role, features) VALUES (465, 5, 22);
INSERT INTO role_features (id, role, features) VALUES (466, 5, 23);
INSERT INTO role_features (id, role, features) VALUES (468, 5, 25);
INSERT INTO role_features (id, role, features) VALUES (467, 5, 24);
INSERT INTO role_features (id, role, features) VALUES (470, 5, 27);
INSERT INTO role_features (id, role, features) VALUES (469, 5, 26);
INSERT INTO role_features (id, role, features) VALUES (471, 5, 28);
INSERT INTO role_features (id, role, features) VALUES (472, 5, 29);
INSERT INTO role_features (id, role, features) VALUES (473, 5, 30);
INSERT INTO role_features (id, role, features) VALUES (474, 5, 31);
INSERT INTO role_features (id, role, features) VALUES (475, 5, 32);
INSERT INTO role_features (id, role, features) VALUES (476, 5, 33);
INSERT INTO role_features (id, role, features) VALUES (477, 5, 34);
INSERT INTO role_features (id, role, features) VALUES (478, 5, 35);
INSERT INTO role_features (id, role, features) VALUES (479, 5, 36);
INSERT INTO role_features (id, role, features) VALUES (480, 5, 37);
INSERT INTO role_features (id, role, features) VALUES (481, 5, 38);
INSERT INTO role_features (id, role, features) VALUES (482, 5, 39);
INSERT INTO role_features (id, role, features) VALUES (483, 5, 40);
INSERT INTO role_features (id, role, features) VALUES (484, 5, 41);
INSERT INTO role_features (id, role, features) VALUES (485, 5, 42);
INSERT INTO role_features (id, role, features) VALUES (486, 5, 43);
INSERT INTO role_features (id, role, features) VALUES (487, 5, 44);
INSERT INTO role_features (id, role, features) VALUES (488, 5, 45);
INSERT INTO role_features (id, role, features) VALUES (489, 5, 46);
INSERT INTO role_features (id, role, features) VALUES (490, 5, 47);
INSERT INTO role_features (id, role, features) VALUES (491, 5, 48);
INSERT INTO role_features (id, role, features) VALUES (492, 5, 49);
INSERT INTO role_features (id, role, features) VALUES (493, 5, 50);
INSERT INTO role_features (id, role, features) VALUES (494, 5, 51);
INSERT INTO role_features (id, role, features) VALUES (495, 5, 52);
INSERT INTO role_features (id, role, features) VALUES (496, 5, 53);
INSERT INTO role_features (id, role, features) VALUES (497, 5, 54);
INSERT INTO role_features (id, role, features) VALUES (498, 5, 55);
INSERT INTO role_features (id, role, features) VALUES (499, 5, 56);
INSERT INTO role_features (id, role, features) VALUES (500, 5, 57);
INSERT INTO role_features (id, role, features) VALUES (501, 5, 58);
INSERT INTO role_features (id, role, features) VALUES (502, 5, 59);
INSERT INTO role_features (id, role, features) VALUES (503, 5, 60);
INSERT INTO role_features (id, role, features) VALUES (504, 5, 61);
INSERT INTO role_features (id, role, features) VALUES (505, 5, 62);
INSERT INTO role_features (id, role, features) VALUES (506, 5, 63);
INSERT INTO role_features (id, role, features) VALUES (507, 5, 64);
INSERT INTO role_features (id, role, features) VALUES (508, 5, 65);
INSERT INTO role_features (id, role, features) VALUES (509, 5, 66);
INSERT INTO role_features (id, role, features) VALUES (510, 5, 67);
INSERT INTO role_features (id, role, features) VALUES (511, 5, 68);
INSERT INTO role_features (id, role, features) VALUES (512, 5, 69);
INSERT INTO role_features (id, role, features) VALUES (513, 5, 70);
INSERT INTO role_features (id, role, features) VALUES (514, 5, 71);
INSERT INTO role_features (id, role, features) VALUES (515, 5, 72);
INSERT INTO role_features (id, role, features) VALUES (516, 5, 73);
INSERT INTO role_features (id, role, features) VALUES (517, 5, 74);
INSERT INTO role_features (id, role, features) VALUES (518, 5, 75);
INSERT INTO role_features (id, role, features) VALUES (519, 5, 76);
INSERT INTO role_features (id, role, features) VALUES (520, 5, 77);
INSERT INTO role_features (id, role, features) VALUES (521, 5, 78);
INSERT INTO role_features (id, role, features) VALUES (525, 5, 89);
INSERT INTO role_features (id, role, features) VALUES (526, 5, 90);
INSERT INTO role_features (id, role, features) VALUES (527, 5, 91);
INSERT INTO role_features (id, role, features) VALUES (528, 5, 92);
INSERT INTO role_features (id, role, features) VALUES (529, 5, 93);
INSERT INTO role_features (id, role, features) VALUES (530, 5, 118);
INSERT INTO role_features (id, role, features) VALUES (531, 5, 96);
INSERT INTO role_features (id, role, features) VALUES (532, 5, 97);
INSERT INTO role_features (id, role, features) VALUES (533, 5, 98);
INSERT INTO role_features (id, role, features) VALUES (534, 5, 99);
INSERT INTO role_features (id, role, features) VALUES (535, 5, 100);
INSERT INTO role_features (id, role, features) VALUES (536, 5, 101);
INSERT INTO role_features (id, role, features) VALUES (537, 5, 102);
INSERT INTO role_features (id, role, features) VALUES (538, 5, 103);
INSERT INTO role_features (id, role, features) VALUES (539, 5, 104);
INSERT INTO role_features (id, role, features) VALUES (540, 5, 105);
INSERT INTO role_features (id, role, features) VALUES (541, 5, 106);
INSERT INTO role_features (id, role, features) VALUES (542, 5, 107);
INSERT INTO role_features (id, role, features) VALUES (543, 5, 108);
INSERT INTO role_features (id, role, features) VALUES (544, 5, 109);
INSERT INTO role_features (id, role, features) VALUES (545, 5, 110);
INSERT INTO role_features (id, role, features) VALUES (546, 5, 111);
INSERT INTO role_features (id, role, features) VALUES (547, 5, 112);
INSERT INTO role_features (id, role, features) VALUES (548, 5, 113);
INSERT INTO role_features (id, role, features) VALUES (549, 5, 114);
INSERT INTO role_features (id, role, features) VALUES (550, 5, 115);
INSERT INTO role_features (id, role, features) VALUES (551, 5, 116);

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------acciones por pagina----------------------------------------------------------------------------------------------

ALTER SEQUENCE action_id_seq RESTART WITH 968;

INSERT INTO action (id, features, action) VALUES (2, 1, 'done');
INSERT INTO action (id, features, action) VALUES (3, 2, 'save');
INSERT INTO action (id, features, action) VALUES (4, 2, 'update');
INSERT INTO action (id, features, action) VALUES (5, 2, 'delete');
INSERT INTO action (id, features, action) VALUES (6, 2, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (7, 2, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (8, 2, 'Business_entity_change');
INSERT INTO action (id, features, action) VALUES (9, 2, 'Business_entity_select');
INSERT INTO action (id, features, action) VALUES (10, 2, 'Real_property_change');
INSERT INTO action (id, features, action) VALUES (11, 2, 'Real_property_select');
INSERT INTO action (id, features, action) VALUES (12, 3, 'search');
INSERT INTO action (id, features, action) VALUES (13, 3, 'reset');
INSERT INTO action (id, features, action) VALUES (14, 3, 'create');
INSERT INTO action (id, features, action) VALUES (15, 3, 'addressViewId');
INSERT INTO action (id, features, action) VALUES (16, 3, 'addressEdit');
INSERT INTO action (id, features, action) VALUES (17, 3, 'firstPage');
INSERT INTO action (id, features, action) VALUES (18, 3, 'previousPage');
INSERT INTO action (id, features, action) VALUES (19, 3, 'nextPage');
INSERT INTO action (id, features, action) VALUES (20, 3, 'lastPage');
INSERT INTO action (id, features, action) VALUES (21, 4, 'edit');
INSERT INTO action (id, features, action) VALUES (22, 4, 'done');
INSERT INTO action (id, features, action) VALUES (23, 4, 'approved');
INSERT INTO action (id, features, action) VALUES (24, 4, 'cancel');
INSERT INTO action (id, features, action) VALUES (25, 4, 'addRentableUnit');
INSERT INTO action (id, features, action) VALUES (26, 4, 'viewareaType');
INSERT INTO action (id, features, action) VALUES (27, 4, 'viewrealProperty');
INSERT INTO action (id, features, action) VALUES (28, 4, 'selectrentableUnit');
INSERT INTO action (id, features, action) VALUES (29, 5, 'save');
INSERT INTO action (id, features, action) VALUES (30, 5, 'update');
INSERT INTO action (id, features, action) VALUES (31, 5, 'delete');
INSERT INTO action (id, features, action) VALUES (32, 5, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (33, 5, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (34, 5, 'Area_type_change');
INSERT INTO action (id, features, action) VALUES (35, 5, 'Area_type_select');
INSERT INTO action (id, features, action) VALUES (36, 5, 'Floor_change');
INSERT INTO action (id, features, action) VALUES (37, 5, 'Floor_select');
INSERT INTO action (id, features, action) VALUES (38, 5, 'addrentableUnit');
INSERT INTO action (id, features, action) VALUES (39, 6, 'search');
INSERT INTO action (id, features, action) VALUES (40, 6, 'areaEdit');
INSERT INTO action (id, features, action) VALUES (41, 6, 'create');
INSERT INTO action (id, features, action) VALUES (42, 6, 'areaViewId');
INSERT INTO action (id, features, action) VALUES (43, 6, 'areaEdit');
INSERT INTO action (id, features, action) VALUES (44, 6, 'firstPage');
INSERT INTO action (id, features, action) VALUES (45, 6, 'previousPage');
INSERT INTO action (id, features, action) VALUES (46, 6, 'nextPage');
INSERT INTO action (id, features, action) VALUES (47, 6, 'lastPage');
INSERT INTO action (id, features, action) VALUES (48, 7, 'edit');
INSERT INTO action (id, features, action) VALUES (49, 7, 'done');
INSERT INTO action (id, features, action) VALUES (50, 7, 'approved');
INSERT INTO action (id, features, action) VALUES (51, 7, 'cancel');
INSERT INTO action (id, features, action) VALUES (52, 7, 'selectarea');
INSERT INTO action (id, features, action) VALUES (305, 41, 'done');
INSERT INTO action (id, features, action) VALUES (54, 8, 'update');
INSERT INTO action (id, features, action) VALUES (306, 41, 'viewrealProperty');
INSERT INTO action (id, features, action) VALUES (60, 9, 'reset');
INSERT INTO action (id, features, action) VALUES (77, 11, 'Real property_select');
INSERT INTO action (id, features, action) VALUES (148, 20, 'addContactLegalRepresentative');
INSERT INTO action (id, features, action) VALUES (53, 8, 'save');
INSERT INTO action (id, features, action) VALUES (98, 15, 'reset');
INSERT INTO action (id, features, action) VALUES (100, 15, 'billingResolutionViewId');
INSERT INTO action (id, features, action) VALUES (110, 16, 'vieweconomicActivity');
INSERT INTO action (id, features, action) VALUES (111, 16, 'selectbillingResolution');
INSERT INTO action (id, features, action) VALUES (121, 17, 'done');
INSERT INTO action (id, features, action) VALUES (125, 18, 'update');
INSERT INTO action (id, features, action) VALUES (149, 20, 'save');
INSERT INTO action (id, features, action) VALUES (134, 19, 'reset');
INSERT INTO action (id, features, action) VALUES (147, 19, 'lastPage');
INSERT INTO action (id, features, action) VALUES (150, 20, 'update');
INSERT INTO action (id, features, action) VALUES (151, 20, 'delete');
INSERT INTO action (id, features, action) VALUES (152, 20, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (153, 20, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (154, 20, 'addbillingResolution');
INSERT INTO action (id, features, action) VALUES (155, 20, 'addphoneNumber');
INSERT INTO action (id, features, action) VALUES (156, 20, 'addaddress');
INSERT INTO action (id, features, action) VALUES (157, 20, 'addproject');
INSERT INTO action (id, features, action) VALUES (158, 20, 'addbusinessLine');
INSERT INTO action (id, features, action) VALUES (159, 20, 'addbusinessEntityContact');
INSERT INTO action (id, features, action) VALUES (160, 20, 'addconsecutiveAccountsBilling');
INSERT INTO action (id, features, action) VALUES (161, 20, 'addconsecutiveCreditNotes');
INSERT INTO action (id, features, action) VALUES (163, 21, 'search');
INSERT INTO action (id, features, action) VALUES (164, 21, 'reset');
INSERT INTO action (id, features, action) VALUES (165, 21, 'create');
INSERT INTO action (id, features, action) VALUES (166, 21, 'businessEntityViewId');
INSERT INTO action (id, features, action) VALUES (167, 21, 'businessEntityEdit');
INSERT INTO action (id, features, action) VALUES (168, 21, 'firstPage');
INSERT INTO action (id, features, action) VALUES (55, 8, 'delete');
INSERT INTO action (id, features, action) VALUES (56, 8, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (57, 8, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (58, 8, 'addarea');
INSERT INTO action (id, features, action) VALUES (59, 9, 'search');
INSERT INTO action (id, features, action) VALUES (61, 9, 'create');
INSERT INTO action (id, features, action) VALUES (62, 9, 'areaTypeViewId');
INSERT INTO action (id, features, action) VALUES (64, 9, 'firstPage');
INSERT INTO action (id, features, action) VALUES (65, 9, 'previousPage');
INSERT INTO action (id, features, action) VALUES (66, 9, 'nextPage');
INSERT INTO action (id, features, action) VALUES (67, 9, 'lastPage');
INSERT INTO action (id, features, action) VALUES (68, 10, 'edit');
INSERT INTO action (id, features, action) VALUES (69, 10, 'done');
INSERT INTO action (id, features, action) VALUES (70, 10, 'viewrealProperty');
INSERT INTO action (id, features, action) VALUES (71, 11, 'save');
INSERT INTO action (id, features, action) VALUES (72, 11, 'update');
INSERT INTO action (id, features, action) VALUES (73, 11, 'delete');
INSERT INTO action (id, features, action) VALUES (74, 11, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (75, 11, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (78, 12, 'search');
INSERT INTO action (id, features, action) VALUES (79, 12, 'reset');
INSERT INTO action (id, features, action) VALUES (80, 12, 'create');
INSERT INTO action (id, features, action) VALUES (81, 12, 'avaluationViewId');
INSERT INTO action (id, features, action) VALUES (82, 12, 'avaluationEdit');
INSERT INTO action (id, features, action) VALUES (83, 12, 'firstPage');
INSERT INTO action (id, features, action) VALUES (84, 12, 'previousPage');
INSERT INTO action (id, features, action) VALUES (85, 12, 'nextPage');
INSERT INTO action (id, features, action) VALUES (86, 12, 'lastPage');
INSERT INTO action (id, features, action) VALUES (87, 13, 'edit');
INSERT INTO action (id, features, action) VALUES (88, 13, 'done');
INSERT INTO action (id, features, action) VALUES (89, 13, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (90, 14, 'save');
INSERT INTO action (id, features, action) VALUES (91, 14, 'update');
INSERT INTO action (id, features, action) VALUES (92, 14, 'delete');
INSERT INTO action (id, features, action) VALUES (94, 14, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (95, 14, 'Business_entity_change');
INSERT INTO action (id, features, action) VALUES (96, 14, 'Business_entity_select');
INSERT INTO action (id, features, action) VALUES (97, 15, 'search');
INSERT INTO action (id, features, action) VALUES (99, 15, 'create');
INSERT INTO action (id, features, action) VALUES (101, 15, 'billingResolutionEdit');
INSERT INTO action (id, features, action) VALUES (102, 15, 'firstPage');
INSERT INTO action (id, features, action) VALUES (103, 15, 'previousPage');
INSERT INTO action (id, features, action) VALUES (104, 15, 'nextPage');
INSERT INTO action (id, features, action) VALUES (105, 15, 'lastPage');
INSERT INTO action (id, features, action) VALUES (106, 16, 'edit');
INSERT INTO action (id, features, action) VALUES (107, 16, 'done');
INSERT INTO action (id, features, action) VALUES (109, 16, 'cancel');
INSERT INTO action (id, features, action) VALUES (112, 16, 'selectphoneNumber');
INSERT INTO action (id, features, action) VALUES (113, 16, 'selectaddress');
INSERT INTO action (id, features, action) VALUES (114, 16, 'selectproject');
INSERT INTO action (id, features, action) VALUES (115, 16, 'selectentityType');
INSERT INTO action (id, features, action) VALUES (116, 16, 'selectbusinessLine');
INSERT INTO action (id, features, action) VALUES (118, 16, 'selectconsecutiveAccountsBilling');
INSERT INTO action (id, features, action) VALUES (119, 16, 'selectconsecutiveCreditNotes');
INSERT INTO action (id, features, action) VALUES (120, 17, 'edit');
INSERT INTO action (id, features, action) VALUES (122, 17, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (123, 17, 'viewcontact');
INSERT INTO action (id, features, action) VALUES (124, 18, 'save');
INSERT INTO action (id, features, action) VALUES (126, 18, 'delete');
INSERT INTO action (id, features, action) VALUES (127, 18, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (128, 18, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (129, 18, 'Business entity_change');
INSERT INTO action (id, features, action) VALUES (130, 18, 'Business entity_select');
INSERT INTO action (id, features, action) VALUES (131, 18, 'Contact_change');
INSERT INTO action (id, features, action) VALUES (132, 18, 'Contact_select');
INSERT INTO action (id, features, action) VALUES (133, 19, 'search');
INSERT INTO action (id, features, action) VALUES (135, 19, 'create');
INSERT INTO action (id, features, action) VALUES (137, 19, 'businessEntityContactEdit');
INSERT INTO action (id, features, action) VALUES (138, 19, 'firstPage');
INSERT INTO action (id, features, action) VALUES (139, 19, 'previousPage');
INSERT INTO action (id, features, action) VALUES (140, 19, 'nextPage');
INSERT INTO action (id, features, action) VALUES (169, 21, 'previousPage');
INSERT INTO action (id, features, action) VALUES (170, 21, 'nextPage');
INSERT INTO action (id, features, action) VALUES (171, 21, 'lastPage');
INSERT INTO action (id, features, action) VALUES (172, 22, 'edit');
INSERT INTO action (id, features, action) VALUES (173, 22, 'done');
INSERT INTO action (id, features, action) VALUES (174, 22, 'approved');
INSERT INTO action (id, features, action) VALUES (175, 22, 'cancel');
INSERT INTO action (id, features, action) VALUES (176, 23, 'save');
INSERT INTO action (id, features, action) VALUES (177, 23, 'update');
INSERT INTO action (id, features, action) VALUES (178, 23, 'delete');
INSERT INTO action (id, features, action) VALUES (179, 23, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (180, 23, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (181, 23, 'addentityType');
INSERT INTO action (id, features, action) VALUES (182, 24, 'search');
INSERT INTO action (id, features, action) VALUES (183, 24, 'reset');
INSERT INTO action (id, features, action) VALUES (184, 24, 'create');
INSERT INTO action (id, features, action) VALUES (185, 24, 'businessEntityTypeViewId');
INSERT INTO action (id, features, action) VALUES (186, 24, 'businessEntityTypeEdit');
INSERT INTO action (id, features, action) VALUES (187, 24, 'firstpage');
INSERT INTO action (id, features, action) VALUES (188, 24, 'previousPage');
INSERT INTO action (id, features, action) VALUES (189, 24, 'nextPage');
INSERT INTO action (id, features, action) VALUES (190, 24, 'lastPage');
INSERT INTO action (id, features, action) VALUES (191, 25, 'edit');
INSERT INTO action (id, features, action) VALUES (192, 25, 'done');
INSERT INTO action (id, features, action) VALUES (193, 25, 'approved');
INSERT INTO action (id, features, action) VALUES (194, 25, 'cancel');
INSERT INTO action (id, features, action) VALUES (195, 25, 'addbusinessLine');
INSERT INTO action (id, features, action) VALUES (196, 25, 'save');
INSERT INTO action (id, features, action) VALUES (197, 25, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (198, 25, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (199, 25, 'viewbusinessLine');
INSERT INTO action (id, features, action) VALUES (200, 25, 'selectbusinessLine');
INSERT INTO action (id, features, action) VALUES (201, 25, 'selectproject');
INSERT INTO action (id, features, action) VALUES (202, 26, 'save');
INSERT INTO action (id, features, action) VALUES (203, 26, 'update');
INSERT INTO action (id, features, action) VALUES (204, 26, 'delete');
INSERT INTO action (id, features, action) VALUES (205, 26, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (206, 26, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (207, 26, 'Business_entity_change');
INSERT INTO action (id, features, action) VALUES (208, 26, 'Business_entity_select');
INSERT INTO action (id, features, action) VALUES (209, 26, 'editbusinessLine');
INSERT INTO action (id, features, action) VALUES (210, 26, 'addbusinessLine');
INSERT INTO action (id, features, action) VALUES (211, 26, 'addproject');
INSERT INTO action (id, features, action) VALUES (212, 26, 'save');
INSERT INTO action (id, features, action) VALUES (213, 26, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (214, 27, 'search');
INSERT INTO action (id, features, action) VALUES (215, 27, 'reset');
INSERT INTO action (id, features, action) VALUES (216, 27, 'create');
INSERT INTO action (id, features, action) VALUES (217, 27, 'businessLineViewId');
INSERT INTO action (id, features, action) VALUES (218, 27, 'businessLineEdit');
INSERT INTO action (id, features, action) VALUES (219, 27, 'firstPage');
INSERT INTO action (id, features, action) VALUES (220, 27, 'previousPage');
INSERT INTO action (id, features, action) VALUES (221, 27, 'nextPage');
INSERT INTO action (id, features, action) VALUES (222, 27, 'lastPage');
INSERT INTO action (id, features, action) VALUES (223, 28, 'edit');
INSERT INTO action (id, features, action) VALUES (224, 28, 'done');
INSERT INTO action (id, features, action) VALUES (225, 28, 'viewregion');
INSERT INTO action (id, features, action) VALUES (226, 28, 'selectaddress');
INSERT INTO action (id, features, action) VALUES (227, 29, 'save');
INSERT INTO action (id, features, action) VALUES (228, 29, 'update');
INSERT INTO action (id, features, action) VALUES (229, 29, 'delete');
INSERT INTO action (id, features, action) VALUES (230, 29, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (231, 29, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (232, 29, 'Region_change');
INSERT INTO action (id, features, action) VALUES (233, 29, 'Region_select');
INSERT INTO action (id, features, action) VALUES (234, 29, 'addaddress');
INSERT INTO action (id, features, action) VALUES (63, 9, 'areaTypeEdit');
INSERT INTO action (id, features, action) VALUES (76, 11, 'Real property_change');
INSERT INTO action (id, features, action) VALUES (93, 14, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (108, 16, 'approved');
INSERT INTO action (id, features, action) VALUES (117, 16, 'selectbusinessEntityContact');
INSERT INTO action (id, features, action) VALUES (136, 19, 'businessEntityContactViewId');
INSERT INTO action (id, features, action) VALUES (235, 30, 'search');
INSERT INTO action (id, features, action) VALUES (236, 30, 'reset');
INSERT INTO action (id, features, action) VALUES (237, 30, 'create');
INSERT INTO action (id, features, action) VALUES (238, 30, 'cityViewId');
INSERT INTO action (id, features, action) VALUES (239, 30, 'cityEdit');
INSERT INTO action (id, features, action) VALUES (240, 30, 'firstPage');
INSERT INTO action (id, features, action) VALUES (241, 30, 'previousPage');
INSERT INTO action (id, features, action) VALUES (242, 30, 'nextPage');
INSERT INTO action (id, features, action) VALUES (243, 30, 'lastPage');
INSERT INTO action (id, features, action) VALUES (244, 31, 'addConcept');
INSERT INTO action (id, features, action) VALUES (245, 31, 'edit');
INSERT INTO action (id, features, action) VALUES (246, 31, 'done');
INSERT INTO action (id, features, action) VALUES (247, 31, 'approved');
INSERT INTO action (id, features, action) VALUES (248, 31, 'viewprojectProperty');
INSERT INTO action (id, features, action) VALUES (249, 31, 'viewplugin');
INSERT INTO action (id, features, action) VALUES (250, 31, 'viewpaymentForm');
INSERT INTO action (id, features, action) VALUES (251, 32, 'addConcept');
INSERT INTO action (id, features, action) VALUES (252, 32, 'save');
INSERT INTO action (id, features, action) VALUES (253, 32, 'update');
INSERT INTO action (id, features, action) VALUES (254, 32, 'delete');
INSERT INTO action (id, features, action) VALUES (255, 32, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (256, 32, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (257, 32, 'Project_properties_change');
INSERT INTO action (id, features, action) VALUES (258, 32, 'Project_properties_select');
INSERT INTO action (id, features, action) VALUES (259, 32, 'Plugin_change');
INSERT INTO action (id, features, action) VALUES (260, 32, 'Plugin_select');
INSERT INTO action (id, features, action) VALUES (261, 33, 'search');
INSERT INTO action (id, features, action) VALUES (262, 33, 'create');
INSERT INTO action (id, features, action) VALUES (263, 33, 'conceptViewId');
INSERT INTO action (id, features, action) VALUES (264, 33, 'conceptEdit');
INSERT INTO action (id, features, action) VALUES (265, 33, 'firstPage');
INSERT INTO action (id, features, action) VALUES (266, 33, 'previousPage');
INSERT INTO action (id, features, action) VALUES (267, 33, 'nextPage');
INSERT INTO action (id, features, action) VALUES (268, 33, 'lastPage');
INSERT INTO action (id, features, action) VALUES (269, 34, 'confirm');
INSERT INTO action (id, features, action) VALUES (270, 35, 'edit');
INSERT INTO action (id, features, action) VALUES (271, 35, 'done');
INSERT INTO action (id, features, action) VALUES (272, 35, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (273, 36, 'save');
INSERT INTO action (id, features, action) VALUES (274, 36, 'update');
INSERT INTO action (id, features, action) VALUES (275, 36, 'delete');
INSERT INTO action (id, features, action) VALUES (276, 36, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (277, 36, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (278, 36, 'Business_entity_change');
INSERT INTO action (id, features, action) VALUES (279, 36, 'Business_entity_select');
INSERT INTO action (id, features, action) VALUES (281, 37, 'search');
INSERT INTO action (id, features, action) VALUES (282, 37, 'reset');
INSERT INTO action (id, features, action) VALUES (283, 37, 'create');
INSERT INTO action (id, features, action) VALUES (284, 37, 'consecutiveAccountsBillingViewId');
INSERT INTO action (id, features, action) VALUES (285, 37, 'consecutiveAccountsBillingEdit');
INSERT INTO action (id, features, action) VALUES (286, 37, 'firstPage');
INSERT INTO action (id, features, action) VALUES (287, 37, 'previousPage');
INSERT INTO action (id, features, action) VALUES (288, 37, 'nextPage');
INSERT INTO action (id, features, action) VALUES (289, 37, 'lastPage');
INSERT INTO action (id, features, action) VALUES (290, 38, 'edit');
INSERT INTO action (id, features, action) VALUES (291, 38, 'done');
INSERT INTO action (id, features, action) VALUES (292, 38, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (293, 39, 'save');
INSERT INTO action (id, features, action) VALUES (294, 39, 'update');
INSERT INTO action (id, features, action) VALUES (295, 39, 'delete');
INSERT INTO action (id, features, action) VALUES (296, 39, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (297, 39, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (298, 39, 'Business_entity_change');
INSERT INTO action (id, features, action) VALUES (299, 39, 'Business_entity_select');
INSERT INTO action (id, features, action) VALUES (300, 40, 'search');
INSERT INTO action (id, features, action) VALUES (301, 40, 'reset');
INSERT INTO action (id, features, action) VALUES (302, 40, 'create');
INSERT INTO action (id, features, action) VALUES (304, 41, 'edit');
INSERT INTO action (id, features, action) VALUES (307, 41, 'selectfloor');
INSERT INTO action (id, features, action) VALUES (308, 42, 'save');
INSERT INTO action (id, features, action) VALUES (309, 42, 'update');
INSERT INTO action (id, features, action) VALUES (310, 42, 'delete');
INSERT INTO action (id, features, action) VALUES (311, 42, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (312, 42, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (313, 42, 'Real_property_change');
INSERT INTO action (id, features, action) VALUES (314, 42, 'Real_property_select');
INSERT INTO action (id, features, action) VALUES (315, 42, 'addfloor');
INSERT INTO action (id, features, action) VALUES (316, 43, 'search');
INSERT INTO action (id, features, action) VALUES (317, 43, 'reset');
INSERT INTO action (id, features, action) VALUES (318, 43, 'create');
INSERT INTO action (id, features, action) VALUES (319, 43, 'constructionViewId');
INSERT INTO action (id, features, action) VALUES (320, 43, 'constructionEdit');
INSERT INTO action (id, features, action) VALUES (321, 43, 'firstPage');
INSERT INTO action (id, features, action) VALUES (322, 43, 'previousPage');
INSERT INTO action (id, features, action) VALUES (323, 43, 'nextPage');
INSERT INTO action (id, features, action) VALUES (324, 43, 'lastPage');
INSERT INTO action (id, features, action) VALUES (325, 44, 'edit');
INSERT INTO action (id, features, action) VALUES (326, 44, 'done');
INSERT INTO action (id, features, action) VALUES (327, 44, 'approved');
INSERT INTO action (id, features, action) VALUES (328, 44, 'cancel');
INSERT INTO action (id, features, action) VALUES (329, 44, 'viewcontactType');
INSERT INTO action (id, features, action) VALUES (330, 44, 'selectrealPropertyContact');
INSERT INTO action (id, features, action) VALUES (331, 44, 'selectbusinessEntityContact');
INSERT INTO action (id, features, action) VALUES (332, 44, 'selectphoneNumber');
INSERT INTO action (id, features, action) VALUES (333, 45, 'save');
INSERT INTO action (id, features, action) VALUES (334, 45, 'update');
INSERT INTO action (id, features, action) VALUES (335, 45, 'delete');
INSERT INTO action (id, features, action) VALUES (336, 45, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (337, 45, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (338, 45, 'Contact_type_change');
INSERT INTO action (id, features, action) VALUES (339, 45, 'Contact_type_select');
INSERT INTO action (id, features, action) VALUES (340, 45, 'addrealPropertyContact');
INSERT INTO action (id, features, action) VALUES (341, 45, 'addbusinessEntityContact');
INSERT INTO action (id, features, action) VALUES (342, 45, 'addphoneNumber');
INSERT INTO action (id, features, action) VALUES (343, 46, 'search');
INSERT INTO action (id, features, action) VALUES (344, 46, 'reset');
INSERT INTO action (id, features, action) VALUES (345, 46, 'create');
INSERT INTO action (id, features, action) VALUES (346, 46, 'contactViewId');
INSERT INTO action (id, features, action) VALUES (347, 46, 'contactEdit');
INSERT INTO action (id, features, action) VALUES (348, 46, 'firstPage');
INSERT INTO action (id, features, action) VALUES (349, 46, 'previousPage');
INSERT INTO action (id, features, action) VALUES (350, 46, 'nextPage');
INSERT INTO action (id, features, action) VALUES (351, 46, 'lastPage');
INSERT INTO action (id, features, action) VALUES (352, 47, 'edit');
INSERT INTO action (id, features, action) VALUES (353, 47, 'done');
INSERT INTO action (id, features, action) VALUES (354, 47, 'selectcontact');
INSERT INTO action (id, features, action) VALUES (355, 48, 'save');
INSERT INTO action (id, features, action) VALUES (356, 48, 'update');
INSERT INTO action (id, features, action) VALUES (357, 48, 'delete');
INSERT INTO action (id, features, action) VALUES (358, 48, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (359, 48, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (360, 48, 'addcontact');
INSERT INTO action (id, features, action) VALUES (361, 49, 'search');
INSERT INTO action (id, features, action) VALUES (362, 49, 'reset');
INSERT INTO action (id, features, action) VALUES (363, 49, 'create');
INSERT INTO action (id, features, action) VALUES (364, 49, 'contactTypeViewId');
INSERT INTO action (id, features, action) VALUES (365, 49, 'contactTypeEdit');
INSERT INTO action (id, features, action) VALUES (366, 49, 'firstPage');
INSERT INTO action (id, features, action) VALUES (367, 49, 'previousPage');
INSERT INTO action (id, features, action) VALUES (368, 49, 'nextPage');
INSERT INTO action (id, features, action) VALUES (369, 49, 'lastPage');
INSERT INTO action (id, features, action) VALUES (370, 50, 'edit');
INSERT INTO action (id, features, action) VALUES (371, 50, 'done');
INSERT INTO action (id, features, action) VALUES (372, 50, 'selectprojectProperty');
INSERT INTO action (id, features, action) VALUES (373, 51, 'save');
INSERT INTO action (id, features, action) VALUES (374, 51, 'update');
INSERT INTO action (id, features, action) VALUES (375, 51, 'delete');
INSERT INTO action (id, features, action) VALUES (376, 51, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (377, 51, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (378, 51, 'addprojectProperty');
INSERT INTO action (id, features, action) VALUES (379, 52, 'search');
INSERT INTO action (id, features, action) VALUES (380, 52, 'reset');
INSERT INTO action (id, features, action) VALUES (381, 52, 'create');
INSERT INTO action (id, features, action) VALUES (382, 52, 'contractTypeViewId');
INSERT INTO action (id, features, action) VALUES (383, 52, 'contractTypeEdit');
INSERT INTO action (id, features, action) VALUES (384, 52, 'firtsPage');
INSERT INTO action (id, features, action) VALUES (385, 52, 'previousPage');
INSERT INTO action (id, features, action) VALUES (386, 52, 'nextPage');
INSERT INTO action (id, features, action) VALUES (387, 52, 'lastPage');
INSERT INTO action (id, features, action) VALUES (388, 53, 'edit');
INSERT INTO action (id, features, action) VALUES (389, 53, 'done');
INSERT INTO action (id, features, action) VALUES (390, 53, 'viewrealProperty');
INSERT INTO action (id, features, action) VALUES (391, 53, 'selectrentableUnitContribution');
INSERT INTO action (id, features, action) VALUES (392, 54, 'save');
INSERT INTO action (id, features, action) VALUES (393, 54, 'update');
INSERT INTO action (id, features, action) VALUES (394, 54, 'delete');
INSERT INTO action (id, features, action) VALUES (395, 54, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (396, 54, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (397, 54, 'Real property_change');
INSERT INTO action (id, features, action) VALUES (398, 54, 'Real property_select');
INSERT INTO action (id, features, action) VALUES (399, 54, 'addrentableUnitContribution');
INSERT INTO action (id, features, action) VALUES (400, 55, 'search');
INSERT INTO action (id, features, action) VALUES (401, 55, 'reset');
INSERT INTO action (id, features, action) VALUES (402, 55, 'create');
INSERT INTO action (id, features, action) VALUES (403, 55, 'contributionModuleViewId');
INSERT INTO action (id, features, action) VALUES (404, 55, 'contributionModuleEdit');
INSERT INTO action (id, features, action) VALUES (405, 55, 'firstPage');
INSERT INTO action (id, features, action) VALUES (406, 55, 'previousPage');
INSERT INTO action (id, features, action) VALUES (407, 55, 'nextPage');
INSERT INTO action (id, features, action) VALUES (408, 55, 'lastPage');
INSERT INTO action (id, features, action) VALUES (409, 57, 'edit');
INSERT INTO action (id, features, action) VALUES (410, 57, 'done');
INSERT INTO action (id, features, action) VALUES (411, 57, 'selectregion');
INSERT INTO action (id, features, action) VALUES (412, 58, 'save');
INSERT INTO action (id, features, action) VALUES (413, 58, 'update');
INSERT INTO action (id, features, action) VALUES (414, 58, 'delete');
INSERT INTO action (id, features, action) VALUES (415, 58, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (416, 58, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (417, 58, 'addregion');
INSERT INTO action (id, features, action) VALUES (418, 59, 'search');
INSERT INTO action (id, features, action) VALUES (419, 59, 'reset');
INSERT INTO action (id, features, action) VALUES (420, 59, 'create');
INSERT INTO action (id, features, action) VALUES (421, 59, 'countryViewId');
INSERT INTO action (id, features, action) VALUES (422, 59, 'countryEdit');
INSERT INTO action (id, features, action) VALUES (423, 59, 'firstPage');
INSERT INTO action (id, features, action) VALUES (424, 59, 'previousPage');
INSERT INTO action (id, features, action) VALUES (425, 59, 'nextPage');
INSERT INTO action (id, features, action) VALUES (444, 59, 'lastPage');
INSERT INTO action (id, features, action) VALUES (445, 60, 'edit');
INSERT INTO action (id, features, action) VALUES (446, 60, 'done');
INSERT INTO action (id, features, action) VALUES (447, 60, 'approved');
INSERT INTO action (id, features, action) VALUES (448, 60, 'cancel');
INSERT INTO action (id, features, action) VALUES (449, 60, 'View');
INSERT INTO action (id, features, action) VALUES (450, 60, 'selectbusinessEntity');
INSERT INTO action (id, features, action) VALUES (451, 61, 'save');
INSERT INTO action (id, features, action) VALUES (452, 61, 'update');
INSERT INTO action (id, features, action) VALUES (453, 61, 'delete');
INSERT INTO action (id, features, action) VALUES (454, 61, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (455, 61, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (456, 61, 'Economic_sector_change');
INSERT INTO action (id, features, action) VALUES (457, 61, 'Economic_sector_select');
INSERT INTO action (id, features, action) VALUES (458, 61, 'addbusinessEntity');
INSERT INTO action (id, features, action) VALUES (459, 62, 'search');
INSERT INTO action (id, features, action) VALUES (460, 62, 'reset');
INSERT INTO action (id, features, action) VALUES (461, 62, 'create');
INSERT INTO action (id, features, action) VALUES (462, 62, 'economicActivityViewId');
INSERT INTO action (id, features, action) VALUES (463, 62, 'economicActivityEdit');
INSERT INTO action (id, features, action) VALUES (464, 62, 'firstPage');
INSERT INTO action (id, features, action) VALUES (465, 62, 'previousPage');
INSERT INTO action (id, features, action) VALUES (466, 62, 'nextPage');
INSERT INTO action (id, features, action) VALUES (467, 62, 'lastPage');
INSERT INTO action (id, features, action) VALUES (468, 62, 'edit');
INSERT INTO action (id, features, action) VALUES (469, 63, 'done');
INSERT INTO action (id, features, action) VALUES (470, 63, 'selecteconomicActivity');
INSERT INTO action (id, features, action) VALUES (471, 64, 'save');
INSERT INTO action (id, features, action) VALUES (472, 64, 'update');
INSERT INTO action (id, features, action) VALUES (473, 64, 'delete');
INSERT INTO action (id, features, action) VALUES (474, 64, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (475, 64, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (476, 64, 'addeconomicActivity');
INSERT INTO action (id, features, action) VALUES (477, 65, 'search');
INSERT INTO action (id, features, action) VALUES (478, 65, 'reset');
INSERT INTO action (id, features, action) VALUES (479, 65, 'create');
INSERT INTO action (id, features, action) VALUES (480, 65, 'economicSectorViewId');
INSERT INTO action (id, features, action) VALUES (481, 65, 'economicSectorEdit');
INSERT INTO action (id, features, action) VALUES (482, 65, 'firstPage');
INSERT INTO action (id, features, action) VALUES (483, 65, 'previousPage');
INSERT INTO action (id, features, action) VALUES (484, 65, 'nextPage');
INSERT INTO action (id, features, action) VALUES (485, 65, 'lastPage');
INSERT INTO action (id, features, action) VALUES (486, 66, 'edit');
INSERT INTO action (id, features, action) VALUES (487, 66, 'done');
INSERT INTO action (id, features, action) VALUES (488, 66, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (489, 66, 'viewbusinessEntityType');
INSERT INTO action (id, features, action) VALUES (491, 67, 'update');
INSERT INTO action (id, features, action) VALUES (490, 67, 'save');
INSERT INTO action (id, features, action) VALUES (492, 67, 'delete');
INSERT INTO action (id, features, action) VALUES (493, 67, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (494, 67, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (495, 67, 'Business entity_change');
INSERT INTO action (id, features, action) VALUES (496, 67, 'Business entity_select');
INSERT INTO action (id, features, action) VALUES (497, 67, 'Business entity type_change');
INSERT INTO action (id, features, action) VALUES (498, 67, 'Business entity type_select');
INSERT INTO action (id, features, action) VALUES (499, 68, 'search');
INSERT INTO action (id, features, action) VALUES (500, 68, 'reset');
INSERT INTO action (id, features, action) VALUES (501, 68, 'create');
INSERT INTO action (id, features, action) VALUES (502, 68, 'entityTypeViewId');
INSERT INTO action (id, features, action) VALUES (503, 68, 'entityTypeEdit');
INSERT INTO action (id, features, action) VALUES (504, 68, 'firstPage');
INSERT INTO action (id, features, action) VALUES (505, 68, 'previousPage');
INSERT INTO action (id, features, action) VALUES (506, 68, 'nextPage');
INSERT INTO action (id, features, action) VALUES (507, 68, 'lastPage');
INSERT INTO action (id, features, action) VALUES (508, 70, 'edit');
INSERT INTO action (id, features, action) VALUES (509, 70, 'done');
INSERT INTO action (id, features, action) VALUES (510, 70, 'approved');
INSERT INTO action (id, features, action) VALUES (511, 70, 'cancel');
INSERT INTO action (id, features, action) VALUES (512, 70, 'viewconstruction');
INSERT INTO action (id, features, action) VALUES (513, 71, 'save');
INSERT INTO action (id, features, action) VALUES (514, 71, 'update');
INSERT INTO action (id, features, action) VALUES (515, 71, 'delete');
INSERT INTO action (id, features, action) VALUES (516, 71, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (517, 71, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (518, 71, 'Construction_change');
INSERT INTO action (id, features, action) VALUES (520, 71, 'Construction_select');
INSERT INTO action (id, features, action) VALUES (521, 71, 'addarea');
INSERT INTO action (id, features, action) VALUES (522, 72, 'search');
INSERT INTO action (id, features, action) VALUES (523, 72, 'reset');
INSERT INTO action (id, features, action) VALUES (524, 72, 'floorEdit');
INSERT INTO action (id, features, action) VALUES (525, 72, 'create');
INSERT INTO action (id, features, action) VALUES (526, 72, 'floorViewId');
INSERT INTO action (id, features, action) VALUES (527, 72, 'floorEdit');
INSERT INTO action (id, features, action) VALUES (528, 72, 'firstPage');
INSERT INTO action (id, features, action) VALUES (529, 72, 'previousPage');
INSERT INTO action (id, features, action) VALUES (530, 72, 'nextPage');
INSERT INTO action (id, features, action) VALUES (531, 72, 'lastPage');
INSERT INTO action (id, features, action) VALUES (532, 74, 'search');
INSERT INTO action (id, features, action) VALUES (533, 74, 'buttonRequestReversion');
INSERT INTO action (id, features, action) VALUES (534, 75, 'edit');
INSERT INTO action (id, features, action) VALUES (535, 75, 'done');
INSERT INTO action (id, features, action) VALUES (536, 75, 'approved');
INSERT INTO action (id, features, action) VALUES (537, 75, 'cancel');
INSERT INTO action (id, features, action) VALUES (538, 75, 'selectrealProperty');
INSERT INTO action (id, features, action) VALUES (539, 76, 'save');
INSERT INTO action (id, features, action) VALUES (540, 76, 'update');
INSERT INTO action (id, features, action) VALUES (541, 76, 'delete');
INSERT INTO action (id, features, action) VALUES (542, 76, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (543, 76, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (544, 76, 'addrealProperty');
INSERT INTO action (id, features, action) VALUES (545, 77, 'search');
INSERT INTO action (id, features, action) VALUES (546, 77, 'reset');
INSERT INTO action (id, features, action) VALUES (547, 77, 'create');
INSERT INTO action (id, features, action) VALUES (548, 77, 'legalNatureOfPropertyViewId');
INSERT INTO action (id, features, action) VALUES (549, 77, 'legalNatureOfPropertyEdit');
INSERT INTO action (id, features, action) VALUES (550, 77, 'firstPage');
INSERT INTO action (id, features, action) VALUES (551, 77, 'previousPage');
INSERT INTO action (id, features, action) VALUES (552, 77, 'nextPage');
INSERT INTO action (id, features, action) VALUES (553, 77, 'lastPage');
INSERT INTO action (id, features, action) VALUES (554, 78, 'submit');
INSERT INTO action (id, features, action) VALUES (555, 79, 'edit');
INSERT INTO action (id, features, action) VALUES (556, 79, 'done');
INSERT INTO action (id, features, action) VALUES (557, 80, 'makerCheckerViewId');
INSERT INTO action (id, features, action) VALUES (558, 80, 'firstPage');
INSERT INTO action (id, features, action) VALUES (559, 80, 'previousPage');
INSERT INTO action (id, features, action) VALUES (560, 80, 'nextPage');
INSERT INTO action (id, features, action) VALUES (561, 80, 'lastPage');
INSERT INTO action (id, features, action) VALUES (562, 81, 'makerCheckerViewId');
INSERT INTO action (id, features, action) VALUES (563, 81, 'firstPage');
INSERT INTO action (id, features, action) VALUES (564, 81, 'previousPage');
INSERT INTO action (id, features, action) VALUES (565, 81, 'nextPage');
INSERT INTO action (id, features, action) VALUES (566, 81, 'lastPage');
INSERT INTO action (id, features, action) VALUES (567, 82, 'save');
INSERT INTO action (id, features, action) VALUES (568, 82, 'update');
INSERT INTO action (id, features, action) VALUES (569, 82, 'delete');
INSERT INTO action (id, features, action) VALUES (570, 82, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (571, 82, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (572, 83, 'reset');
INSERT INTO action (id, features, action) VALUES (573, 83, 'editConcept');
INSERT INTO action (id, features, action) VALUES (574, 83, 'closeModal');
INSERT INTO action (id, features, action) VALUES (575, 83, 'makerCheckerViewId');
INSERT INTO action (id, features, action) VALUES (576, 83, 'firstPage');
INSERT INTO action (id, features, action) VALUES (577, 83, 'previousPage');
INSERT INTO action (id, features, action) VALUES (578, 83, 'nextPage');
INSERT INTO action (id, features, action) VALUES (579, 83, 'lastPage');
INSERT INTO action (id, features, action) VALUES (580, 84, 'makerCheckerViewId');
INSERT INTO action (id, features, action) VALUES (581, 84, 'firstPage');
INSERT INTO action (id, features, action) VALUES (582, 84, 'previousPage');
INSERT INTO action (id, features, action) VALUES (583, 84, 'nextPage');
INSERT INTO action (id, features, action) VALUES (584, 84, 'lastPage');
INSERT INTO action (id, features, action) VALUES (585, 85, 'makerCheckerViewId');
INSERT INTO action (id, features, action) VALUES (586, 85, 'firstPage');
INSERT INTO action (id, features, action) VALUES (587, 85, 'previousPage');
INSERT INTO action (id, features, action) VALUES (588, 85, 'nextPage');
INSERT INTO action (id, features, action) VALUES (589, 85, 'lastPage');
INSERT INTO action (id, features, action) VALUES (590, 86, 'edit');
INSERT INTO action (id, features, action) VALUES (591, 86, 'done');
INSERT INTO action (id, features, action) VALUES (592, 87, 'edit');
INSERT INTO action (id, features, action) VALUES (593, 87, 'done');
INSERT INTO action (id, features, action) VALUES (594, 87, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (595, 87, 'viewcontact');
INSERT INTO action (id, features, action) VALUES (596, 88, 'save');
INSERT INTO action (id, features, action) VALUES (597, 88, 'update');
INSERT INTO action (id, features, action) VALUES (598, 88, 'delete');
INSERT INTO action (id, features, action) VALUES (599, 88, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (600, 88, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (601, 88, 'Business_entity_change');
INSERT INTO action (id, features, action) VALUES (602, 88, 'Business_entity_select');
INSERT INTO action (id, features, action) VALUES (603, 88, 'Contact_change');
INSERT INTO action (id, features, action) VALUES (604, 88, 'Contact_select');
INSERT INTO action (id, features, action) VALUES (605, 89, 'search');
INSERT INTO action (id, features, action) VALUES (606, 89, 'reset');
INSERT INTO action (id, features, action) VALUES (607, 89, 'create');
INSERT INTO action (id, features, action) VALUES (608, 89, 'phoneNumberViewId');
INSERT INTO action (id, features, action) VALUES (609, 89, 'phoneNumberEdit');
INSERT INTO action (id, features, action) VALUES (610, 89, 'firstPage');
INSERT INTO action (id, features, action) VALUES (611, 89, 'previousPage');
INSERT INTO action (id, features, action) VALUES (612, 89, 'nextPage');
INSERT INTO action (id, features, action) VALUES (613, 89, 'lastPage');
INSERT INTO action (id, features, action) VALUES (614, 90, 'edit');
INSERT INTO action (id, features, action) VALUES (615, 90, 'done');
INSERT INTO action (id, features, action) VALUES (616, 90, 'selectconcept');
INSERT INTO action (id, features, action) VALUES (617, 90, 'selectpluginContractType');
INSERT INTO action (id, features, action) VALUES (618, 91, 'save');
INSERT INTO action (id, features, action) VALUES (619, 91, 'update');
INSERT INTO action (id, features, action) VALUES (620, 91, 'delete');
INSERT INTO action (id, features, action) VALUES (621, 91, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (622, 91, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (623, 91, 'addconcept');
INSERT INTO action (id, features, action) VALUES (624, 91, 'addpluginContractType');
INSERT INTO action (id, features, action) VALUES (625, 92, 'search');
INSERT INTO action (id, features, action) VALUES (626, 92, 'reset');
INSERT INTO action (id, features, action) VALUES (627, 92, 'create');
INSERT INTO action (id, features, action) VALUES (628, 92, 'pluginViewId');
INSERT INTO action (id, features, action) VALUES (629, 92, 'pluginEdit');
INSERT INTO action (id, features, action) VALUES (630, 92, 'firstPage');
INSERT INTO action (id, features, action) VALUES (631, 92, 'previousPage');
INSERT INTO action (id, features, action) VALUES (632, 92, 'nextPage');
INSERT INTO action (id, features, action) VALUES (633, 92, 'lastPage');
INSERT INTO action (id, features, action) VALUES (634, 93, 'edit');
INSERT INTO action (id, features, action) VALUES (635, 93, 'done');
INSERT INTO action (id, features, action) VALUES (636, 93, 'approved');
INSERT INTO action (id, features, action) VALUES (637, 93, 'cancel');
INSERT INTO action (id, features, action) VALUES (638, 93, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (639, 93, 'viewbusinessLine');
INSERT INTO action (id, features, action) VALUES (640, 93, 'selectprojectProperty');
INSERT INTO action (id, features, action) VALUES (641, 94, 'save');
INSERT INTO action (id, features, action) VALUES (642, 94, 'update');
INSERT INTO action (id, features, action) VALUES (643, 94, 'delete');
INSERT INTO action (id, features, action) VALUES (644, 94, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (645, 94, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (646, 94, 'usiness_entity_change');
INSERT INTO action (id, features, action) VALUES (647, 94, 'usiness_entity_select');
INSERT INTO action (id, features, action) VALUES (648, 94, 'Business_line_change');
INSERT INTO action (id, features, action) VALUES (649, 94, 'Business_line_select');
INSERT INTO action (id, features, action) VALUES (650, 94, 'addprojectProperty');
INSERT INTO action (id, features, action) VALUES (651, 95, 'search');
INSERT INTO action (id, features, action) VALUES (652, 95, 'openModalPanelInvoice');
INSERT INTO action (id, features, action) VALUES (653, 95, 'openModalPanelNews');
INSERT INTO action (id, features, action) VALUES (654, 95, 'openModalPanelProjectClosure');
INSERT INTO action (id, features, action) VALUES (655, 95, 'openModalPanelInvoiceConcept');
INSERT INTO action (id, features, action) VALUES (656, 95, 'closeModalPanelInvoice');
INSERT INTO action (id, features, action) VALUES (657, 95, 'okModalPanelInvoice');
INSERT INTO action (id, features, action) VALUES (658, 95, 'closeModalPanelInvoiceConcept');
INSERT INTO action (id, features, action) VALUES (659, 95, 'doApprovePreliquidationButton');
INSERT INTO action (id, features, action) VALUES (660, 95, 'openViewChangeModalPanel');
INSERT INTO action (id, features, action) VALUES (661, 95, 'closeModalPanelNews');
INSERT INTO action (id, features, action) VALUES (662, 95, 'closeviewChangeModalPanel');
INSERT INTO action (id, features, action) VALUES (663, 95, 'create');
INSERT INTO action (id, features, action) VALUES (664, 95, 'projectViewId');
INSERT INTO action (id, features, action) VALUES (665, 95, 'projectEdit');
INSERT INTO action (id, features, action) VALUES (666, 95, 'linklink');
INSERT INTO action (id, features, action) VALUES (667, 95, 'firstPage');
INSERT INTO action (id, features, action) VALUES (668, 95, 'previousPage');
INSERT INTO action (id, features, action) VALUES (669, 95, 'nextPage');
INSERT INTO action (id, features, action) VALUES (670, 95, 'lastPage');
INSERT INTO action (id, features, action) VALUES (671, 96, 'pdf');
INSERT INTO action (id, features, action) VALUES (672, 96, 'edit');
INSERT INTO action (id, features, action) VALUES (673, 96, 'done');
INSERT INTO action (id, features, action) VALUES (674, 96, 'approved');
INSERT INTO action (id, features, action) VALUES (675, 96, 'cancel');
INSERT INTO action (id, features, action) VALUES (676, 96, 'addConcept');
INSERT INTO action (id, features, action) VALUES (677, 96, 'cancelConcept');
INSERT INTO action (id, features, action) VALUES (678, 96, 'linkPDF');
INSERT INTO action (id, features, action) VALUES (679, 96, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (680, 96, 'viewproject');
INSERT INTO action (id, features, action) VALUES (681, 96, 'selectconcept');
INSERT INTO action (id, features, action) VALUES (682, 97, 'addConcept');
INSERT INTO action (id, features, action) VALUES (683, 97, 'save');
INSERT INTO action (id, features, action) VALUES (684, 97, 'update');
INSERT INTO action (id, features, action) VALUES (686, 97, 'delete');
INSERT INTO action (id, features, action) VALUES (687, 97, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (688, 97, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (689, 97, 'Billed_change');
INSERT INTO action (id, features, action) VALUES (690, 97, 'Billed_select');
INSERT INTO action (id, features, action) VALUES (691, 97, 'Projects_change');
INSERT INTO action (id, features, action) VALUES (692, 97, 'Projects_select');
INSERT INTO action (id, features, action) VALUES (693, 97, 'Real_property_change');
INSERT INTO action (id, features, action) VALUES (694, 97, 'Real_property_select');
INSERT INTO action (id, features, action) VALUES (695, 97, 'viewConcept');
INSERT INTO action (id, features, action) VALUES (696, 97, 'saveGrouping');
INSERT INTO action (id, features, action) VALUES (697, 97, 'closeAgroupingConfirmation');
INSERT INTO action (id, features, action) VALUES (698, 97, 'addConcept');
INSERT INTO action (id, features, action) VALUES (699, 98, 'search');
INSERT INTO action (id, features, action) VALUES (700, 98, 'reset');
INSERT INTO action (id, features, action) VALUES (701, 98, 'create');
INSERT INTO action (id, features, action) VALUES (702, 98, 'projectPropertyViewId');
INSERT INTO action (id, features, action) VALUES (703, 98, 'projectPropertyEdit');
INSERT INTO action (id, features, action) VALUES (704, 98, 'firstPage');
INSERT INTO action (id, features, action) VALUES (705, 98, 'previousPage');
INSERT INTO action (id, features, action) VALUES (706, 98, 'nextPage');
INSERT INTO action (id, features, action) VALUES (707, 98, 'lastPage');
INSERT INTO action (id, features, action) VALUES (728, 99, 'editfloor');
INSERT INTO action (id, features, action) VALUES (729, 99, 'closeModalPanelFloorList');
INSERT INTO action (id, features, action) VALUES (730, 99, 'closeModalPanelAreaList');
INSERT INTO action (id, features, action) VALUES (731, 99, 'editrentableunit');
INSERT INTO action (id, features, action) VALUES (732, 99, 'closeModalPanelRentableUnitList');
INSERT INTO action (id, features, action) VALUES (733, 99, 'selectaddress');
INSERT INTO action (id, features, action) VALUES (734, 99, 'selectrealPropertyContact');
INSERT INTO action (id, features, action) VALUES (735, 99, 'selectprojectProperty');
INSERT INTO action (id, features, action) VALUES (782, 104, 'search');
INSERT INTO action (id, features, action) VALUES (783, 104, 'reset');
INSERT INTO action (id, features, action) VALUES (784, 104, 'create');
INSERT INTO action (id, features, action) VALUES (785, 104, 'realPropertyViewId');
INSERT INTO action (id, features, action) VALUES (786, 104, 'realPropertyEdit');
INSERT INTO action (id, features, action) VALUES (787, 104, 'firstPage');
INSERT INTO action (id, features, action) VALUES (788, 104, 'previousPage');
INSERT INTO action (id, features, action) VALUES (789, 104, 'nextPage');
INSERT INTO action (id, features, action) VALUES (790, 104, 'lastPage');
INSERT INTO action (id, features, action) VALUES (736, 100, 'edit');
INSERT INTO action (id, features, action) VALUES (737, 100, 'done');
INSERT INTO action (id, features, action) VALUES (738, 100, 'viewcontact');
INSERT INTO action (id, features, action) VALUES (739, 100, 'viewrealProperty');
INSERT INTO action (id, features, action) VALUES (791, 105, 'edit');
INSERT INTO action (id, features, action) VALUES (792, 105, 'done');
INSERT INTO action (id, features, action) VALUES (793, 105, 'approved');
INSERT INTO action (id, features, action) VALUES (794, 105, 'cancel');
INSERT INTO action (id, features, action) VALUES (795, 105, 'selectrealProperty');
INSERT INTO action (id, features, action) VALUES (796, 106, 'save');
INSERT INTO action (id, features, action) VALUES (740, 101, 'save');
INSERT INTO action (id, features, action) VALUES (741, 101, 'update');
INSERT INTO action (id, features, action) VALUES (742, 101, 'delete');
INSERT INTO action (id, features, action) VALUES (743, 101, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (744, 101, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (745, 101, 'Contact_change');
INSERT INTO action (id, features, action) VALUES (746, 101, 'Contact_select');
INSERT INTO action (id, features, action) VALUES (797, 106, 'update');
INSERT INTO action (id, features, action) VALUES (798, 106, 'delete');
INSERT INTO action (id, features, action) VALUES (799, 106, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (801, 106, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (803, 106, 'addrealProperty');
INSERT INTO action (id, features, action) VALUES (804, 107, 'search');
INSERT INTO action (id, features, action) VALUES (805, 107, 'reset');
INSERT INTO action (id, features, action) VALUES (806, 107, 'create');
INSERT INTO action (id, features, action) VALUES (807, 107, 'realPropertyUseTypeViewId');
INSERT INTO action (id, features, action) VALUES (808, 107, 'realPropertyUseTypeEdit');
INSERT INTO action (id, features, action) VALUES (809, 107, 'firstPage');
INSERT INTO action (id, features, action) VALUES (810, 107, 'previousPage');
INSERT INTO action (id, features, action) VALUES (811, 107, 'nextPage');
INSERT INTO action (id, features, action) VALUES (812, 107, 'lastPage');
INSERT INTO action (id, features, action) VALUES (813, 108, 'edit');
INSERT INTO action (id, features, action) VALUES (708, 99, 'edit');
INSERT INTO action (id, features, action) VALUES (709, 99, 'done');
INSERT INTO action (id, features, action) VALUES (710, 99, 'approved');
INSERT INTO action (id, features, action) VALUES (711, 99, 'cancel');
INSERT INTO action (id, features, action) VALUES (712, 99, 'editconstruction');
INSERT INTO action (id, features, action) VALUES (713, 99, 'addconstruction');
INSERT INTO action (id, features, action) VALUES (714, 99, 'createfloor');
INSERT INTO action (id, features, action) VALUES (715, 99, 'floorlist');
INSERT INTO action (id, features, action) VALUES (716, 99, 'search');
INSERT INTO action (id, features, action) VALUES (717, 99, 'createarea');
INSERT INTO action (id, features, action) VALUES (720, 99, 'createRentableUnit');
INSERT INTO action (id, features, action) VALUES (721, 99, 'rentableUnitList');
INSERT INTO action (id, features, action) VALUES (722, 99, 'save');
INSERT INTO action (id, features, action) VALUES (723, 99, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (725, 99, 'update');
INSERT INTO action (id, features, action) VALUES (727, 99, 'button');
INSERT INTO action (id, features, action) VALUES (747, 101, 'Real property_change');
INSERT INTO action (id, features, action) VALUES (748, 101, 'Real property_select');
INSERT INTO action (id, features, action) VALUES (749, 102, 'search');
INSERT INTO action (id, features, action) VALUES (750, 102, 'reset');
INSERT INTO action (id, features, action) VALUES (751, 102, 'create');
INSERT INTO action (id, features, action) VALUES (752, 102, 'realPropertyContactViewId');
INSERT INTO action (id, features, action) VALUES (753, 102, 'realPropertyContactEdit');
INSERT INTO action (id, features, action) VALUES (754, 102, 'firstPage');
INSERT INTO action (id, features, action) VALUES (755, 102, 'previousPage');
INSERT INTO action (id, features, action) VALUES (756, 102, 'nextPage');
INSERT INTO action (id, features, action) VALUES (757, 102, 'lastPage');
INSERT INTO action (id, features, action) VALUES (758, 103, 'save');
INSERT INTO action (id, features, action) VALUES (759, 103, 'update');
INSERT INTO action (id, features, action) VALUES (760, 103, 'delete');
INSERT INTO action (id, features, action) VALUES (761, 103, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (762, 103, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (764, 103, 'addaddress');
INSERT INTO action (id, features, action) VALUES (765, 103, 'addrealPropertyContact');
INSERT INTO action (id, features, action) VALUES (766, 103, 'addprojectProperty');
INSERT INTO action (id, features, action) VALUES (767, 103, 'editconstruction');
INSERT INTO action (id, features, action) VALUES (768, 103, 'addconstruction');
INSERT INTO action (id, features, action) VALUES (769, 103, 'createfloor');
INSERT INTO action (id, features, action) VALUES (770, 103, 'floorlist');
INSERT INTO action (id, features, action) VALUES (771, 103, 'search');
INSERT INTO action (id, features, action) VALUES (772, 103, 'createarea');
INSERT INTO action (id, features, action) VALUES (774, 103, 'createRentableUnit');
INSERT INTO action (id, features, action) VALUES (775, 103, 'rentableUnitList');
INSERT INTO action (id, features, action) VALUES (777, 103, 'editfloor');
INSERT INTO action (id, features, action) VALUES (778, 103, 'closeModalPanelFloorList');
INSERT INTO action (id, features, action) VALUES (779, 103, 'closeModalPanelAreaList');
INSERT INTO action (id, features, action) VALUES (780, 103, 'editrentableunit');
INSERT INTO action (id, features, action) VALUES (781, 103, 'closeModalPanelRentableUnitList');
INSERT INTO action (id, features, action) VALUES (814, 108, 'done');
INSERT INTO action (id, features, action) VALUES (815, 108, 'viewcountry');
INSERT INTO action (id, features, action) VALUES (816, 108, 'selectcity');
INSERT INTO action (id, features, action) VALUES (817, 109, 'save');
INSERT INTO action (id, features, action) VALUES (818, 109, 'update');
INSERT INTO action (id, features, action) VALUES (819, 109, 'delete');
INSERT INTO action (id, features, action) VALUES (820, 109, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (821, 109, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (822, 109, 'Country_change');
INSERT INTO action (id, features, action) VALUES (823, 109, 'Country_select');
INSERT INTO action (id, features, action) VALUES (824, 109, 'addcity');
INSERT INTO action (id, features, action) VALUES (825, 110, 'search');
INSERT INTO action (id, features, action) VALUES (826, 110, 'reset');
INSERT INTO action (id, features, action) VALUES (827, 110, 'create');
INSERT INTO action (id, features, action) VALUES (828, 110, 'regionViewId');
INSERT INTO action (id, features, action) VALUES (829, 110, 'regionEdit');
INSERT INTO action (id, features, action) VALUES (830, 110, 'firstPage');
INSERT INTO action (id, features, action) VALUES (831, 110, 'previousPage');
INSERT INTO action (id, features, action) VALUES (832, 110, 'nextPage');
INSERT INTO action (id, features, action) VALUES (833, 110, 'lastPage');
INSERT INTO action (id, features, action) VALUES (834, 111, 'edit');
INSERT INTO action (id, features, action) VALUES (835, 111, 'done');
INSERT INTO action (id, features, action) VALUES (836, 111, 'approved');
INSERT INTO action (id, features, action) VALUES (837, 111, 'cancel');
INSERT INTO action (id, features, action) VALUES (838, 111, 'viewrentableUnit');
INSERT INTO action (id, features, action) VALUES (839, 111, 'selectrentableUnit');
INSERT INTO action (id, features, action) VALUES (840, 112, 'edit');
INSERT INTO action (id, features, action) VALUES (841, 112, 'done');
INSERT INTO action (id, features, action) VALUES (842, 112, 'viewrentableUnit');
INSERT INTO action (id, features, action) VALUES (843, 112, 'viewcontributionModule');
INSERT INTO action (id, features, action) VALUES (844, 113, 'save');
INSERT INTO action (id, features, action) VALUES (845, 113, 'update');
INSERT INTO action (id, features, action) VALUES (846, 113, 'delete');
INSERT INTO action (id, features, action) VALUES (848, 113, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (847, 113, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (849, 113, 'Rentable unit_change');
INSERT INTO action (id, features, action) VALUES (850, 113, 'Rentable unit_select');
INSERT INTO action (id, features, action) VALUES (851, 113, 'Contribution module_change');
INSERT INTO action (id, features, action) VALUES (852, 113, 'Contribution module_select');
INSERT INTO action (id, features, action) VALUES (853, 114, 'search');
INSERT INTO action (id, features, action) VALUES (854, 114, 'reset');
INSERT INTO action (id, features, action) VALUES (855, 114, 'create');
INSERT INTO action (id, features, action) VALUES (856, 114, 'rentableUnitContributionViewId');
INSERT INTO action (id, features, action) VALUES (857, 114, 'rentableUnitContributionEdit');
INSERT INTO action (id, features, action) VALUES (858, 114, 'firstPage');
INSERT INTO action (id, features, action) VALUES (859, 114, 'previousPage');
INSERT INTO action (id, features, action) VALUES (860, 114, 'nextPage');
INSERT INTO action (id, features, action) VALUES (861, 114, 'lastPage');
INSERT INTO action (id, features, action) VALUES (862, 115, 'save');
INSERT INTO action (id, features, action) VALUES (863, 115, 'update');
INSERT INTO action (id, features, action) VALUES (864, 115, 'delete');
INSERT INTO action (id, features, action) VALUES (865, 115, 'cancelEdit');
INSERT INTO action (id, features, action) VALUES (866, 115, 'cancelAdd');
INSERT INTO action (id, features, action) VALUES (867, 115, 'addrentableUnit');
INSERT INTO action (id, features, action) VALUES (868, 116, 'search');
INSERT INTO action (id, features, action) VALUES (869, 116, 'reset');
INSERT INTO action (id, features, action) VALUES (870, 116, 'create');
INSERT INTO action (id, features, action) VALUES (871, 116, 'rentableUnitViewId');
INSERT INTO action (id, features, action) VALUES (872, 116, 'rentableUnitEdit');
INSERT INTO action (id, features, action) VALUES (873, 116, 'firstPage');
INSERT INTO action (id, features, action) VALUES (874, 116, 'previousPage');
INSERT INTO action (id, features, action) VALUES (875, 116, 'nextPage');
INSERT INTO action (id, features, action) VALUES (876, 116, 'lastPage');
INSERT INTO action (id, features, action) VALUES (877, 1, 'viewbusinessEntity');
INSERT INTO action (id, features, action) VALUES (878, 1, 'viewcity');
INSERT INTO action (id, features, action) VALUES (879, 1, 'viewrealProperty');
INSERT INTO action (id, features, action) VALUES (1, 1, 'edit');
INSERT INTO action (id, features, action) VALUES (880, 118, 'search');
INSERT INTO action (id, features, action) VALUES (881, 118, 'reset');
INSERT INTO action (id, features, action) VALUES (883, 95, 'Reversion');
INSERT INTO action (id, features, action) VALUES (882, 95, 'reset');
INSERT INTO action (id, features, action) VALUES (884, 95, 'openModalPanelGroup');
INSERT INTO action (id, features, action) VALUES (885, 118, 'firstPage');
INSERT INTO action (id, features, action) VALUES (886, 118, 'previousPage');
INSERT INTO action (id, features, action) VALUES (887, 118, 'nextPage');
INSERT INTO action (id, features, action) VALUES (888, 118, 'lastPage');
INSERT INTO action (id, features, action) VALUES (889, 119, 'search');
INSERT INTO action (id, features, action) VALUES (890, 119, 'reset');
INSERT INTO action (id, features, action) VALUES (891, 119, 'businessLineViewId');
INSERT INTO action (id, features, action) VALUES (892, 119, 'firstPage');
INSERT INTO action (id, features, action) VALUES (893, 119, 'previousPage');
INSERT INTO action (id, features, action) VALUES (894, 119, 'nextPage');
INSERT INTO action (id, features, action) VALUES (895, 119, 'lastPage');
INSERT INTO action (id, features, action) VALUES (896, 120, 'search');
INSERT INTO action (id, features, action) VALUES (897, 120, 'reset');
INSERT INTO action (id, features, action) VALUES (898, 120, 'addressViewId');
INSERT INTO action (id, features, action) VALUES (899, 120, 'firstPage');
INSERT INTO action (id, features, action) VALUES (900, 120, 'previousPage');
INSERT INTO action (id, features, action) VALUES (901, 120, 'nextPage');
INSERT INTO action (id, features, action) VALUES (902, 120, 'lastPage');
INSERT INTO action (id, features, action) VALUES (910, 121, 'search');
INSERT INTO action (id, features, action) VALUES (911, 121, 'reset');
INSERT INTO action (id, features, action) VALUES (912, 121, 'areaViewId');
INSERT INTO action (id, features, action) VALUES (913, 121, 'firstPage');
INSERT INTO action (id, features, action) VALUES (914, 121, 'previousPage');
INSERT INTO action (id, features, action) VALUES (915, 121, 'nextPage');
INSERT INTO action (id, features, action) VALUES (916, 121, 'lastPage');
INSERT INTO action (id, features, action) VALUES (917, 6, 'reset');
INSERT INTO action (id, features, action) VALUES (918, 122, 'search');
INSERT INTO action (id, features, action) VALUES (919, 122, 'reset');
INSERT INTO action (id, features, action) VALUES (920, 122, 'firstPage');
INSERT INTO action (id, features, action) VALUES (921, 122, 'previousPage');
INSERT INTO action (id, features, action) VALUES (922, 122, 'nextPage');
INSERT INTO action (id, features, action) VALUES (923, 122, 'lastPage');
INSERT INTO action (id, features, action) VALUES (924, 123, 'search');
INSERT INTO action (id, features, action) VALUES (925, 123, 'reset');
INSERT INTO action (id, features, action) VALUES (926, 123, 'avaluationViewId');
INSERT INTO action (id, features, action) VALUES (927, 123, 'firstPage');
INSERT INTO action (id, features, action) VALUES (928, 123, 'previousPage');
INSERT INTO action (id, features, action) VALUES (929, 123, 'nextPage');
INSERT INTO action (id, features, action) VALUES (930, 123, 'lastPage');
INSERT INTO action (id, features, action) VALUES (931, 124, 'search');
INSERT INTO action (id, features, action) VALUES (932, 124, 'reset');
INSERT INTO action (id, features, action) VALUES (933, 124, 'billingResolutionViewId');
INSERT INTO action (id, features, action) VALUES (934, 124, 'firstPage');
INSERT INTO action (id, features, action) VALUES (935, 124, 'previousPage');
INSERT INTO action (id, features, action) VALUES (936, 124, 'nextPage');
INSERT INTO action (id, features, action) VALUES (937, 124, 'lastPage');
INSERT INTO action (id, features, action) VALUES (938, 125, 'businessEntityContactViewId');
INSERT INTO action (id, features, action) VALUES (939, 125, 'firstPage');
INSERT INTO action (id, features, action) VALUES (940, 125, 'previousPage');
INSERT INTO action (id, features, action) VALUES (941, 125, 'nextPage');
INSERT INTO action (id, features, action) VALUES (942, 125, 'lastPage');
INSERT INTO action (id, features, action) VALUES (943, 126, 'search');
INSERT INTO action (id, features, action) VALUES (944, 126, 'reset');
INSERT INTO action (id, features, action) VALUES (945, 126, 'realPropertyViewId');
INSERT INTO action (id, features, action) VALUES (946, 126, 'firstPage');
INSERT INTO action (id, features, action) VALUES (947, 126, 'previousPage');
INSERT INTO action (id, features, action) VALUES (948, 126, 'nextPage');
INSERT INTO action (id, features, action) VALUES (949, 126, 'lastPage');
INSERT INTO action (id, features, action) VALUES (950, 127, 'search');
INSERT INTO action (id, features, action) VALUES (951, 127, 'reset');
INSERT INTO action (id, features, action) VALUES (952, 127, 'projectPropertyViewId');
INSERT INTO action (id, features, action) VALUES (953, 127, 'firstPage');
INSERT INTO action (id, features, action) VALUES (954, 127, 'previousPage');
INSERT INTO action (id, features, action) VALUES (955, 127, 'nextPage');
INSERT INTO action (id, features, action) VALUES (956, 127, 'lastPage');
INSERT INTO action (id, features, action) VALUES (957, 117, 'projectPropertyEdit');
INSERT INTO action (id, features, action) VALUES (958, 117, 'firstPage');
INSERT INTO action (id, features, action) VALUES (959, 117, 'previousPage');
INSERT INTO action (id, features, action) VALUES (960, 117, 'nextPage');
INSERT INTO action (id, features, action) VALUES (961, 117, 'lastPage');
INSERT INTO action (id, features, action) VALUES (964, 128, 'firstPage');
INSERT INTO action (id, features, action) VALUES (965, 128, 'previousPage');
INSERT INTO action (id, features, action) VALUES (966, 128, 'nextPage');
INSERT INTO action (id, features, action) VALUES (967, 128, 'lastPage');

-----------------------------------------------------------------------------------------------------------------------------------------------------------------


------------------------------------------Creacion de tabla tipo de unidad rentable, con sus relaciones y algunos datos--------------------------------------------

CREATE SEQUENCE rentable_unit_type_id_seq;
CREATE TABLE rentable_unit_type (
                id INTEGER NOT NULL DEFAULT nextval('rentable_unit_type_id_seq'),
                type VARCHAR NOT NULL,
                CONSTRAINT rentable_unit_type_pk PRIMARY KEY (id)
);


ALTER SEQUENCE rentable_unit_type_id_seq OWNED BY rentable_unit_type.id;

ALTER SEQUENCE rentable_unit_type_id_seq RESTART WITH 18;

INSERT INTO rentable_unit_type ( id, type) VALUES (1, 'Garaje');
INSERT INTO rentable_unit_type ( id, type) VALUES (2, 'Local Comercial');
INSERT INTO rentable_unit_type ( id, type) VALUES (3, 'Local Comida');
INSERT INTO rentable_unit_type ( id, type) VALUES (4, 'Oficina');
INSERT INTO rentable_unit_type ( id, type) VALUES (5, 'Hotel');
INSERT INTO rentable_unit_type ( id, type) VALUES (6, 'Patio de Contenedores');
INSERT INTO rentable_unit_type ( id, type) VALUES (7, 'Estacion de Servicio');
INSERT INTO rentable_unit_type ( id, type) VALUES (8, 'Serviteca');
INSERT INTO rentable_unit_type ( id, type) VALUES (9, 'Auditorio');
INSERT INTO rentable_unit_type ( id, type) VALUES (10, 'Parqueadero');
INSERT INTO rentable_unit_type ( id, type) VALUES (11, 'Salon Comunal');
INSERT INTO rentable_unit_type ( id, type) VALUES (12, 'Terraza');
INSERT INTO rentable_unit_type ( id, type) VALUES (13, 'Bodega');
INSERT INTO rentable_unit_type ( id, type) VALUES (14, 'Kiosko');
INSERT INTO rentable_unit_type ( id, type) VALUES (15, 'Deposito');
INSERT INTO rentable_unit_type ( id, type) VALUES (16, 'Cuarto Util');
INSERT INTO rentable_unit_type ( id, type) VALUES (17, 'Plazoleta');


ALTER TABLE rentable_unit ADD COLUMN rentable_unit_type INTEGER NOT NULL DEFAULT 1REFERENCES public.rentable_unit_type (id);

ALTER TABLE rentable_unit ADD CONSTRAINT rentable_unit_type_rentable_unit_fk
FOREIGN KEY (rentable_unit_type)
REFERENCES rentable_unit_type (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;



------------------------------------------Creacion de tabla tipo de facturacion, con sus relaciones y algunos datos--------------------------------------------

CREATE SEQUENCE billing_type_id_seq;

CREATE TABLE billing_type (
                id INTEGER NOT NULL DEFAULT nextval('billing_type_id_seq'),
                type VARCHAR NOT NULL,
                CONSTRAINT billing_type_pk PRIMARY KEY (id)
);


ALTER SEQUENCE billing_type_id_seq OWNED BY billing_type.id;

ALTER SEQUENCE billing_type_id_seq RESTART WITH 4;
INSERT INTO billing_type ( id, type) VALUES (1, 'Preimpreso');
INSERT INTO billing_type ( id, type) VALUES (2, 'Computador');
INSERT INTO billing_type ( id, type) VALUES (3, 'Digital');


ALTER TABLE billing_resolution ADD COLUMN billing_type INTEGER NOT NULL DEFAULT 1 REFERENCES billing_type (id); 

ALTER TABLE billing_resolution ADD CONSTRAINT billing_type_billing_resolution_fk
FOREIGN KEY (billing_type)
REFERENCES billing_type (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

----------------------------------------Modificacion de un campo en tipo de entidad de negocio(tipo tercero)-------------------------------------------------------------------
update business_entity_type
   SET name ='Proyecto/Cliente'
 WHERE id=1;
-----------------------------------------------------------------------------------------------------------------------------------------------------------------



-----------------------------------------------------------------------Otros 1------------------------------------------------------------------------------------------
alter table concept add column expression VARCHAR(20000);


alter table project_property alter column phone_billed drop not null;

alter table project_property alter column  address_billed  drop not null;

alter table project_property alter column  billed  drop not null;

alter table concept drop column plugin;

ALTER TABLE ONLY real_property ALTER COLUMN cadastral_id TYPE VARCHAR, ALTER COLUMN cadastral_id SET NOT NULL;
-----------------------------------------------------------------------------------------------------------------------------------------------------------------


------------------------------------------------Insercion de datos en objeto del contrato----------------------------------------------------------
SELECT pg_catalog.setval('object_of_contract_id_seq', 3, true);

INSERT INTO object_of_contract (id, description) VALUES (1, 'Activo');
INSERT INTO object_of_contract (id, description) VALUES (2, 'Unidad Arrendable');
INSERT INTO object_of_contract (id, description) VALUES (3, 'Servicios');
-----------------------------------------------------------------------------------------------------------------------------------------------------------------



------------------------------------------------------------------Otros 2-------------------------------------------------------------------------------------------
update real_property
   SET legal_nature_id = 4
 WHERE legal_nature_id= 1;
-----Se cambian los que tenga tipo 1->%PROPIEDAD PEI por 4->PROINDIVISO, para que no exista error al borrar el tipo 1, puesto q solo estos son los que piden el porcentaje.


delete FROM legal_nature_of_property
 WHERE id=1;
--se borra el campo %PROPIEDAD PEI

ALTER TABLE public.consecutive_accounts_billing DROP COLUMN max;

ALTER TABLE public.consecutive_credit_notes DROP COLUMN max;

ALTER TABLE business_line
   ALTER COLUMN business_unit DROP NOT NULL;
--Se hace scrip para cambiar de la tabla business_line la columna business_unit por nula




-----------------------------------------------------------------------------GROUP CACHE----------------------------------------------------------------------------------

CREATE SEQUENCE public.group_cache_id_seq;

CREATE TABLE public.group_cache (
               id INTEGER NOT NULL DEFAULT nextval('public.group_cache_id_seq'),
               real_property INTEGER NOT NULL,
               billed INTEGER NOT NULL,
               biller INTEGER NOT NULL,
               grouped BOOLEAN NOT NULL,
               grouped_into_period BOOLEAN NOT NULL,
               address INTEGER NOT NULL,
               billing_period VARCHAR NOT NULL,
               billing_count INTEGER NOT NULL,
               billing_document_type INTEGER NOT NULL,
               CONSTRAINT group_cache_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.group_cache_id_seq OWNED BY public.group_cache.id;

ALTER TABLE public.invoice ADD COLUMN real_property INTEGER;

ALTER TABLE public.invoice ADD CONSTRAINT real_property_invoice_fk
FOREIGN KEY (real_property)
REFERENCES public.real_property (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE terranvm_db.public.invoice ALTER COLUMN project_property DROP NOT NULL

-----------------------------------------------------------------------------------------------------------------------------------------------------------------

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------BORRANDO PLUGINS------------------------------------------------------------------------------------------------

DROP TABLE public.plugin_contract_type;

DROP TABLE public.plugin;



-----------------------------------------------------------------------------------------------------------------------------------------------------------------







