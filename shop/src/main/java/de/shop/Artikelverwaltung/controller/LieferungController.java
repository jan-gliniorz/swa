package de.shop.Artikelverwaltung.controller;

import static de.shop.Util.Constants.JSF_INDEX;
import static de.shop.Util.Messages.MessagesType.ARTIKELVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
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
import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Util.Client;
import de.shop.Util.Log;
import de.shop.Util.Messages;

/**
 * Dialogsteuerung fuer die LieferungService
 */
@Named("lic")
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)
@Log
public class LieferungController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	
	private static final String JSF_LIST_LIEFERUNG = "/artikelverwaltung/listLieferung";
	private static final String FLASH_LIEFERUNG = "lieferung";
	
	private static final String JSF_LIST_LIEFERUNGEN_ALL = "/artikelverwaltung/listLieferungenAll";
	private static final String FLASH_LIEFERUNGEN_ALL = "lieferungen";
	
	private static final String JSF_UPDATE_LIEFERUNG = "/artikelverwaltung/updateLieferung";
	
	private static final int MAX_AUTOCOMPLETE = 10;
	
	private static final String CLIENT_ID_LIEFERUNGID = "form:lieferungIdInput";
	private static final String MSG_KEY_LIEFERUNG_NOT_FOUND_BY_ID = "listLieferung.notFound";
	
	private static final String CLIENT_ID_ARTIKELID = "form:artikelIdInput";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_ID = "listArtikel.notFound";
	
	
	private Long id;
	private Long neueId;

	private Lieferung lieferung;
	private List<Lieferung> lieferungen;
	private Lieferung neueLieferung;
	
	private List<Lieferungsposition> lieferungspositionen;
	private List <Lieferungsposition> neueLieferungspositionen = new ArrayList<>();
	
	private Lieferungsposition neueLieferungsposition;
	
	private boolean geaendertLieferung;
	
	@Inject
	private LieferungService ls;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	@Inject
	@Client
	Locale locale;
	
	@Inject
	private Messages messages;
	
	@Inject
	@Push(topic = "updateLieferung")
	private transient Event<String> updateLieferungEvent;
	
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

	public String selectForUpdate(Lieferung lieferung) {
		if (lieferung == null) {
			return null;
		}
		return JSF_UPDATE_LIEFERUNG;
					
	}
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertLieferung) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertLieferung = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertLieferung = true;				
		}
	}
	
	@TransactionAttribute(REQUIRED)
	public String findLieferungById() {
		lieferung = ls.findLieferungById(id, LieferungService.FetchType.MIT_POSITIONEN, locale);
		return JSF_LIST_LIEFERUNG;
	}
	
	@TransactionAttribute(REQUIRED)
	public String findLieferungenAll() {
		lieferungen = ls.findLieferungenAll (LieferungService.FetchType.NUR_LIEFERUNG, LieferungService.OrderType.ID);
		flash.put(FLASH_LIEFERUNGEN_ALL, lieferungen);
		
		return  JSF_LIST_LIEFERUNGEN_ALL;
	}
	
	private String findLieferungByIdErrorMsg(String id) {
		messages.error(ARTIKELVERWALTUNG, MSG_KEY_LIEFERUNG_NOT_FOUND_BY_ID, CLIENT_ID_LIEFERUNGID, id);
		return null;
	}
	
	@TransactionAttribute(REQUIRED)
	public List<Lieferung> findLieferungByIdPrefix(String idPrefix) {
		List<Lieferung> lieferungPrefix = null;
		Long id = null; 
		try {
			id = Long.valueOf(idPrefix);
		}
		catch (NumberFormatException e) {
			findLieferungByIdErrorMsg(idPrefix);
			return null;
		}
		
		lieferungPrefix = ls.findLieferungByIdPrefix(id);
		if (lieferungPrefix == null || lieferungPrefix.isEmpty()) {
			// Keine Lieferung zu gegebenem ID-Praefix vorhanden
			findLieferungByIdErrorMsg(idPrefix);
			return null;
		}
		
		if (lieferungPrefix.size() > MAX_AUTOCOMPLETE) {
			return lieferungPrefix.subList(0, MAX_AUTOCOMPLETE);
		}
		return lieferungPrefix;
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
			// Keine Lieferung zu gegebenem ID-Praefix vorhanden
			findArtikelByIdErrorMsg(idPrefix);
			return null;
		}
		
		if (artikelPrefix.size() > MAX_AUTOCOMPLETE) {
			return artikelPrefix.subList(0, MAX_AUTOCOMPLETE);
		}
		return artikelPrefix;
	}
	
	@TransactionAttribute(REQUIRED)
	public String createLieferung() {
		
		neueLieferung = ls.createLieferung(neueLieferung, locale);

		// Aufbereitung fuer listLieferung.xhtml
//		id = neueLieferung.getId();
		
//		neueLieferung = ls.findLieferungById(id, FetchType.MIT_POSITIONEN, locale);  // zuruecksetzen
//		lieferung = neueLieferung;
		
		id = null;
		neueLieferung = null;
		lieferung = null;
		
		return JSF_INDEX;
	}
	
//	private String createLieferungErrorMsg(AbstractShopException e) {
//		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
//		if (exceptionClass.equals(LieferungInvalidIdException.class)) {
//			final LieferungInvalidIdException orig = (LieferungInvalidIdException) e;
//			messages.error(orig.getViolations(), null);
//		}
//		
//		return null;
//	}
	
	@TransactionAttribute(REQUIRED)
	public String updateLieferung() {
			
		LOGGER.tracef("Aktualisierte Lieferung: %s", lieferung);
		
		lieferung = ls.updateLieferung(lieferung, locale);

		// ValueChangeListener zuruecksetzen
		geaendertLieferung = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		id = null;
		lieferung = null;
		
		return JSF_INDEX;
	}
	
	public void addPos(){
		
		Lieferungsposition neueLieferungsposition = new Lieferungsposition();
		neueLieferungsposition.setArtikel(new Artikel());
		neueLieferung.addLieferungsposition(neueLieferungsposition);	
	}
	
	public void removePos(Lieferungsposition lieferungsposition) {
		
		neueLieferung.removeLieferungsposition(lieferungsposition);		
	}
	
	
	public void addUpdatePos(){	
		
		Lieferungsposition neueLieferungsposition = new Lieferungsposition();
		neueLieferungsposition.setArtikel(new Artikel());
		lieferung.addLieferungsposition(neueLieferungsposition);	
	}
	
	public void removeUpdatePos(Lieferungsposition lieferungsposition) {
		
		lieferung.removeLieferungsposition(lieferungsposition);		
	}
		
	
	public void createEmptyLieferung() {
		if (neueLieferung != null) {
			return;
		}
		neueLieferung = new Lieferung();
		neueLieferung.setLieferungspositionen(neueLieferungspositionen);	
	}
		
	@TransactionAttribute(REQUIRED)
	public String deleteLieferung(Lieferung lieferung) {

		
		ls.deleteLieferung(lieferung);
	
//		if(lieferungen!=null)
//			lieferungen.remove(lieferung);
//		
		this.lieferung=null;
		this.id=null;
		
		return JSF_INDEX;
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
