package org.koghi.terranvm.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.international.StatusMessages;

@Name("confirm")
public class Confirm
{
    @Logger private Log log;

    @In StatusMessages statusMessages;

    public void confirm()
    {
        // implement your business logic here
        log.info("confirm.confirm() action called");
        statusMessages.add("confirm");
    }

    // add additional action methods

}
