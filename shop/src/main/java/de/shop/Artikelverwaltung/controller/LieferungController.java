package de.shop.Artikelverwaltung.controller;

import static de.shop.Util.Constants.JSF_INDEX;
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

import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.LieferungInvalidIdException;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Util.AbstractShopException;
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
	
	private Long id;
	private Long neueId;

	private Lieferung lieferung;
	private List<Lieferung> lieferungen;
	private Lieferung neueLieferung;
	
	private List<Lieferungsposition> lieferungspositionen;
	
	private Lieferungsposition neueLieferungsposition;
	
	private boolean geaendertLieferung;
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
	private Messages messages;
	
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
		flash.put(FLASH_LIEFERUNG, lieferung);	
		return JSF_LIST_LIEFERUNG;
	}
	
	@TransactionAttribute(REQUIRED)
	public String findLieferungenAll() {
		lieferungen = ls.findLieferungenAll (LieferungService.FetchType.NUR_LIEFERUNG, LieferungService.OrderType.ID);
		flash.put(FLASH_LIEFERUNGEN_ALL, lieferungen);
		
		return  JSF_LIST_LIEFERUNGEN_ALL;
	}
	
	@TransactionAttribute(REQUIRED)
	public String createKunde() {
		
		try {
			neueLieferung = (Lieferung) ls.createLieferung(neueLieferung, locale);
		}
		catch (LieferungInvalidIdException e) {
			final String outcome = createLieferungErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neueLieferungEvent.fire(String.valueOf(neueLieferung.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		id = neueLieferung.getId();
		lieferung = neueLieferung;
		neueLieferung = null;  // zuruecksetzen
		
		return JSF_LIST_LIEFERUNGEN_ALL;
	}
	
	private String createLieferungErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		if (exceptionClass.equals(LieferungInvalidIdException.class)) {
			final LieferungInvalidIdException orig = (LieferungInvalidIdException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}
	
	@TransactionAttribute(REQUIRED)
	public String updateLieferung() {

		if (!geaendertLieferung || lieferung == null) {
			return JSF_INDEX;
		}
			
		LOGGER.tracef("Aktualisierte Lieferung: %s", lieferung);
		
		lieferung = ls.updateLieferung(lieferung, locale);

		// Push-Event fuer Webbrowser
		updateLieferungEvent.fire(String.valueOf(lieferung.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertLieferung = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		id = lieferung.getId();
		
		return JSF_LIST_LIEFERUNGEN_ALL;
	}
	
	@TransactionAttribute(REQUIRED)
	public void addPos(Lieferung lieferung){
		
	lieferung.addLieferungsposition(neueLieferungsposition);
	
	}
	
	public void createEmptyLieferung() {
		if (neueLieferung != null) {
			return;
		}

		neueLieferung = new Lieferung();
		final List <Lieferungsposition> lieferungspositionen = new ArrayList<>();
		neueLieferung.setLieferungspositionen(lieferungspositionen);	
	}
		
	@TransactionAttribute(REQUIRED)
	public String deleteLieferung(Lieferung lieferung) {

		ls.deleteLieferung(lieferung);
		
		lieferungen.remove(lieferung);
		return null;
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
