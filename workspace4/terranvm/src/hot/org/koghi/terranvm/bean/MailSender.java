package org.koghi.terranvm.bean;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.seam.annotations.Name;

@Name("mailSender")
public class MailSender implements Runnable {

	private final String mailAddress = "terranvm.koghi";
	private final String password = "abc123ABC";

	private String addresses;
	private String subject;
	private String message;
	private String attachmentPath;
	private String attachmentName;

	/**
	 * USAGE EXAMPLE:
	 * 
	 * @code USAGE:
	 * @code MailSender mailSender = new MailSender(); String to = "someone@server.com,anotherone@server.com";String
	 *       subject = "mail subject string"; String atachmentPath = NULL;
	 *       String atachmentName = NULL; mailSender.set(to, message,
	 *       subject,atachmentPath ,atachmentName); (new
	 *       Thread(mailSender)).start();
	 */
	public MailSender() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Esta funcion se encarga de instanciar las variables necesarias para
	 * enviar el correo
	 * 
	 * @param to
	 *            : Recipientes de correo separados por comas ","
	 * @param sub
	 *            : Subject del correo
	 * @param m
	 *            : Cuerpo del mensaje
	 * @param a
	 *            : attachment Absolute path, if not NULL
	 * @param an
	 *            : attachment Name, if not NULL, cannot be null if a != null
	 */
	public void set(String to, String sub, String m, String a, String an) {

		this.addresses = to;
		this.subject = sub;
		this.message = m;
		this.attachmentPath = a;
		this.attachmentName = an;

	}

	/**
	 * Este metodo ejecuta el envio del correo electronico, antes de usarlo es
	 * obligatorio haber usado el metodo set de esta misma clase
	 */
	public void send() {
		try {
			Properties props = new Properties();
			String host = "smtp.gmail.com";
			String username = this.mailAddress;
			String password = this.password;
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.host", host);
			props.setProperty("mail.user", username);
			props.setProperty("mail.password", password);
			props.setProperty("mail.protocol.port", "465");
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtps.auth", "true");

			Session session = Session.getDefaultInstance(props, null);

			MimeMessage message = new MimeMessage(session);
			message.setSubject(this.subject);
			message.setContent(this.message, "text/plain");
			String[] array = this.addresses.split(",");
			for (int i = 0; i < array.length; i++) {
				if (!array[i].isEmpty())
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(array[i]));
			}
			if (this.attachmentPath != null) {
				MimeBodyPart messageBodyPart = new MimeBodyPart();

				messageBodyPart.setText(this.message);
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(this.attachmentPath);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(this.attachmentName);
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
			}

			Transport t = session.getTransport("smtps");

			t.connect(host, username, password);
			t.sendMessage(message, message.getAllRecipients());

			t.close();

			System.out.println("MAIL SENT - to " + addresses);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the mailAddress
	 */
	public String getMailAddress() {
		return mailAddress;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	public static void main(String[] args) {
		System.out.println(".");
		MailSender ms = new MailSender();
		ms.set("dvaldivieso@koghi.com", "PRUEBA TERRANVM", "HELLO WORLD", null, null);
		ms.send();
	}

	public void run() {
		this.send();

	}

	// public void AttachExample(String email, String atattachment) {
	// try {
	//
	// String host = "smtp.gmail.com";
	// String from = this.mailAddress;
	// String password = this.password;
	// // String to = "acamacho@koghi.com";
	// String to = "";
	// if (email != null && email != "")
	// to = email;
	//
	// String filename = "";
	// File f = new File(atattachment);
	// f.getName();
	// if (atattachment != null)
	// filename = atattachment;
	// // String filename =
	// // "/home/cserrano/Escritorio/creditNotesR1P-4.pdf";
	//
	// // Get system properties
	// Properties props = System.getProperties();
	//
	// props.setProperty("mail.transport.protocol", "smtp");
	// props.setProperty("mail.host", host);
	// props.setProperty("mail.user", from);
	// props.setProperty("mail.password", password);
	// props.setProperty("mail.protocol.port", "465");
	// props.setProperty("mail.smtp.starttls.enable", "true");
	// props.setProperty("mail.smtps.auth", "true");
	//
	// // Setup mail server
	// // props.put("mail.smtp.host", host);
	//
	// // Get session
	// Session session = Session.getInstance(props, null);
	//
	// // Define message
	// Message message = new MimeMessage(session);
	// message.setFrom(new InternetAddress(from));
	// message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	// message.setSubject("Nota Credito Aprobada");
	//
	// // Create the message part
	// BodyPart messageBodyPart = new MimeBodyPart();
	//
	// // Fill the message
	// messageBodyPart.setText(" Se envia el archivo adjunto de la nota credito que solicito y fue aprobada.");
	//
	// // Create a Multipart
	// Multipart multipart = new MimeMultipart();
	//
	// // Add part one
	// multipart.addBodyPart(messageBodyPart);
	//
	// //
	// // Part two is attachment
	// //
	//
	// // Create second body part
	// messageBodyPart = new MimeBodyPart();
	//
	// // Get the attachment
	// DataSource source = new FileDataSource(filename);
	//
	// // Set the data handler to the attachment
	// messageBodyPart.setDataHandler(new DataHandler(source));
	//
	// // Set the filename
	// messageBodyPart.setFileName(f.getName());
	// // messageBodyPart.setFileName(filename);
	//
	// // Add part two
	// multipart.addBodyPart(messageBodyPart);
	//
	// // Put parts in message
	// message.setContent(multipart);
	//
	// // Send the message
	// // Transport.send(message);
	// Transport t = session.getTransport("smtps");
	//
	// t.connect(host, from, password);
	// t.sendMessage(message, message.getAllRecipients());
	//
	// t.close();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}