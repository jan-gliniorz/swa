package de.shop.Auftragsverwaltung.controller;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Util.Client;
import de.shop.Util.Log;

@Named("wk")
@ConversationScoped
@Log
public class Warenkorb implements Serializable {
	private static final long serialVersionUID = -1981070683990640854L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String JSF_VIEW_WARENKORB = "/auftragsverwaltung/viewWarenkorb?init=true";
	private static final int TIMEOUT = 5;
	
	private final List<Auftragsposition> positionen = new ArrayList<Auftragsposition>();;
	private Long artikelId;  // fuer selectArtikel.xhtml
	
	@Inject
	private transient Conversation conversation;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private ArtikelService as;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public List<Auftragsposition> getPositionen() {
		return positionen;
	}
		
	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	@Override
	public String toString() {
		return "Warenkorb " + positionen;
	}
	
	/**
	 */
	public String add(Artikel artikel) {
		beginConversation();
		
		for (Auftragsposition ap : positionen) {
			if (ap.getArtikel().equals(artikel)) {
				// bereits im Warenkorb
				final int vorhandeneAnzahl = ap.getAnzahl();
				ap.setAnzahl((vorhandeneAnzahl + 1));
				return JSF_VIEW_WARENKORB;
			}
		}
		
		final Auftragsposition neu = new Auftragsposition(artikel, 1);
		positionen.add(neu);
		return JSF_VIEW_WARENKORB;
	}
	
	/**
	 */
	public String add() {
		final Artikel artikel = as.findArtikelByID(artikelId, FetchType.NUR_Artikel, locale);
		if (artikel == null) {
			return null;
		}
		
		final String outcome = add(artikel);
		artikelId = null;
		return outcome;
	}
	
	/**
	 */
	public void beginConversation() {
		if (!conversation.isTransient()) {
			return;
		}
		conversation.begin();
		conversation.setTimeout(MINUTES.toMillis(TIMEOUT));
		LOGGER.trace("Conversation beginnt");
	}
	
	/**
	 */
	public void endConversation() {
		conversation.end();
		LOGGER.trace("Conversation beendet");
	}
	
	/**
	 */
	public void remove(Auftragsposition bestellposition) {
		positionen.remove(bestellposition);
		if (positionen.isEmpty()) {
			endConversation();
		}
	}
}
