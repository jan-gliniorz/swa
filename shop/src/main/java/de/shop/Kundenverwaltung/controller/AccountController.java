package de.shop.Kundenverwaltung.controller;

import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

import de.shop.Auftragsverwaltung.service.AuftragService;
import de.shop.Auth.controller.AuthController;
import de.shop.Auth.controller.KundeLoggedIn;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Kundenverwaltung.service.KundeService.FetchType;
import de.shop.Util.Client;
import de.shop.Util.Messages;
import de.shop.Util.Transactional;
//import de.shop.Kundenverwaltung.service.InvalidKundeException;

/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("mac")
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)
public class AccountController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	
	private static final String JSF_KUNDENVERWALTUNG = "/kundenverwaltung/";
	
	@Inject
	private KundeService ks;
	
	@Inject
	private AuftragService as;
	
	@Inject
	private transient HttpServletRequest request;
	
	@Inject
	private AuthController auth;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Messages messages;
	
	@Inject
	@KundeLoggedIn
	private Kunde kunde;
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	@Override
	public String toString() {
		return "AccountController [kundeId="  + "]";
	}

	public Kunde getKunde() {
		return kunde;
	}
	
	public Date getAktuellesDatum() {
		final Date datum = new Date();
		return datum;
	}
	
	@Transactional
	public void preRenderOrdersView() {
		// Den eingeloggten Kunden mit seinen Auftraegen ermitteln
		kunde = ks.findKundenByKundennummer(kunde.getKundenNr(), FetchType.MIT_BESTELLUNGEN, locale);
	}
}
