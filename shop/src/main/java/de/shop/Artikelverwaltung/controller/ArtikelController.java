package de.shop.Artikelverwaltung.controller;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
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
	private static final int ANZAHL_LADENHUETER = 5;
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";

	private String bezeichnung;
	
	private List<Artikel> ladenhueter;

	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	
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

//	@Transactional
//	public String findArtikelByBezeichnung() {
//		final List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
//		flash.put(FLASH_ARTIKEL, artikel);
//
//		return JSF_LIST_ARTIKEL;
//	}
	

//	@Transactional
//	public void loadLadenhueter() {
//		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
//	}
	
//	@Transactional
//	public String selectArtikel() {
//		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
//			return JSF_SELECT_ARTIKEL;
//		}
//		
//		final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
//		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
//		return JSF_SELECT_ARTIKEL;
//	}
}