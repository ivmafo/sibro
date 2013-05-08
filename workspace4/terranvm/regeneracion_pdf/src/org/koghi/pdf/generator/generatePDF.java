package org.koghi.pdf.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.koghi.database.Querys;
import org.koghi.properties.Configuration;
import org.koghi.properties.ConvertNumberToString;
import org.koghi.properties.PdfHome;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class generatePDF {

    final String COLUMN_PHONE_NUMBER = "number";
    final String COLUMN_CITY_NAME = "city_name";
    final String COLUMN_COUNTRY_NAME = "name";
    final String COLUMN_ID_BUSINESS_ENTITY = "business_entity";
    final String COLUMN_NAME_BUSINESS_ENTITY = "name_business_entity";
    final String COLUMN_BE_ID_NUMBER = "id_number";
    final String COLUMN_BE_VERIFICATION_NUMBER = "verification_number";
    final String COLUMN_BE_ID_TYPE = "id_type";
    final String COLUMN_P_ID = "id";
    final String COLUMN_P_NAME_PROJECT = "name_project";
    final String COLUMN_P_PROJECT_PRFEIX = "project_prefix";
    final String COLUMN_I_NUMBER = "number";
    final String COLUMN_I_SUFFIX = "suffix";
    final String COLUMN_I_NAME_BILLER = "name_biller";
    final String COLUMN_I_PREFIX = "prefix";
    final String COLUMN_I_ADDRESS_BILLER = "address_biller";
    final String COLUMN_I_CITY_BILLER = "city_biller";
    final String COLUMN_I_PHONE_BILLER = "phone_biller";
    final String COLUMN_I_BILLED = "billed";
    final String COLUMN_I_PROJECT_PROPERTY = "project_property";
    final String COLUMN_I_MANDATE = "mandate";
    final String COLUMN_I_NAME_BILLED = "name_billed";
    final String COLUMN_I_ID_NUMBER_BILLED = "id_number_billed";
    final String COLUMN_I_ADDRESS_BILLED = "address_billed";
    final String COLUMN_I_PHONE_BILLED = "phone_billed";
    final String COLUMN_I_CITY_BILLED = "city_billed";
    final String COLUMN_I_BILLER = "biller";
    final String COLUMN_CN_CONSECUTIVE = "consecutive";
    final String COLUMN_CN_SIIGO_CODE = "siigo_code";
    final String COLUMN_CN_REASON = "reason";
    final String COLUMN_CN_CREDIT_NOTE_DATE = "credit_note_date";
    final String COLUMN_IC_INVOICE = "invoice";
    final String COLUMN_IC_CALCULATED_TAX = "calculated_tax";
    final String COLUMN_IC_CALCULATED_COST = "calculated_cost";
    final String COLUMN_IC_CONCEPT = "concept";
    final String COLUMN_IC_INVOICE_CONCEPT_PARENT_TYPE = "invoice_concept_parent_type";
    final String COLUMN_IC_NAME_CONCEPT = "name_concept";
    final String COLUMN_IC_NEW_BALANCE = "new_balance";

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    NumberFormat formatter = new DecimalFormat("#,###.00");
    double total = 0.0;
    double balance = 0.0;
    // double cant = 0.0;
    HashMap<String, Object> invoice = null;
    HashMap<String, Object> creditNote;
    List<HashMap<String, Object>> invoiceConcepts;
    HashMap<String, Object> project;
    // ResultSet invoiceConceptParent;
    HashMap<String, Object> businessEntity;
    Querys querys = new Querys();

    public void createCreditNotesPDF(int idCreditNote) throws SQLException {
        try {

            ConvertNumberToString convertNumberToString = new ConvertNumberToString();
            String consecutive = "";
            creditNote = querys
                    .getFirstResult(
                            "SELECT cn.*, ccn.siigo_code FROM credit_note cn JOIN invoice_concept ic ON ic.credit_note = cn.id JOIN invoice i ON i.id = ic.invoice JOIN business_entity be ON be.id = i.biller JOIN consecutive_credit_notes ccn ON ccn.business_entity = be.id WHERE cn.id = "
                                    + idCreditNote + " LIMIT 1;", new String[] { COLUMN_CN_CONSECUTIVE, COLUMN_CN_SIIGO_CODE, COLUMN_CN_REASON,
                                    COLUMN_CN_CREDIT_NOTE_DATE });

            consecutive = (String) creditNote.get(COLUMN_CN_CONSECUTIVE);

            invoiceConcepts = querys
                    .getResultList(
                            "SELECT ic.*,icp.invoice_concept_type as invoice_concept_parent_type  , c.name as name_concept FROM invoice_concept ic JOIN invoice_concept icp ON icp.id = ic.invoice_concept_parent JOIN concept c ON c.id = icp.concept  WHERE  ic.credit_note = "
                                    + idCreditNote, new String[] { COLUMN_IC_INVOICE, COLUMN_IC_CALCULATED_TAX, COLUMN_IC_CALCULATED_COST,
                                    COLUMN_IC_CONCEPT, COLUMN_IC_INVOICE_CONCEPT_PARENT_TYPE, COLUMN_IC_NAME_CONCEPT, COLUMN_IC_NEW_BALANCE });

            // se extraen los datos de una de las Invoice que estan
            // involucradas
            // en esta nota crédito.
            invoice = querys.getFirstResult(
                    "SELECT i.*, pp.mandate from invoice i JOIN project_property pp ON pp.id = i.project_property WHERE i.id = "
                            + invoiceConcepts.get(0).get(COLUMN_IC_INVOICE), new String[] { COLUMN_I_ADDRESS_BILLED, COLUMN_I_CITY_BILLED,
                            COLUMN_I_ID_NUMBER_BILLED, COLUMN_I_MANDATE, COLUMN_I_NAME_BILLED, COLUMN_I_PHONE_BILLED, COLUMN_I_PROJECT_PROPERTY,
                            COLUMN_I_NUMBER, COLUMN_I_SUFFIX, COLUMN_I_NAME_BILLER, COLUMN_I_PREFIX, COLUMN_I_ADDRESS_BILLER, COLUMN_I_CITY_BILLER,
                            COLUMN_I_PHONE_BILLER, COLUMN_I_BILLED, COLUMN_I_BILLER });

            project = querys.getFirstResult(
                    "SELECT p.id , name_project , project_prefix , business_entity FROM project p JOIN project_property pp on pp.project = p.id  where pp.id = "
                            + (Integer) invoice.get(COLUMN_I_PROJECT_PROPERTY), new String[] { COLUMN_P_ID, COLUMN_P_NAME_PROJECT,
                            COLUMN_P_PROJECT_PRFEIX, COLUMN_ID_BUSINESS_ENTITY });
            Document document = new Document(PageSize.LETTER);

            if (invoice != null) {

                // -------CREAR CARPETA PARA ALMACENAR
                // ARCHIVOS-------------------

                String systemConfiguration = Configuration.getInstance().getProperty(Configuration.PDF_PATH);

                String OrigenCarpeta = systemConfiguration + "/" + (Integer) project.get(COLUMN_P_ID) + "_"
                        + (String) project.get(COLUMN_P_NAME_PROJECT);
                File directorio = new File(OrigenCarpeta);
                directorio.mkdir();
                // --------------------------

                // invoice en la misma hoja de terminos
                try {

                    String filePDF = "creditNotes" + (String) project.get(COLUMN_P_PROJECT_PRFEIX) + "-C-"
                            + (String) creditNote.get(COLUMN_CN_SIIGO_CODE) + "-" + consecutive + ".pdf";
                    // String server =
                    // Configuration.getInstance().getProperty(Configuration.DATABASE_SERVER);
                    String path = Configuration.getInstance().getProperty(Configuration.SERVER_PATH); // FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
                    path = path.substring(0, path.lastIndexOf("/")) + "/";
                    // --------------------------
                    String filePDF1 = "";

                    filePDF1 = (String) project.get(COLUMN_P_PROJECT_PRFEIX) + "-C-" + (String) creditNote.get(COLUMN_CN_SIIGO_CODE) + "-"
                            + consecutive + ".pdf";
                    PdfWriter.getInstance(document, new FileOutputStream(OrigenCarpeta + "/" + filePDF1));
                    // --------------------------

                    PdfWriter.getInstance(document, new FileOutputStream(path + filePDF));

                    document.open();
                    document.newPage();
                    String fileIMAGE = "img/log_Terranum.jpg";
                    String consultarLogo = "SELECT logo from business_entity WHERE id = ";
                    byte[] logo = null;
                    byte[] logo2 = null;
                    ResultSet logos = querys.consultar(consultarLogo + (Integer) invoice.get(COLUMN_I_BILLER));
                    while (logos.next())
                        logo2 = logos.getBytes("logo");
                    try {
                        if ((Boolean) invoice.get("mandate") != null && (Boolean) invoice.get("mandate")) {
                            ResultSet logos2 = querys.consultar(consultarLogo + (Integer)project.get(COLUMN_ID_BUSINESS_ENTITY));
                            while (logos2.next())
                                logo = logos2.getBytes("logo");

                            if (logo != null && logo.length > 0) {
                                Image image = Image.getInstance(logo);
                                image.setAbsolutePosition(50, 700);
                                image.scaleToFit(150, 80);
                                document.add(image);
                            }
                            if (logo2 != null && logo2.length > 0) {
                                Image image1 = Image.getInstance(logo2);
                                image1.setAbsolutePosition(400, 700);
                                image1.scaleToFit(150, 80);
                                document.add(image1);
                            }
                        } else {
                            if (logo2 != null && logo2.length > 0) {
                                Image image = Image.getInstance(logo2);
                                image.setAbsolutePosition(50, 700);
                                image.scaleToFit(150, 80);
                                document.add(image);
                            }
                            Image image1 = Image.getInstance(fileIMAGE);
                            image1.setAbsolutePosition(400, 700);
                            image1.scaleToFit(150, 80);
                            document.add(image1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    String fontFile = "Font/TAHOMA.TTF";
                    BaseFont bf = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, true);

                    Font font1 = new Font(bf, 7, Font.NORMAL);
                    Font font2 = new Font(bf, 8, Font.BOLD);
                    Font font3 = new Font(bf, 7, Font.BOLD);
                    Font font4 = new Font(bf, 8, Font.NORMAL);

                    document.add(new Paragraph("\n\n\n\n\n"));

                    PdfPTable table1 = new PdfPTable(2);
                    table1.setWidthPercentage(100);
                    table1.setHorizontalAlignment(Element.ALIGN_LEFT);

                    PdfPCell cell1 = new PdfPCell();
                    // String addresses;
                    String consultarBusiness = "SELECT name_business_entity , id_number , verification_number , id_type from business_entity where id = ";
                    String consultarCountryAndPhone = "SELECT c.name, ct.name as city_name  , pn.number  from project_property pp JOIN phone_number pn on pn.id = pp.phone_biller JOIN address a ON pp.address_biller = a.id JOIN city ct on a.city = ct.id JOIN region r ON ct.region_id = r.id JOIN country c ON r.country_id = c.id WHERE pp.id = ";
                    String nombrePais = "", phoneBiller = "";
                    String parrafo = "";
                    if ((Boolean) invoice.get(COLUMN_I_MANDATE) != null && (Boolean) invoice.get(COLUMN_I_MANDATE)) {

                        businessEntity = querys.getFirstResult(consultarBusiness + (Integer) project.get(COLUMN_ID_BUSINESS_ENTITY), new String[] {
                                COLUMN_NAME_BUSINESS_ENTITY, COLUMN_BE_ID_NUMBER, COLUMN_BE_VERIFICATION_NUMBER, COLUMN_BE_ID_TYPE });
                        consultarCountryAndPhone = "SELECT c.name, ct.name as city_name  , pn.number  from business_entity be join phone_number pn ON pn.business_entity = be.id JOIN address a ON be.id =  a.id JOIN city ct on a.city = ct.id JOIN region r ON ct.region_id = r.id JOIN country c ON r.country_id = c.id WHERE be.id = "
                                + (Integer) project.get(COLUMN_ID_BUSINESS_ENTITY);
                        // while(countryAndPhone.next()){
                        // nombrePais = countryAndPhone.getString("name");
                        // phoneBiller = countryAndPhone.getString("number");
                        // }
                        String[] parameter = { COLUMN_PHONE_NUMBER, COLUMN_CITY_NAME, COLUMN_COUNTRY_NAME };
                        HashMap<String, Object> nameCityAndPhone = querys.getFirstResult(consultarCountryAndPhone, parameter);
                        parrafo = 
                                (String) businessEntity.get("name_business_entity")
                                + "\n"
                                + querys.getAddres((Integer) project.get(COLUMN_ID_BUSINESS_ENTITY))
                                + " TEL. "
                                + nameCityAndPhone.get(COLUMN_PHONE_NUMBER)
                                + "\n"
                                + nameCityAndPhone.get(COLUMN_CITY_NAME)
                                + " - "
                                + nameCityAndPhone.get(COLUMN_COUNTRY_NAME)
                                + "\n"
                                + ((Integer) businessEntity.get("id_type") == 31 ? ("NIT." + (String) businessEntity.get(COLUMN_BE_ID_NUMBER) + " - " + (Integer) businessEntity
                                        .get("verification_number")) : "C.C." + (String) businessEntity.get(COLUMN_BE_ID_NUMBER));
                        cell1 = new PdfPCell(new Paragraph(parrafo, font3));
                    } else {
                        businessEntity = querys.getFirstResult(consultarBusiness + (Integer) invoice.get(COLUMN_I_BILLER), new String[] {
                                COLUMN_NAME_BUSINESS_ENTITY, COLUMN_BE_ID_NUMBER, COLUMN_BE_VERIFICATION_NUMBER, COLUMN_BE_ID_TYPE });
                        // ResultSet datosPP =
                        // querys.consultar(consultarCountryAndPhone +
                        // invoice.getInt("project_property"););

                        // while(datosPP.next()){
                        parrafo = (String) invoice.get(COLUMN_I_NAME_BILLER)
                                + "\n"
                                + (String) invoice.get(COLUMN_I_ADDRESS_BILLER)
                                + " TEL. "
                                + phoneBiller
                                + "\n"
                                + (String) invoice.get(COLUMN_I_CITY_BILLER)
                                + " - "
                                + nombrePais
                                + "\n"
                                + ((Integer) businessEntity.get("id_type") == 31 ? ("NIT." + (String) businessEntity.get(COLUMN_BE_ID_NUMBER) + " - " + (Integer) businessEntity
                                        .get("verification_number")) : "C.C." + (String) businessEntity.get(COLUMN_BE_ID_NUMBER));
                        // }
                        cell1 = new PdfPCell(new Paragraph(parrafo, font3));

                    }
                    cell1.setBorder(Rectangle.NO_BORDER);
                    table1.addCell(cell1);

                    PdfPCell cell8 = new PdfPCell(new Paragraph("NOTA CREDITO No C " + (String) creditNote.get(COLUMN_CN_SIIGO_CODE) + " - "
                            + consecutive, font2));
                    cell8.setBorder(Rectangle.NO_BORDER);
                    table1.addCell(cell8);

                    document.add(table1);
                    document.add(new Paragraph("\n"));

                    // /////////////////////////////////////////////////////////////////////////////////////////////////////

                    PdfPTable table6 = new PdfPTable(3);
                    table6.setWidthPercentage(100);
                    table6.setHorizontalAlignment(Element.ALIGN_LEFT);

                    PdfPCell cell9 = new PdfPCell(new Paragraph("Señores: " + (String) invoice.get("name_billed"), font1));
                    cell9.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell9);

                    PdfPCell cell10 = new PdfPCell(new Paragraph("Nit:  " + (String) invoice.get("id_number_billed"), font1));
                    cell10.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell10);

                    PdfPCell cell11 = new PdfPCell(new Paragraph("Fecha: " + dateFormat.format((Date) creditNote.get(COLUMN_CN_CREDIT_NOTE_DATE)),
                            font1));
                    cell11.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell11);

                    PdfPCell cell12 = new PdfPCell(new Paragraph("Direccion: " + (String) invoice.get("address_billed"), font1));
                    cell12.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell12);

                    PdfPCell cell13 = new PdfPCell(new Paragraph("Telefonos:  " + (String) invoice.get("phone_billed"), font1));
                    cell13.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell13);

                    PdfPCell cell14 = new PdfPCell(new Paragraph("", font1));
                    cell14.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell14);

                    PdfPCell cell15 = new PdfPCell(new Paragraph("Ciudad: " + (String) invoice.get("city_billed"), font1));
                    cell15.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell15);


                    PdfPCell cell16 = new PdfPCell(new Paragraph("", font1));
                    cell16.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell16);
                    
                    PdfPCell cell14r = new PdfPCell(new Paragraph("", font1));
                    cell14r.setBorder(Rectangle.NO_BORDER);
                    table6.addCell(cell14r);
     
                    document.add(table6);
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph("A continuación se describen los descuentos aplicados por motivo de: ", font3));
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph((String) creditNote.get(COLUMN_CN_REASON), font4));
                    document.add(new Paragraph("\n"));

                    // ///////////////////TITULOS DE LA
                    // TABLA//////////////////////////////////////////////

                    float[] colsWidth2 = { 1f, 2f, 1f };
                    PdfPTable table2 = new PdfPTable(colsWidth2);
                    table2.setWidthPercentage(100);
                    table2.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell cell18 = new PdfPCell(new Paragraph("DOCUMENTO ", font1));
                    cell18.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
                    cell18.setGrayFill(0.6f);
                    table2.addCell(cell18);

                    PdfPCell cell19 = new PdfPCell(new Paragraph("CONCEPTO ", font1));
                    cell19.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
                    cell19.setGrayFill(0.6f);
                    table2.addCell(cell19);

                    PdfPCell cell20 = new PdfPCell(new Paragraph("VALOR ", font1));
                    cell20.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
                    cell20.setGrayFill(0.6f);
                    table2.addCell(cell20);

                    document.add(table2);
                    Double ivaTotal = 0.0;
                    for (HashMap<String, Object> invoiceConcepts : this.invoiceConcepts) {

                        ivaTotal += (Double) invoiceConcepts.get(COLUMN_IC_CALCULATED_TAX);
                        float[] colsWidth = { 1f, 2f, 1f };
                        PdfPTable table = new PdfPTable(colsWidth);
                        table.setWidthPercentage(100);
                        table.setHorizontalAlignment(Element.ALIGN_CENTER);
                        String getInvoice = 
                                "SELECT i.*, pp.mandate from invoice i JOIN project_property pp ON pp.id = i.project_property WHERE i.id = "
                                        + invoiceConcepts.get(COLUMN_IC_INVOICE);
                        
                        String phrase = "" + new PdfHome().invoiceNumber(querys.getFirstResult(getInvoice, new String[] { COLUMN_I_ADDRESS_BILLED, COLUMN_I_CITY_BILLED,
                                COLUMN_I_ID_NUMBER_BILLED, COLUMN_I_MANDATE, COLUMN_I_NAME_BILLED, COLUMN_I_PHONE_BILLED, COLUMN_I_PROJECT_PROPERTY,
                                COLUMN_I_NUMBER, COLUMN_I_SUFFIX, COLUMN_I_NAME_BILLER, COLUMN_I_PREFIX, COLUMN_I_ADDRESS_BILLER, COLUMN_I_CITY_BILLER,
                                COLUMN_I_PHONE_BILLER, COLUMN_I_BILLED, COLUMN_I_BILLER }) );
                        Phrase phrase1 = new Phrase(20,phrase , font4);
                        PdfPCell cell21 = new PdfPCell(new Paragraph(phrase1));
                        cell21.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
                        table.addCell(cell21);

                        String ivaPorcent = "";
                        String consultarTax = "SELECT rra.value from retention_rate_account rra join concept_retention_rate_account crra ON crra.retention_rate_account = rra.id where crra.concept = "
                                + (Integer) invoiceConcepts.get(COLUMN_IC_CONCEPT) + "  and rra.retention_rate = ";
                        if ((Integer) invoiceConcepts.get(COLUMN_IC_INVOICE_CONCEPT_PARENT_TYPE) == PdfHome.TYPE_INTEREST) {
                            ivaPorcent = (querys.getTax(consultarTax + "1104") == null ? "" : "(IVA " +  querys.getTax(consultarTax + "1104") +"%)");
                            System.out.println(ivaPorcent);
                        } else {
                            ivaPorcent = (querys.getTax(consultarTax + "3") == null ? "" : "(IVA " + querys.getTax(consultarTax + "3") +"%)");
                        }
                        Phrase phrase2 = new Phrase(20, ""
                                + new PdfHome().nameInvoiceConcept((Integer) invoiceConcepts.get(COLUMN_IC_INVOICE_CONCEPT_PARENT_TYPE),
                                        (String) invoiceConcepts.get(COLUMN_IC_NAME_CONCEPT)) + ivaPorcent, font4);
                        PdfPCell cell22 = new PdfPCell(new Paragraph(phrase2));
                        cell22.setBorder(Rectangle.RIGHT);
                        table.addCell(cell22);

                        // [VALOR RECAUDO ]
                        Phrase phrase3 = new Phrase(20, "$"
                                + formatter.format((Double) invoiceConcepts.get(COLUMN_IC_CALCULATED_COST)
                                        + (Double) invoiceConcepts.get(COLUMN_IC_CALCULATED_TAX)), font4);

                        PdfPCell cell23 = new PdfPCell(new Paragraph(phrase3));
                        cell23.setBorder(Rectangle.RIGHT);
                        cell23.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
                        table.addCell(cell23);

                        document.add(table);

                        total = total + (Double) invoiceConcepts.get("calculated_cost") + (Double) invoiceConcepts.get("calculated_tax");

                        balance = balance + (Double) invoiceConcepts.get(COLUMN_IC_NEW_BALANCE);

                    }

                    // --------Excedente--------------------------------------------------

                    float[] colsWidth3 = { 1f, 2f, 1f };
                    PdfPTable table7 = new PdfPTable(colsWidth3);
                    table7.setWidthPercentage(100);
                    table7.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell cell22 = new PdfPCell(new Paragraph(""));
                    cell22.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
                    table7.addCell(cell22);

                    PdfPCell newParag = new PdfPCell(new Paragraph("\n", font4));
                    newParag.setBorder(Rectangle.RIGHT);
                    table7.addCell(newParag);
                    newParag.setBorder(Rectangle.RIGHT);
                    table7.addCell(newParag);
                    newParag.setBorder(Rectangle.LEFT);
                    table7.addCell(newParag);

                    PdfPCell cell23 = new PdfPCell(new Paragraph("SALDO ", font4));
                    cell23.setBorder(Rectangle.BOX);
                    table7.addCell(cell23);

                    PdfPCell cell24 = new PdfPCell(new Paragraph("$" + formatter.format(balance), font4));
                    cell24.setBorder(Rectangle.BOX);
                    cell24.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
                    table7.addCell(cell24);

                    document.add(table7);

                    // --------------------------------------------------

                    Phrase phraseIva = new Phrase(20, "IVA  ", font4);
                    // [TOTAL]
                    Phrase phraseIvaValue = new Phrase(20, "$" + formatter.format(ivaTotal), font4);
                    float[] colsWidth1 = { 2f, 1f, 1f };
                    PdfPTable table3 = new PdfPTable(colsWidth1);
                    table3.setWidthPercentage(100);
                    table3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    PdfPCell celdaSinMarco = new PdfPCell();
                    celdaSinMarco.setBorder(Rectangle.TOP + Rectangle.LEFT);
                    table3.addCell(celdaSinMarco);
                    table3.addCell(phraseIva);
                    PdfPCell cellIva = new PdfPCell(phraseIvaValue);
                    cellIva.setBorder(Rectangle.TOP + Rectangle.RIGHT);
                    cellIva.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
                    table3.addCell(cellIva);
                    // --------------------------------------------------

                    Phrase phrase4 = new Phrase(20, "TOTAL  ", font4);
                    // [TOTAL]
                    Phrase phrase5 = new Phrase(20, "$" + formatter.format(total), font4);

                    celdaSinMarco.setBorder(Rectangle.LEFT);
                    table3.addCell(celdaSinMarco);
                    table3.addCell(phrase4);

                    PdfPCell cell25 = new PdfPCell(phrase5);
                    cell25.setBorder(Rectangle.TOP + Rectangle.RIGHT);
                    cell25.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
                    table3.addCell(cell25);

                    document.add(table3);

                    // /////////////////////////TERCERA
                    // PARTE DE LA
                    // TABLA///////////////////////////

                    Phrase phrase21 = new Phrase(20, "SON: " + convertNumberToString.convertToString(total), font4);

                    PdfPTable table4 = new PdfPTable(1);
                    table4.setWidthPercentage(100);
                    table4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table4.addCell(phrase21);
                    document.add(table4);

                    // //////////////////////////CUARTA
                    // PARTE DE LA
                    // TABLA////////////////////////////

                    Phrase phrase23 = new Phrase(20, "\n  Firma de Recibido: ", font2);

                    PdfPTable table5 = new PdfPTable(2);
                    table5.setWidthPercentage(100);
                    table5.setHorizontalAlignment(Element.ALIGN_TOP);

                    table5.addCell("");
                    table5.addCell(phrase23);

                    document.add(table5);

                    document.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    document.close();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}