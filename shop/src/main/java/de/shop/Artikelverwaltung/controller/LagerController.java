package de.shop.Artikelverwaltung.controller;

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

import org.jboss.logging.Logger;
import org.richfaces.component.SortOrder;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.service.ArtikelService.OrderType;
import de.shop.Artikelverwaltung.service.LagerService;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Util.Client;
import de.shop.Util.Log;
import de.shop.Util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("la")
@RequestScoped
@Log
public class LagerController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_LAGER = "/artikelverwaltung/listLager";
	private static final String FLASH_LAGER = "lager";
	private static final String JSF_SELECT_LAGER = "/artikelverwaltung/selectLager";
	private static final String SESSION_VERFUEGBARE_LAGER = "verfuegbareLAGER";

	private static String bezeichnungfilter = "";
	
	private String bezeichnung;
	
	private List<Lagerposition> lagerpositionen;
	
	private Long id;
	
	private List<Artikel> ladenhueter;

	@Inject
	private LagerService la;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;
	
	@Inject
	@Client
	private Locale locale;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Lagerposition> getLagerposition() {
		return lagerpositionen;
	}	
	
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

	public String getBezeichnungFilter() {
		return bezeichnungfilter;
	}
	
	public void setBezeichnungFilter(String bezeichnungFilter) {
		this.bezeichnungfilter = bezeichnungFilter;
	}

	public List<Artikel> getLadenhueter() {
		return ladenhueter;
	}

//	@Transactional
//	public String findLagerposAll() {
//		final List<Lagerposition> lagerpos = la.findLagerpositionlAll();
//		flash.put(FLASH_LAGER, lagerpos);
//
//		return JSF_LIST_LAGER;
//	}
	
	@Transactional
	public String listLagerposAll() {
		lagerpositionen = la.findLagerpositionlAll();
		
		return JSF_LIST_LAGER;
	}
	
//	@Transactional
//	public String findLagerposById() {
//		
//		final Lagerposition lagerpos = la.findLagerpositionById(id, locale);
//		
//		List<Lagerposition> lagerposList = new ArrayList<Lagerposition>();
//		lagerposList.add(lagerpos);
//		
//		flash.put(FLASH_LAGER, lagerposList);
//
//		return JSF_LIST_LAGER;
//	}
	
	
	
//	@Transactional
//	public String selectLager() {
//		if (session.getAttribute(SESSION_VERFUEGBARE_LAGER) != null) {
//			return JSF_SELECT_LAGER;
//		}
//		final List<Lagerposition> alleLagerpos = la.findLagerpositionlAll();
//		session.setAttribute(SESSION_VERFUEGBARE_LAGER, alleLagerpos);
//		return JSF_SELECT_LAGER;
//	}
}
