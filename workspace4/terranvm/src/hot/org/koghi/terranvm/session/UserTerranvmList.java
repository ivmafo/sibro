package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.User_Terranvm;

@Name("userTerranvmList")
public class UserTerranvmList extends EntityQuery<User_Terranvm> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select userTerranvm from User_Terranvm userTerranvm";

	private static final String[] RESTRICTIONS = {
			"lower(userTerranvm.login) like lower(concat(#{userTerranvmList.userTerranvm.login},'%'))",
			"lower(userTerranvm.password) like lower(concat(#{userTerranvmList.userTerranvm.password},'%'))",
			"lower(userTerranvm.mail) like lower(concat(#{userTerranvmList.userTerranvm.mail},'%'))",
			"lower(userTerranvm.nombre) like lower(concat(#{userTerranvmList.userTerranvm.nombre},'%'))", };

	private User_Terranvm userTerranvm = new User_Terranvm();

	public UserTerranvmList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public User_Terranvm getUserTerranvm() {
		return userTerranvm;
	}
}
