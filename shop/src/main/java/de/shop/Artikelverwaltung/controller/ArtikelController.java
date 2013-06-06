package de.shop.Artikelverwaltung.controller;

import static de.shop.Util.Constants.JSF_INDEX;
import static de.shop.Util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.Util.Messages.MessagesType.ARTIKELVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.service.ArtikelService.OrderType;
import de.shop.Util.Client;
import de.shop.Util.Log;
import de.shop.Util.Messages;
import de.shop.Util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("arc")
@Stateful
@TransactionAttribute(SUPPORTS)
@SessionScoped
@Log
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String JSF_ARTIKELVERWALTUNG = "/artikelverwaltung/";
	private static final String JSF_LIST_ARTIKEL = JSF_ARTIKELVERWALTUNG + "listArtikel";
	private static final String FLASH_ARTIKEL = "artikel";
	private static final String JSF_UPDATE_ARTIKEL = JSF_ARTIKELVERWALTUNG + "updateArtikel";
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";
	private static final String JSF_UPDATE_ARTIKEL_FROM = JSF_ARTIKELVERWALTUNG + "updateArtikelForm";
	
	private static final String CLIENT_ID_ARTIKEL_BEZEICHNUNG = "form:bezeichnung";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG = "listArtikel.notFound";
	
	private static final String CLIENT_ID_ARTIKELID = "form:artikelIdInput";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_ID = "listArtikel.notFound";

	private static final int MAX_AUTOCOMPLETE = 10;
	private String bezeichnung;
	
	private boolean geaendertArtikel;
	
	private Long id;
	
	private Artikel neuerArtikel;
	private Artikel alterArtikel;
	
	private List<Artikel> artikel = Collections.emptyList();
	

	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Messages messages;
	
	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerArtikelEvent;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "ArtikelController [bezeichnung=" + bezeichnung + "]";
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public List<Artikel> getArtikel() {
		return artikel;
	}
	
	
	@Transactional
	public String findArtikelByBezeichnung() {
		final List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL;
	}
	
	
	@Transactional
	public String findArtikelByBezeichnungUpdate() {
		final List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_UPDATE_ARTIKEL;
	}
	
	@Transactional
	public String findArtikelById() {
		
		final Artikel artikel = as.findArtikelByID(id, FetchType.NUR_Artikel, locale);
		
		final List<Artikel> artikelList = new ArrayList<Artikel>();
		artikelList.add(artikel);
		
		flash.put(FLASH_ARTIKEL, artikelList);

		return JSF_LIST_ARTIKEL;
	}
	

//	@Transactional
//	public void loadLadenhueter() {
//		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
//	}
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}	
	
	@Transactional
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
			return JSF_SELECT_ARTIKEL;
		}
		
		final List<Artikel> alleArtikel = as.findArtikelAll(FetchType.NUR_Artikel, OrderType.KEINE);
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	private String findArtikelByIdErrorMsg(String id) {
		messages.error(ARTIKELVERWALTUNG, MSG_KEY_ARTIKEL_NOT_FOUND_BY_ID, CLIENT_ID_ARTIKELID, id);
		return null;
	}
	
	
	public String selectForUpdate(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		
		alterArtikel = ausgewaehlterArtikel;
		
		return JSF_UPDATE_ARTIKEL_FROM;
					
	}
	
	@TransactionAttribute(REQUIRED)
	public List<Artikel> findArtikelByIdPrefix(String idPrefix) {
		List<Artikel> artikelPrefix = null;
		Long id = null; 
		try {
			id = Long.valueOf(idPrefix);
		}
		catch (NumberFormatException e) {
			findArtikelByIdErrorMsg(idPrefix);
			return null;
		}
		
		artikelPrefix = as.findArtikelByIdPrefix(id);
		if (artikelPrefix == null || artikelPrefix.isEmpty()) {
			// Kein Kunde zu gegebenem ID-Praefix vorhanden
			findArtikelByIdErrorMsg(idPrefix);
			return null;
		}
		
		if (artikelPrefix.size() > MAX_AUTOCOMPLETE) {
			return artikelPrefix.subList(0, MAX_AUTOCOMPLETE);
		}
		return artikelPrefix;
	}
	
	@TransactionAttribute(REQUIRED)
	public List<String> findBezeichnungByPrefix(String bezPrefix) {
		
		final List<String> bezeichnung = as.findBezeichnungByPrefix(bezPrefix);
		if (bezeichnung.isEmpty()) {
			messages.error(ARTIKELVERWALTUNG, MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG, CLIENT_ID_ARTIKEL_BEZEICHNUNG);
			return bezeichnung;
		}

		if (bezeichnung.size() > MAX_AUTOCOMPLETE) {
			return bezeichnung.subList(0, MAX_AUTOCOMPLETE);
		}

		return bezeichnung;
	}
	
	
	@TransactionAttribute(REQUIRED)
	public String createArtikel() {
	
			neuerArtikel = (Artikel) as.createArtikel(neuerArtikel, locale);

		// Push-Event fuer Webbrowser
		neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		id = neuerArtikel.getId();
		alterArtikel = neuerArtikel;
		neuerArtikel = null;  // zuruecksetzen
		
		return JSF_LIST_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}

	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) {
			return;
		}

		neuerArtikel = new Artikel();

	}
	

	@TransactionAttribute(REQUIRED)
	public String update() {
		
		if (!geaendertArtikel || alterArtikel == null) {
			return JSF_INDEX;
		}
		

		alterArtikel = as.updateArtikel(alterArtikel, locale);
		
		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(alterArtikel.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertArtikel = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		id = alterArtikel.getId();
		
		return JSF_UPDATE_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}

	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}

	public void setNeuerArtikel(Artikel neuerArtikel) {
		this.neuerArtikel = neuerArtikel;
	}

	public Artikel getAlterArtikel() {
		return alterArtikel;
	}

	public void setAlterArtikel(Artikel alterArtikel) {
		this.alterArtikel = alterArtikel;
	}
	
	
}
