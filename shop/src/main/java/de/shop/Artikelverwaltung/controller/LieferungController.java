package de.shop.Artikelverwaltung.controller;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Artikelverwaltung.service.LieferungService.OrderType;
import de.shop.Util.Log;
import de.shop.Util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("lic")
@RequestScoped
@Log
public class LieferungController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	
	private static final String JSF_LIST_LIEFERUNG_NUR_LIEFERUNG = "/artikelverwaltung/listLieferung";
	private static final String FLASH_LIEFERUNG_NUR_LIEFERUNG = "lieferung";
	private static final String JSF_VIEW_LIEFERUNG_MIT_POSITIONEN = "/artikelverwaltung/viewLieferung";
	private static final String FLASH_LIEFERUNG_MIT_POSITIONEN = "lieferung";
	private static final String JSF_LIST_LIEFERUNGEN_ALL = "/artikelverwaltung/listLieferungenAll";
	private static final String FLASH_LIEFERUNGEN = "lieferungen";
	
	private Long id;

	private Lieferung lieferung;
	private Lieferung lieferungMitPositionen;
	private List<Lieferung> lieferungen;
	
	@Inject
	private LieferungService ls;
	
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
		return "LieferungController [id=" + id + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@Transactional
	public String findLieferungByIdNurLieferung(FetchType fetch, Locale locale) {
		lieferung = ls.findLieferungById(id, LieferungService.FetchType.NUR_LIEFERUNG, locale);
		flash.put(FLASH_LIEFERUNG_NUR_LIEFERUNG, lieferung);
		
		return JSF_LIST_LIEFERUNG_NUR_LIEFERUNG;
	}
	
	@Transactional
	public String findLieferungByIdMitPositionen(FetchType fetch, Locale locale) {
		lieferungMitPositionen = ls.findLieferungById(id, LieferungService.FetchType.MIT_POSITIONEN, locale);
		flash.put(FLASH_LIEFERUNG_MIT_POSITIONEN, lieferungMitPositionen);
		
		return JSF_VIEW_LIEFERUNG_MIT_POSITIONEN;
	}
	
	@Transactional
	public String findLieferungenAll(FetchType fetch, OrderType order) {
		lieferungen = ls.findLieferungenAll (fetch, order);
		flash.put(FLASH_LIEFERUNGEN, lieferungen);
		
		return JSF_LIST_LIEFERUNGEN_ALL;
	}
	
	public Lieferung getLieferung() {
		return lieferung;
	}
	public Lieferung getLieferungMitPositionen() {
		return lieferungMitPositionen;
	}
	public List<Lieferung> getLieferungen() {
		return lieferungen;
	}
}
