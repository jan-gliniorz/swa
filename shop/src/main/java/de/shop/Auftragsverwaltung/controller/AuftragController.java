package de.shop.Auftragsverwaltung.controller;

import static de.shop.Util.Constants.JSF_DEFAULT_ERROR;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import de.shop.Auth.controller.AuthController;
import de.shop.Auth.controller.KundeLoggedIn;
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.service.AuftragService;
import de.shop.Auftragsverwaltung.service.AuftragValidationException;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Kundenverwaltung.service.KundeService.FetchType;
import de.shop.Util.Client;
import de.shop.Util.Log;
import de.shop.Util.Transactional;

@Named("auc")
@RequestScoped
@Log
public class AuftragController implements Serializable {
	private static final long serialVersionUID = -1790295502719370565L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private static final String JSF_VIEW_AUFTRAG = "/auftragsverwaltung/viewAuftrag";
	
	@Inject
	private Warenkorb warenkorb;
	
	@Inject
	private AuftragService as;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private AuthController auth;
	
	@Inject
	@KundeLoggedIn
	private Kunde kunde;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Flash flash;
	

	@Transactional
	public String bestellen() {
		auth.preserveLogin();
		
		if (warenkorb == null || warenkorb.getPositionen() == null || warenkorb.getPositionen().isEmpty()) {
			// Darf nicht passieren, wenn der Button zum Bestellen verfuegbar ist
			return JSF_DEFAULT_ERROR;
		}
		
		// Den eingeloggten Kunden mit seinen Auftraegen ermitteln, und dann den neuen Auftrag zu ergaenzen
		kunde = ks.findKundenByKundennummer(kunde.getKundenNr(), FetchType.MIT_BESTELLUNGEN, locale);
		
		// Aus dem Warenkorb nur Positionen mit Anzahl > 0
		final List<Auftragsposition> positionen = warenkorb.getPositionen();
		final List<Auftragsposition> neuePositionen = new ArrayList<>(positionen.size());
		for (Auftragsposition ap : positionen) {
			if (ap.getAnzahl() > 0) {
				neuePositionen.add(ap);
			}
		}
		
		// Warenkorb zuruecksetzen
		warenkorb.endConversation();
		
		// Neuen Auftrag mit neuen Auftragspositionen erstellen
		Auftrag auftrag = new Auftrag();
		auftrag.setAuftragspositionen(neuePositionen);
		LOGGER.tracef("Neuer Auftrag: %s\nAuftragspositionen: %s", auftrag, auftrag.getAuftragspositionen());
		
		// Auftrag mit VORHANDENEM Kunden verknuepfen:
		// dessen Auftraege muessen geladen sein, weil es eine bidirektionale Beziehung ist
		try {
			auftrag = as.createAuftrag(auftrag, kunde, locale);
		}
		catch (AuftragValidationException e) {
			// Validierungsfehler KOENNEN NICHT AUFTRETEN, da Attribute durch JSF validiert wurden
			// und in der Klasse Auftrag keine Validierungs-Methoden vorhanden sind
			throw new IllegalStateException(e);
		}
		
		// Auftrag im Flash speichern wegen anschliessendem Redirect
		flash.put("auftrag", auftrag);
		
		return JSF_VIEW_AUFTRAG;
	}
}
