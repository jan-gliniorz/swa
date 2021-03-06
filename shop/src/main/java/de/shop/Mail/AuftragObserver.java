package de.shop.Mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.logging.Logger;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Auftragsverwaltung.service.NeuerAuftrag;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Util.Log;

@ApplicationScoped
@Log
public class AuftragObserver implements Serializable {
	private static final long serialVersionUID = -1567643645881819340L;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Inject 
	private transient Logger logger;
	
	@Resource(lookup = "java:jboss/mail/Default")
	private transient Session mailSession;
	
	private String mailAbsender;   // in META-INF\seam-beans.xml setzen
	private String nameAbsender;   // in META-INF\seam-beans.xml setzen

	@PostConstruct
	private void init() {
		if (mailAbsender == null) {
			logger.warnf("Der Absender fuer Bestellung-Emails ist nicht gesetzt.");
			return;
		}
		logger.info("Absender fuer Bestellung-Emails: " + mailAbsender);
	}
	
	public void onCreateAuftrag(@Observes @NeuerAuftrag Auftrag auftrag) {
		final Kunde kunde = auftrag.getKunde();
		final String mailEmpfaenger = kunde.getEmail();
		if (mailAbsender == null || mailEmpfaenger == null) {
			return;
		}
		final String vorname = kunde.getVorname() == null ? "" : kunde.getVorname();
		final String nameEmpfaenger = vorname + kunde.getNachname();
		
		final MimeMessage message = new MimeMessage(mailSession);

		try {
			// Absender setzen
			final InternetAddress absenderObj = new InternetAddress(mailAbsender, nameAbsender);
			message.setFrom(absenderObj);
			
			// Empfaenger setzen
			final InternetAddress empfaenger = new InternetAddress(mailEmpfaenger, nameEmpfaenger);
			message.setRecipient(RecipientType.TO, empfaenger);   // RecipientType: TO, CC, BCC

			// Subject setzen
			message.setSubject("Neue Bestellung Nr. " + auftrag.getId());
			
			// Text setzen mit MIME Type "text/plain"
			final StringBuilder sb = new StringBuilder("Neue Bestellung Nr. "
                                                       + auftrag.getId() + NEWLINE);
			for (Auftragsposition bp : auftrag.getAuftragspositionen()) {
				sb.append(bp.getAnzahl() + "\t" + bp.getArtikel().getBezeichnung() + NEWLINE);
			}
			final String text = sb.toString();
			//LOGGER.finest(text);
			message.setText(text);

			// Hohe Prioritaet einstellen
			//message.setHeader("Importance", "high");
			//message.setHeader("Priority", "urgent");
			//message.setHeader("X-Priority", "1");

			Transport.send(message);
		}
		catch (MessagingException | UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return;
		}
	}
}
