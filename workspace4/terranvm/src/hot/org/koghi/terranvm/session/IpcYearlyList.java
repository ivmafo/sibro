package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.IpcYearly;

@Name("ipcYearlyList")
public class IpcYearlyList extends EntityQuery<IpcYearly> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select ipcYearly from IpcYearly ipcYearly";

	private static final String[] RESTRICTIONS = {};

	private IpcYearly ipcYearly = new IpcYearly();

	public IpcYearlyList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public IpcYearly getIpcYearly() {
		return ipcYearly;
	}
}
