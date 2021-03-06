package de.shop.Auftragsverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Auftragsverwaltung.service.AuftragService;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.rest.UriHelperKunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Kundenverwaltung.service.KundeService.FetchType;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;
import de.shop.Util.Transactional;


@Path("/auftraege")
@Produces({ APPLICATION_JSON })
@Consumes
@RequestScoped
@Transactional
@Log
public class AuftragResource {
	@Inject
	private Logger logger;
	
	@Inject
	private AuftragService auftragService;
	
	@Inject
	private KundeService kundeService;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelperAuftrag uriHelperAuftrag;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@GET
	@Wrapped(element = "auftraege") 
	public Collection<Auftrag> findAuftraegeAll(@Context UriInfo uriInfo) {
		logger.debugf("findAll");
		final Collection<Auftrag> auftraege = auftragService.findAuftragAll();
		for (Auftrag a : auftraege) {
			uriHelperAuftrag.updateUriAuftrag(a, uriInfo);
		}
		
		return auftraege;
	} 
	
	/**
	 * Mit der URL /auftraege/{id} eine Bestellung ermitteln
	 * @param id ID des Auftrages
	 * @return Objekt mit Auftragsdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Auftrag findAuftragById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		logger.debugf("findById");
		final Auftrag auftrag = auftragService.findAuftragById(id);
		if (auftrag == null) {
			final String msg = "Kein Auftrag gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperAuftrag.updateUriAuftrag(auftrag, uriInfo);
		return auftrag;
	}
	
	@GET
	@Path("byKunde/{kundenr:[1-9][0-9]*}")
	public List<Auftrag> findAuftragByKundeNr(@PathParam("kundenr") Long id, @Context UriInfo uriInfo) {
		logger.debugf("findKdnr");
		final List<Auftrag> auftraege = auftragService.findAuftragByKundeId(id);
		if (auftraege == null) {
			final String msg = "Keine Auftraege gefunden zu Kunde mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		for (Auftrag auftrag : auftraege) {
			uriHelperAuftrag.updateUriAuftrag(auftrag, uriInfo);
		}
		
		return auftraege;
	}
	
	/**
	 * Mit der URL /auftraege einen neuen Auftrag anlegen
	 * @param auftrag der neue Auftrag
	 * @return Objekt mit Auftragsdaten, falls die ID vorhanden ist
	 */
	@POST
	@Consumes({ APPLICATION_JSON })
	@Produces
	public Response createAuftrag(Auftrag auftrag, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Schluessel des Kunden extrahieren
		final String kundeUriStr = auftrag.getKundeUri().toString();
		int startPos = kundeUriStr.lastIndexOf('/') + 1;
		final String kundeIdStr = kundeUriStr.substring(startPos);
		Long kundeId = null;
		try {
			kundeId = Long.valueOf(kundeIdStr);
		}
		catch (NumberFormatException e) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeIdStr, e);
		}
		
		// Kunde mit den vorhandenen ("alten") Bestellungen ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = kundeService.findKundenByKundennummer(kundeId, FetchType.MIT_BESTELLUNGEN, locale);
		// Implizites Nachladen innerhalb der Transaktion wuerde auch funktionieren
		// final AbstractKunde kunde = ks.findKundeById(kundeId);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeId);
		}
		
		// persistente Artikel ermitteln
		final Collection<Auftragsposition> auftragspositionen = auftrag.getAuftragspositionen();
		final List<Long> artikelIds = new ArrayList<>(auftragspositionen.size());
		for (Auftragsposition ap : auftragspositionen) {
			final String artikelUriStr = ap.getArtikelUri().toString();
			startPos = artikelUriStr.lastIndexOf('/') + 1;
			final String artikelIdStr = artikelUriStr.substring(startPos);
			Long artikelId = null;
			try {
				artikelId = Long.valueOf(artikelIdStr);
			}
			catch (NumberFormatException e) {
				// Ungueltige Artikel-ID: wird nicht beruecksichtigt
				continue;
			}
			artikelIds.add(artikelId);
		}
		
		if (artikelIds.isEmpty()) {
			// keine einzige gueltige Artikel-ID
			final StringBuilder sb = new StringBuilder("Keine Artikel vorhanden mit den IDs: ");
			for (Auftragsposition ap : auftragspositionen) {
				final String artikelUriStr = ap.getArtikelUri().toString();
				startPos = artikelUriStr.lastIndexOf('/') + 1;
				sb.append(artikelUriStr.substring(startPos));
				sb.append(" ");
			}
			throw new NotFoundException(sb.toString());
		}

		final Collection<Artikel> gefundeneArtikel = as.findArtikelByIDs(artikelIds, 
																	ArtikelService.FetchType.NUR_Artikel, 
																	locale);
		if (gefundeneArtikel.isEmpty()) {
			throw new NotFoundException("Keine Artikel vorhanden mit den IDs: " + artikelIds);
		}
		
		// Bestellpositionen haben URLs fuer persistente Artikel.
		// Diese persistenten Artikel wurden in einem DB-Zugriff ermittelt (s.o.)
		// Fuer jede Bestellposition wird der Artikel passend zur Artikel-URL bzw. Artikel-ID gesetzt.
		// Bestellpositionen mit nicht-gefundene Artikel werden eliminiert.
		int i = 0;
		final List<Auftragsposition> neueAuftragspositionen = new ArrayList<>(auftragspositionen.size());
		for (Auftragsposition ap : auftragspositionen) {
			// Artikel-ID der aktuellen Bestellposition (s.o.):
			// artikelIds haben gleiche Reihenfolge wie bestellpositionen
			final long artikelId = artikelIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel.getId().longValue() == artikelId) {
					// Der Artikel wurde gefunden
					ap.setArtikel(artikel);
					//Gesammtpreis der Auftragsposition berechnen. 
					//Der Preis, der eventuell �ber das JSON Objekt geliefert wird, muss nicht korrekt sein.
					ap.setPreis(artikel.getPreis().multiply(new BigDecimal(ap.getAnzahl())));
					neueAuftragspositionen.add(ap);
					break;					
				}
			}
		}
		auftrag.setAuftragspositionen(neueAuftragspositionen);
		
		// Die neue Bestellung mit den aktualisierten persistenten Artikeln abspeichern.
		// Die Bestellung darf dem Kunden noch nicht hinzugefuegt werden, weil dieser
		// sonst in einer Transaktion modifiziert werden wuerde.
		// Beim naechsten DB-Zugriff (auch lesend!) wuerde der EntityManager sonst
		// erstmal versuchen den Kunden-Datensatz in der DB zu modifizieren.
		// Dann wuerde aber der Kunde mit einer *transienten* Bestellung modifiziert werden,
		// was zwangslaeufig zu einer Inkonsistenz fuehrt!
		// Das ist die Konsequenz einer Transaktion (im Gegensatz zu den Action-Methoden von JSF!).
		auftrag = auftragService.createAuftrag(auftrag, kunde, locale);

		final URI auftragUri = uriHelperAuftrag.getUriAuftrag(auftrag, uriInfo);
		final Response response = Response.created(auftragUri).build();
		logger.debugf(auftragUri.toString());
		
		return response;
	}
}
