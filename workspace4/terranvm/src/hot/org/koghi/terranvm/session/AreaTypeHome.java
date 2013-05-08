package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.Area;
import org.koghi.terranvm.entity.AreaType;

@Name("areaTypeHome")
public class AreaTypeHome extends EntityHome<AreaType> {

	public void setAreaTypeId(Integer id) {
		setId(id);
	}

	public Integer getAreaTypeId() {
		return (Integer) getId();
	}

	@Override
	protected AreaType createInstance() {
		AreaType areaType = new AreaType();
		return areaType;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public AreaType getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Area> getAreas() {
		return getInstance() == null ? null : new ArrayList<Area>(getInstance().getAreas());
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean areaTypeListInApprove(AreaType areatype){
		return new MakerCheckerHome().isObjectInMakerChecker(areatype);
	}
	
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((AreaType) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}
	
	@Transactional
	public void approveChange(){
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "Aprobación exitosa","ApproveSuccessfully");
	}
	
	public void cancelChange(){
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "Cancelación exitosa","CancelSuccessfully");
	}
	
	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}

}
