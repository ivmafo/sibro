package org.koghi.terranvm.session;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.BusinessEntity;

@Name("businessEntityList")
public class BusinessEntityList extends EntityQuery<BusinessEntity> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static final String EJBQL = "select businessEntity from BusinessEntity businessEntity";


	private static final String[] RESTRICTIONS = { "lower(concat(concat(businessEntity.idNumber,' - '),businessEntity.verificationNumber)) like lower(concat(#{businessEntityList.businessEntity.idNumber},'%'))", 
								"lower(businessEntity.nameBusinessEntity) like lower(concat('%',concat(#{businessEntityList.businessEntity.nameBusinessEntity},'%')))", 
								"lower(businessEntity.tradeName) like lower(concat('%',concat(#{businessEntityList.businessEntity.tradeName},'%')))", 
								"lower(businessEntity.email) like lower(concat('%',concat(#{businessEntityList.businessEntity.email},'%')))", 
								"lower(businessEntity.legalEntityType) like lower(concat(#{businessEntityList.businessEntity.legalEntityType},'%')", };

	private BusinessEntity businessEntity = new BusinessEntity();
	
	private Integer entityTypeId = 0;
	
	
	public BusinessEntityList() {
		setEjbql(EJBQL);
		
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}
	
	
	@Override
	public List<BusinessEntity> getResultList() {
		List<BusinessEntity> list =  super.getResultList();
		if (entityTypeId!=0){
			list = new BusinessEntityHome().filterListForType(list, entityTypeId);
		}
		return list;
	}

	public Integer getEntityTypeId() {
		return entityTypeId;
	}

	public void setEntityTypeId(Integer entityTypeId) {
		this.entityTypeId = entityTypeId;
	}
	
}

