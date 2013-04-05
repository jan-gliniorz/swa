package de.shop.Kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.rest.UriHelperAuftrag;
import de.shop.Auftragsverwaltung.service.AuftragService;
import de.shop.Kundenverwaltung.domain.Adresse;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Kundenverwaltung.service.KundeService.FetchType;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;
import de.shop.Util.Transactional;

@Path("/kunden")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Transactional
@Log
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String VERSION = "1.0";
	
	@Inject
	private KundeService ks;
	
	@Inject
	private AuftragService as;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@Inject
	private UriHelperAuftrag uriHelperAuftrag;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean =%d wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean =%d wird geloescht", this);
	}
	
	@GET
	@Produces(TEXT_PLAIN)
	@Path("version")
	public String getVersion() {
		return VERSION;
	}
	
	/**
	 * Mit der URL /kunden/{id} einen Kunden ermitteln
	 * @param id ID des Kunden
	 * @return Objekt mit Kundendaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
//	@Formatted    // XML formatieren, d.h. Einruecken und Zeilenumbruch
	public Kunde findKundenByKundennummer(@PathParam("id") Long kundennummer,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = ks.findKundenByKundennummer(kundennummer, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der kundennummer " + kundennummer;
			throw new NotFoundException(msg);
		}
	
		// URLs innerhalb des gefundenen Kunden anpassen
		uriHelperKunde.updateUriKunde(kunde, uriInfo);

		return kunde;
	}
	
	/**
	 * Mit der URL /kunden werden alle Kunden ermittelt oder
	 * mit kundenverwaltung/kunden?nachname=... diejenigen mit einem bestimmten Nachnamen.
	 * @return Collection mit den gefundenen Kundendaten
	 */
	@GET
	public Collection<Kunde> findKundenByNachname(@QueryParam("nachname") @DefaultValue("") String nachname,
			                                              @Context UriInfo uriInfo,
			                                              @Context HttpHeaders headers) {
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				final String msg = "Keine Kunden vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			final List<Locale> locales = headers.getAcceptableLanguages();
			final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, locale);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname " + nachname;
				throw new NotFoundException(msg);
			}
		}
		
		// URLs innerhalb der gefundenen Kunden anpassen
		for (Kunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}
		
		return kunden;
	}
	
	/**
	 * Mit der URL /kunden/{id}/bestellungen die Auftraege zu eine Kunden ermitteln
	 * @param kundeId ID des Kunden
	 * @return Objekt mit Auftragssdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/auftraege")
	public Collection<Auftrag> findAuftragById(@PathParam("id") Long kundennummer,  @Context UriInfo uriInfo) {
		final Collection<Auftrag> auftraege = as.findAuftragByKundeId(kundennummer);
		if (auftraege.isEmpty()) {
			final String msg = "Kein Kunde gefunden mit der Kundennummer" + kundennummer;
			throw new NotFoundException(msg);
		}
		
		// URLs innerhalb der gefundenen Auftr�ge anpassen
		for (Auftrag auftrag : auftraege) {
			uriHelperAuftrag.updateUriAuftrag(auftrag, uriInfo);
		}
		return auftraege;
	}

	/**
	 * Mit der URL /kunden einen Kunden per POST anlegen.
	 * @param kunde neuer Kunde
	 * @return Response-Objekt mit URL des neuen Kunden
	 */
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
		}
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		kunde = ks.createKunde(kunde, locale);
		LOGGER.debugf("Kunde: =%d", kunde);
		
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	/**
	 * Mit der URL /kunden einen Kunden per PUT aktualisieren
	 * @param kunde zu aktualisierende Daten des Kunden
	 */
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Kunden ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Kunde origKunde = ks.findKundenByKundennummer(kunde.getKundenNr(), FetchType.NUR_KUNDE, locale);
		if (origKunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + kunde.getKundenNr();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Kunde vorher: =%s", origKunde);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origKunde.setValues(kunde);
		LOGGER.debugf("Kunde nachher: =%s", origKunde);
		
		// Update durchfuehren
		kunde = ks.updateKunde(origKunde, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getKundenNr();
			throw new NotFoundException(msg);
		}
	}
	
	/**
	 * Mit der URL /kunden{id} einen Kunden per DELETE l&ouml;schen
	 * @param kundeId des zu l&ouml;schenden Kunden
	 */
	@Path("{kundennummer:[0-9]+}")
	@DELETE
	@Produces
	public void deleteKunde(@PathParam("kundennummer") Long kundennummer, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = ks.findKundenByKundennummer(kundennummer, FetchType.NUR_KUNDE, locale);
		ks.deleteKunde(kunde);
	}
}
