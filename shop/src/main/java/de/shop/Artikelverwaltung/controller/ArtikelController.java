package de.shop.Artikelverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static de.shop.Util.Messages.MessagesType.ARTIKELVERWALTUNG;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import de.shop.Util.Messages;
import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.service.ArtikelService.OrderType;
import de.shop.Util.Client;
import de.shop.Util.Log;
import de.shop.Util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("arc")
@RequestScoped
@Log
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final String FLASH_ARTIKEL = "artikel";
	private static final int ANZAHL_LADENHUETER = 10;
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";
	
	private static final String CLIENT_ID_ARTIKEL_BEZEICHNUNG = "form:bezeichnung";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG = "listArtikel.notFound";
	
	private static final String CLIENT_ID_ARTIKELID = "form:artikelIdInput";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_ID = "listArtikel.notFound";

	private static final int MAX_AUTOCOMPLETE = 10;
	private String bezeichnung;
	
	private Long id;
	
	private List<Artikel> ladenhueter;

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


	public List<Artikel> getLadenhueter() {
		return ladenhueter;
	}

	@Transactional
	public String findArtikelByBezeichnung() {
		final List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL;
	}
	
	@Transactional
	public String findArtikelById() {
		
		final Artikel artikel = as.findArtikelByID(id, FetchType.NUR_Artikel, locale);
		
		List<Artikel> artikelList = new ArrayList<Artikel>();
		artikelList.add(artikel);
		
		flash.put(FLASH_ARTIKEL, artikelList);

		return JSF_LIST_ARTIKEL;
	}
	

//	@Transactional
//	public void loadLadenhueter() {
//		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
//	}
	
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
}
