package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.SegmentStage;

@Name("segmentStageList")
public class SegmentStageList extends EntityQuery<SegmentStage> {

	private static final String EJBQL = "select segmentStage from SegmentStage segmentStage";

	private static final String[] RESTRICTIONS = { "lower(segmentStage.tramo) like lower(concat(#{segmentStageList.segmentStage.tramo},'%'))", };

	private SegmentStage segmentStage = new SegmentStage();

	public SegmentStageList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SegmentStage getSegmentStage() {
		return segmentStage;
	}
}
