package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.ConsecutiveDailySiigo;

@Name("consecutiveDailySiigoHome")
public class ConsecutiveDailySiigoHome extends EntityHome<ConsecutiveDailySiigo> {

	public void setConsecutiveDailySiigoId(Integer id) {
		setId(id);
	}

	public Integer getConsecutiveDailySiigoId() {
		return (Integer) getId();
	}

	@Override
	protected ConsecutiveDailySiigo createInstance() {
		ConsecutiveDailySiigo consecutiveDailySiigo = new ConsecutiveDailySiigo();
		return consecutiveDailySiigo;
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

	public ConsecutiveDailySiigo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
