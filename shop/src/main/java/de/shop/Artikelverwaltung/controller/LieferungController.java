package de.shop.Artikelverwaltung.controller;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
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
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Artikelverwaltung.service.LieferungService.OrderType;
import de.shop.Util.Client;
import de.shop.Util.Log;
import de.shop.Util.Transactional;
import de.shop.Artikelverwaltung.service.*;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("lic")
@RequestScoped
@Log
public class LieferungController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	
	private static final String JSF_LIST_LIEFERUNG = "/artikelverwaltung/listLieferung";
	private static final String FLASH_LIEFERUNG = "lieferungNurLieferung";
	
	private static final String JSF_VIEW_LIEFERUNG_MIT_POSITIONEN = "/artikelverwaltung/viewLieferung";
	private static final String FLASH_LIEFERUNG_MIT_POSITIONEN = "lieferungMitPositionen";
	
	private static final String JSF_VIEW_LIEFERUNGEN_ALL = "/artikelverwaltung/listLieferungenAll";
	private static final String FLASH_LIEFERUNGEN_ALL = "lieferungen";
	
	private Long id;

	private Lieferung lieferungNurLieferung;
	private Lieferung lieferungMitPositionen;
	private List<Lieferung> lieferungen;
	private Lieferung neueLieferung;
	
	private List<Lieferungsposition> lieferungspositionen;
	
	@Inject
	private LieferungService ls;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	@Inject
	@Client
	Locale locale;
	
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
	public String findLieferungByIdNurLieferung() {
		lieferungNurLieferung = ls.findLieferungById(id, LieferungService.FetchType.MIT_POSITIONEN, locale);
		flash.put(FLASH_LIEFERUNG_MIT_POSITIONEN, lieferungNurLieferung);
		
		return JSF_LIST_LIEFERUNG;
	}

	@Transactional
	public String findLieferungByIdMitPositionen() {
		lieferungMitPositionen = ls.findLieferungById(id, LieferungService.FetchType.MIT_POSITIONEN, locale);
		flash.put(FLASH_LIEFERUNG_MIT_POSITIONEN, lieferungMitPositionen);
		
		return JSF_VIEW_LIEFERUNG_MIT_POSITIONEN;
	}
	
	@Transactional
	public String findLieferungenAll() {
		lieferungen = ls.findLieferungenAll (LieferungService.FetchType.NUR_LIEFERUNG, LieferungService.OrderType.ID);
		flash.put(FLASH_LIEFERUNGEN_ALL, lieferungen);
		
		return  JSF_VIEW_LIEFERUNGEN_ALL;
	}
	
	@Transactional
	public String createLieferung() {

		List<Lieferungsposition> pos = new ArrayList<>();
		
		for (Lieferungsposition li : lieferungspositionen) {
			pos.add(li);
		}	
		neueLieferung.setLieferungspositionen(pos);
		
		neueLieferung = (Lieferung) ls.createLieferung(neueLieferung, locale);

		// Push-Event fuer Webbrowser
		neueLieferung.fire(String.valueOf(neueLieferung.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerPrivatkunde.getId();
		kunde = neuerPrivatkunde;
		neuerPrivatkunde = null;  // zuruecksetzen
		hobbies = null;
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	public Lieferung getLieferungNurLieferung() {
		return lieferungNurLieferung;
	}
	
	public Lieferung getLieferungMitPositionen() {
		return lieferungMitPositionen;
	}
	
	public List<Lieferung> getLieferungen() {
		return lieferungen;
	}
	
	public Lieferung getNeueLieferung() {
		return neueLieferung;
	}
}
