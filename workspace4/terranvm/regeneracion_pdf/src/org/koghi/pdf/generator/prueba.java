package org.koghi.pdf.generator;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.koghi.database.Concection;
import org.koghi.database.Querys;
import org.koghi.properties.Configuration;

public class prueba {

    @SuppressWarnings("static-access") 
    public static void main(String[] args) {
//        immageDownlader downlader = new immageDownlader();
//        try {
//            downlader.download();
//        } catch (SQLException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        
        File f = new File(Configuration.CONFIG_FILE_NAME);
        System.out.println(f.getAbsolutePath());
        Querys querys = new Querys(); 
        List<Integer> notasCredito = querys.getIdCreditNotes();
        Configuration.getInstance().log("GeneracionPDF", Level.INFO, "Se inicia el proceso de regeneración de PDF");
        for (Integer integer : notasCredito) {
            Configuration.getInstance().log("GeneracionPDF", Level.INFO, "Se creara el Pdf para la nota crédito No: " + integer);
            generatePDF generatePDF = new generatePDF(); 
            try {
                generatePDF.createCreditNotesPDF(integer);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Configuration.getInstance().log("GeneracionPDF", Level.INFO, "Se finaliza el proceso de regeneración de PDF");
        Concection.getInstance().cerrarConcection(); 
    } 
} 
