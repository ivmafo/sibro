package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("contractTypeList")
public class ContractTypeList extends EntityQuery<ContractType> {

	private static final String EJBQL = "select contractType from ContractType contractType";

	private static final String[] RESTRICTIONS = {
			"lower(contractType.name) like lower(concat(#{contractTypeList.contractType.name},'%'))",
			"lower(contractType.description) like lower(concat(#{contractTypeList.contractType.description},'%'))", };

	private ContractType contractType = new ContractType();

	public ContractTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ContractType getContractType() {
		return contractType;
	}
}
