package de.shop.Artikelverwaltung.rest;

import static java.util.logging.Level.FINER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;

import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Artikelverwaltung.rest.UriHelperLieferung;


import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@Path("/lieferungen")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class LieferungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private LieferungService ls;

	@Inject
	private ArtikelService as;

	@Inject
	private UriHelperLieferung uriHelperLieferung;
	
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	
	@GET
	@Wrapped(element ="lieferungen") 
	public Collection<Lieferung>findLieferungenAll() {
	Collection<Lieferung> lieferungen = ls.findLieferungenAll(null, null);
	return lieferungen;
	} 
	
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Lieferung findLieferungById(@PathParam("id") Long id, FetchType fetch, Locale locale, @Context UriInfo uriInfo) {
		final Lieferung lieferung = ls.findLieferungById(id, fetch, locale);
		if (lieferung == null) {
			final String msg = "Keine Lieferung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperLieferung.updateUriLieferung(lieferung, uriInfo);
		return lieferung;
	}

	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createLieferung(Lieferung lieferung, @Context UriInfo uriInfo, @Context HttpHeaders headers) {

		// persistente Artikel ermitteln
		Collection<Lieferungsposition> lieferungspositionen = lieferung.getLieferungsposition();
		List<Long> artikelIds = new ArrayList<>(lieferungspositionen.size());
		for (Lieferungsposition lp : lieferungspositionen) {
			final String artikelUriStr = lp.getArtikelUri().toString();
			int startPos = artikelUriStr.lastIndexOf('/') + 1;
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
			final StringBuilder sb = new StringBuilder("Keine Lieferungspositionen vorhanden mit den IDs: ");
			for (Lieferungsposition lp : lieferungspositionen) {
				final String artikelUriStr = lp.getArtikelUri().toString();
				int startPos = artikelUriStr.lastIndexOf('/') + 1;
				sb.append(artikelUriStr.substring(startPos));
				sb.append(" ");
			}
			throw new NotFoundException(sb.toString());
		}

		Collection<Artikel> gefundeneArtikel = as.findArtikelByID(artikelId);
		if (gefundeneArtikel.isEmpty()) {
			throw new NotFoundException("Keine Artikel vorhanden mit den IDs: " + artikelIds);
		}

		int i = 0;
		final List<Lieferungsposition> neueLieferungspositionen = new ArrayList<>(lieferungspositionen.size());
		for (Lieferungsposition lp : lieferungspositionen) {
			// Artikel-ID der aktuellen Bestellposition (s.o.):
			// artikelIds haben gleiche Reihenfolge wie bestellpositionen
			final long artikelId = artikelIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel.getId().longValue() == artikelId) {
					// Der Artikel wurde gefunden
					lp.setArtikel(artikel);
					neueLieferungspositionen.add(lp);
					break;					
				}
			}
		}
		lieferung.setLieferungsposition(neueLieferungspositionen);

		lieferung = ls.createLieferung(lieferung, locale);

		final URI lieferungUri = uriHelperLieferung.getUriLieferung(lieferung, uriInfo);
		final Response response = Response.created(lieferungUri).build();
		LOGGER.finest(lieferungUri.toString());
		
		return response;
	}
}
