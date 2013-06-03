package de.shop.Artikelverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;

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
	private static final String FLASH_LIEFERUNG = "lieferung";
	
	private static final String JSF_LIST_LIEFERUNGEN_ALL = "/artikelverwaltung/listLieferungenAll";
	private static final String FLASH_LIEFERUNGEN_ALL = "lieferungen";
	
	private static final String JSF_VIEW_LIEFERUNG = "/artikelverwaltung/viewLieferung";
	
	private Long id;
	private Long neueId;

	private Lieferung lieferung;
	private List<Lieferung> lieferungen;
	private Lieferung neueLieferung;
	private Lieferung vorhLieferung;
	
	private List<Lieferungsposition> lieferungspositionen;
	private List<Lieferungsposition> neueLieferungspositionen;
	
	
	@Inject
	private LieferungService ls;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	@Inject
	@Client
	Locale locale;
	
	@Inject
	@Push(topic = "updateLieferung")
	private transient Event<String> updateLieferungEvent;
	
	@Inject
	@Push(topic = "createLieferung")
	private transient Event<String> neueLieferungEvent;
	
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

	@Transactional
	public String findLieferungById() {
		lieferung = ls.findLieferungById(id, LieferungService.FetchType.MIT_POSITIONEN, locale);
		flash.put(FLASH_LIEFERUNG, lieferung);	
		return JSF_LIST_LIEFERUNG;
	}
	
	@Transactional
	public String findLieferungenAll() {
		lieferungen = ls.findLieferungenAll (LieferungService.FetchType.NUR_LIEFERUNG, LieferungService.OrderType.ID);
		flash.put(FLASH_LIEFERUNGEN_ALL, lieferungen);
		
		return  JSF_LIST_LIEFERUNGEN_ALL;
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
		neueLieferungEvent.fire(String.valueOf(neueLieferung.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		neueId = neueLieferung.getId();
		lieferung = neueLieferung;
		neueLieferung = null;  // zuruecksetzen
		lieferungspositionen = null;
		
		return JSF_VIEW_LIEFERUNG;
	}
	
	@TransactionAttribute(REQUIRED)
	public String updateLieferung() {
	
			final List<Lieferungsposition> neuLieferungspositionen = new ArrayList<>();
			
			for (Lieferungsposition li : neuLieferungspositionen) {
				neuLieferungspositionen.add((Lieferungsposition) lieferung.getLieferungspositionen());
			}
			
			vorhLieferung.setLieferungspositionen(neuLieferungspositionen);
			
		LOGGER.tracef("Aktualisierte Lieferung: %s", lieferung);
		
		lieferung = ls.updateLieferung(vorhLieferung, locale);


		// Push-Event fuer Webbrowser
		updateLieferungEvent.fire(String.valueOf(lieferung.getId()));

		// Aufbereitung fuer viewKunde.xhtml
		id = lieferung.getId();
		
		return JSF_LIST_LIEFERUNG;
	}
	
	public void createEmptyLieferung() {
		if (neueLieferung != null) {
			return;
		}

		neueLieferung = new Lieferung();
		final List <Lieferungsposition> lieferungspositionen = new ArrayList<>();
		neueLieferung.setLieferungspositionen(lieferungspositionen);
		
		final int anzahlLieferungspositionen = lieferungspositionen.size();
		neueLieferungspositionen = new ArrayList<>(anzahlLieferungspositionen);
	}
		
	public Lieferung getLieferung() {
		return lieferung;
	}
	
	public List<Lieferung> getLieferungen() {
		return lieferungen;
	}
	
	public Lieferung getNeueLieferung() {
		return neueLieferung;
	}
	
	public List<Lieferungsposition> getLieferungspositionen() {
		return lieferungspositionen;
	}
	
	public void setLieferungspositionen(List<Lieferungsposition> lieferungspositionen) {
		this.lieferungspositionen = lieferungspositionen;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getneueId() {
		return neueId;
	}
}
