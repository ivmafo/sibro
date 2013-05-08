package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("realPropertyUseTypeHome")
public class RealPropertyUseTypeHome extends EntityHome<RealPropertyUseType> {

	public void setRealPropertyUseTypeId(Integer id) {
		setId(id);
	}

	public Integer getRealPropertyUseTypeId() {
		return (Integer) getId();
	}

	@Override
	protected RealPropertyUseType createInstance() {
		RealPropertyUseType realPropertyUseType = new RealPropertyUseType();
		return realPropertyUseType;
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

	public RealPropertyUseType getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RealProperty> getRealProperties() {
		return getInstance() == null ? null : new ArrayList<RealProperty>(getInstance().getRealProperties());
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean realPropertyUseTypeListInApprove(RealPropertyUseType realPropertyUseType){
		return new MakerCheckerHome().isObjectInMakerChecker(realPropertyUseType);
	}
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((RealPropertyUseType) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}
	
	@Transactional
	public void approveChange(){
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}","ApproveSuccessfully");
	}
	
	public void cancelChange(){
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}","CancelSuccessfully");
	}

}
