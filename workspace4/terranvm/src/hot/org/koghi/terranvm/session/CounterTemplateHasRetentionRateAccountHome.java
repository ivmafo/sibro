package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.CounterConfigurationTamplate;
import org.koghi.terranvm.entity.CounterTemplateHasRetentionRateAccount;

@Name("counterTemplateHasRetentionRateAccountHome")
public class CounterTemplateHasRetentionRateAccountHome extends EntityHome<CounterTemplateHasRetentionRateAccount> {

	@In(create = true)
	CounterConfigurationTamplateHome counterConfigurationTamplateHome;

	public void setCounterTemplateHasRetentionRateAccountId(Integer id) {
		setId(id);
	}

	public Integer getCounterTemplateHasRetentionRateAccountId() {
		return (Integer) getId();
	}

	@Override
	protected CounterTemplateHasRetentionRateAccount createInstance() {
		CounterTemplateHasRetentionRateAccount counterTemplateHasRetentionRateAccount = new CounterTemplateHasRetentionRateAccount();
		return counterTemplateHasRetentionRateAccount;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		CounterConfigurationTamplate counterConfigurationTamplate = counterConfigurationTamplateHome.getDefinedInstance();
		if (counterConfigurationTamplate != null) {
			getInstance().setCounterConfigurationTamplate(counterConfigurationTamplate);
		}
	}

	public boolean isWired() {
		if (getInstance().getCounterConfigurationTamplate() == null)
			return false;
		return true;
	}

	public CounterTemplateHasRetentionRateAccount getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
