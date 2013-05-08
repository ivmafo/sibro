/**
 * 
 */
package org.koghi.terranvm.maker_checker;

import java.util.Hashtable;

/**
 * @author jlopez
 *
 */
public class MakerCheckerConfig {
	
	private static final Hashtable<String, String> NAMES = new Hashtable<String, String>();
	private static final Hashtable<String, String> VIEW_PAGES = new Hashtable<String, String>();
	private static final Hashtable<String, String> NAME_PARAMS_PAGE = new Hashtable<String, String>();
	
	{
		NAMES.put("org.koghi.terranvm.entity.Area", "Área");
		NAMES.put("org.koghi.terranvm.entity.AreaType", "Tipo de Área");
		NAMES.put("org.koghi.terranvm.entity.BusinessLine", "Linea de Negocio");
		NAMES.put("org.koghi.terranvm.entity.Project", "Proyecto");
		NAMES.put("org.koghi.terranvm.entity.Contact", "Contactos");
		NAMES.put("org.koghi.terranvm.entity.Floor", "Pisos");
		NAMES.put("org.koghi.terranvm.entity.RentableUnit", "Unidad Arrendable");
		NAMES.put("org.koghi.terranvm.entity.RealProperty", "Activo");
		NAMES.put("org.koghi.terranvm.entity.RealPropertyUseType", "Tipo de Uso del Activo");
		NAMES.put("org.koghi.terranvm.entity.LegalNatureOfProperty", "Naturaleza Jurídica del Activo");
		NAMES.put("org.koghi.terranvm.entity.Construction", "Edificio");
		NAMES.put("org.koghi.terranvm.entity.ProjectProperty", "Hoja de Términos");
		NAMES.put("org.koghi.terranvm.entity.EconomicActivity", "Actividad Económica");
		NAMES.put("org.koghi.terranvm.entity.BusinessEntityType", "Tipo de Entidad de Negocio");
		NAMES.put("org.koghi.terranvm.entity.BusinessEntity", "Entidad de Negocio");
		NAMES.put("org.koghi.terranvm.entity.Concept", "Concepto");
		NAMES.put("org.koghi.terranvm.entity.Recover", "Recaudo");
		NAMES.put("org.koghi.terranvm.entity.CreditNote", "Nota Crédito");
	}

	{
		VIEW_PAGES.put("org.koghi.terranvm.entity.Area", "Area.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.AreaType", "AreaType.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.BusinessLine", "BusinessLine.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.Project", "Project.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.Contact", "Contact.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.Floor", "Floor.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.RentableUnit", "RentableUnit.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.RealProperty", "RealProperty.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.RealPropertyUseType", "RealPropertyUseType.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.LegalNatureOfProperty", "LegalNatureOfProperty.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.Construction", "Construction.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.ProjectProperty", "ProjectProperty.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.EconomicActivity", "EconomicActivity.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.BusinessEntityType", "BusinessEntityType.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.BusinessEntity", "BusinessEntity.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.Concept", "Concept.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.Concept", "Recover.xhtml");
		VIEW_PAGES.put("org.koghi.terranvm.entity.CreditNote", "CreditNotesView.xhtml");
	}
	
	{
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Area", "areaId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.AreaType", "areaTypeId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.BusinessLine", "businessLineId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Project", "projectId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Contact", "contactId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Floor", "floorId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.RentableUnit", "rentableUnitId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.RealProperty", "realPropertyId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.RealPropertyUseType", "realPropertyUseTypeId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.LegalNatureOfProperty", "legalNatureOfPropertyId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Construction", "constructionId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.ProjectProperty", "projectPropertyId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.EconomicActivity", "economicActivityId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.BusinessEntityType", "businessEntityTypeId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.BusinessEntity", "businessEntityId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Concept", "conceptId");
		NAME_PARAMS_PAGE.put("org.koghi.terranvm.entity.Recover", "recoverId");
		
	}
	
	
	public String getViewPage(String key){
		return VIEW_PAGES.get(key);
	}
	
	public String getNameView(String key){
		return NAMES.get(key);
	}
	
	public String getParamNameView(String key){
		return NAME_PARAMS_PAGE.get(key);
	}
}
