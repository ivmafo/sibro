package org.koghi.properties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;


public class PdfHome {
    public static final int TYPE_INTEREST = 3;
    public static final int TYPE_RETROACTIVE = 4;
    

    /**
     * forma el nombre a mostrar de un invoiceConcept, dependiendo del tipo(intereses, retroactivos, normales)
     * @param invoiceConceptType
     * @param nameConcept
     * @return nombre concepto(modificado)
     */
    public String nameInvoiceConcept(int invoiceConceptType, String nameConcept){
        String name = "";
        if(invoiceConceptType == TYPE_INTEREST)
            name = nameConcept + " (Interes de Mora)";
        else if(invoiceConceptType == TYPE_RETROACTIVE)
            name =  nameConcept + " (Retroactivo)";
        else
            name = nameConcept;
        return name;
    }
    
    /**
     * Este metodo recibe un ResultSet con los datos del invoice de orbis que esta siendo analizado y devuelve un string concatenando el
     * prefijo, numero y sufijo del consecutivo una factura o el consecutivo de
     * una cuenta de cobro
     */

    public String invoiceNumber(HashMap<String, Object> invoice) {
        String number = "";

        try {
            if ((String)invoice.get("prefix") == null)
                number = invoice.get("number") + " - " + (String)invoice.get("suffix");
            else if ((String)invoice.get("suffix") == null)
                number = invoice.get("prefix") + " - " + (String)invoice.get("number");
            else if ((String)invoice.get("number") == null)
                number = "";
            else if (invoice.get("prefix") == null && (String)invoice.get("suffix") == null)
                number = (String)invoice.get("number");
            else if (invoice.get("prefix") == null && (String)invoice.get("suffix") == null
                    && (String)invoice.get("number") == null)
                number = "";
            else
                number = (String)invoice.get("prefix") + " - " +(String) invoice.get("number") + " - "
                        + (String)invoice.get("suffix");
        } catch (Exception e) {
            Configuration.log(this.getClass().getName(), Level.SEVERE, "No se pudo obtener el numero de la invoice, esto fue causado por: ");
            e.printStackTrace();
        }
        return number;
    }
    
}
