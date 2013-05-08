package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.Area;
import org.koghi.terranvm.entity.AreaType;
import org.koghi.terranvm.entity.Floor;
import org.koghi.terranvm.entity.RentableUnit;

@Name("areaHome")
public class AreaHome extends EntityHome<Area> {

	@In(create = true)
	AreaTypeHome areaTypeHome;
	@In(create = true)
	FloorHome floorHome;
	
	

	public void setAreaId(Integer id) {
		setId(id);
	}

	public Integer getAreaId() {
		return (Integer) getId();
	}

	@Override
	protected Area createInstance() {
		Area area = new Area();
		return area;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		AreaType areaType = areaTypeHome.getDefinedInstance();
		if (areaType != null) {
			getInstance().setAreaType(areaType);
		}
		Floor floor = floorHome.getDefinedInstance();
		if (floor != null) {
			getInstance().setFloor(floor);
		}
	}

	public boolean isWired() {
		if (getInstance().getAreaType() == null)
			return false;
		if (getInstance().getFloor() == null)
			return false;
		return true;
	}

	public Area getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RentableUnit> getRentableUnits() {
		return getInstance() == null ? null : new ArrayList<RentableUnit>(getInstance().getRentableUnits());
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean areaListInApprove(Area area){
		return new MakerCheckerHome().isObjectInMakerChecker(area);
	}
	
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((Area) new MakerCheckerHome().getInstance(makerCheckerId));
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
