package org.koghi.terranvm.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.IpcAccumulated;

import java.util.Arrays;

@Name("ipcAccumulatedList")
public class IpcAccumulatedList extends EntityQuery<IpcAccumulated> {

	private static final String EJBQL = "select ipcAccumulated from IpcAccumulated ipcAccumulated";

	private static final String[] RESTRICTIONS = {};

	private IpcAccumulated ipcAccumulated = new IpcAccumulated();

	public IpcAccumulatedList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public IpcAccumulated getIpcAccumulated() {
		return ipcAccumulated;
	}
}
