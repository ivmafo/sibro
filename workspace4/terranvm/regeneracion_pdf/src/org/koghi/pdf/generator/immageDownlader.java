package org.koghi.pdf.generator;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import org.koghi.database.Querys;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

public class immageDownlader {

    public void download() throws SQLException{
        String consultarLogo = "SELECT logo from business_entity WHERE id = ";
        byte[] logo = null;
        Querys querys = new Querys();
        for (int i = 0; i < 575; i++) {
            ResultSet logos;
                logos = querys.consultar(consultarLogo + i);
                while(logos.next())
                    logo = logos.getBytes("logo");
                if(logo != null && logo.length > 0){
                    try {
                        // convert byte array back to BufferedImage
                        InputStream in = new ByteArrayInputStream(logo);
                        BufferedImage bImageFromConvert = ImageIO.read(in);
             
                        ImageIO.write(bImageFromConvert, "jpg", new File(
                                "/home/wfamaya/Escritorio/imagenes/logo_business_entity_" + i +".jpg"));
             
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                
            
            
        }
    }
    }
    
}
