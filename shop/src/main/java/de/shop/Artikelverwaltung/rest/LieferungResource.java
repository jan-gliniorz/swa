package de.shop.Artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;





import java.lang.invoke.MethodHandles;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Util.NotFoundException;
import de.shop.Util.Log;
import de.shop.Util.Transactional;

@Path("/lieferungen")
@Produces({ APPLICATION_JSON })
@Transactional
@Consumes
@RequestScoped
@Log
public class LieferungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String VERSION = "1.0";
	
	@Inject
	private LieferungService ls;

	@Inject
	private ArtikelService as;

	@Inject
	private UriHelperLieferung uriHelperLieferung;

	@Inject
	private UriHelperLieferungsposition uriHelperLieferungsposition;
	
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	@GET
	@Produces(APPLICATION_JSON)
	@Path("version")
	public String getVersion() {
		return VERSION;
	}
	
	@GET
	@Wrapped(element = "lieferungen") 
	public Collection<Lieferung> findLieferungenAll(@Context UriInfo uriInfo) {
		LOGGER.debugf("findAll");
		final Collection<Lieferung> lieferungen = ls.findLieferungenAll(
										  LieferungService.FetchType.NUR_LIEFERUNG, LieferungService.OrderType.ID);
		for (Lieferung l : lieferungen) {
			uriHelperLieferung.updateUriLieferung(l, uriInfo);
		}
		
		return lieferungen;
	} 
	
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Lieferung findLieferungById(
					 @PathParam("id") Long id, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final Lieferung lieferung = ls.findLieferungById(id, LieferungService.FetchType.MIT_POSITIONEN, locale);
		if (lieferung == null) {
			final String msg = "Keine Lieferung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		uriHelperLieferung.updateUriLieferung(lieferung, uriInfo);
		return lieferung;
	}
	
	
	@POST
	@Consumes({ APPLICATION_JSON })
	@Produces
	public Response createLieferung(Lieferung lieferung, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final Collection<Lieferungsposition> lieferungspositionen = lieferung.getLieferungspositionen();
		
		LOGGER.debugf("Lieferungspositionen: " + lieferungspositionen);
		
		final List<Long> artikelIds = new ArrayList<>(lieferungspositionen.size());
		
		for (Lieferungsposition lp : lieferungspositionen) {

			final String artikelUriStr = lp.getArtikelUri().toString();
			
			LOGGER.debugf("ArtikelUri: " + artikelUriStr);
			
			final int startPos = artikelUriStr.lastIndexOf('/') + 1;
			final String artikelIdStr = artikelUriStr.substring(startPos);
			
			Long artikelId = null;
			
			try {
				artikelId = Long.valueOf(artikelIdStr);
			}
			catch (NumberFormatException e) {
				// Ungueltige Artikel-ID: wird nicht beruecksichtigt
				continue;
			}
			
			LOGGER.debugf("Artikel: " + artikelId);
			
			artikelIds.add(artikelId);
		}
		
		if (artikelIds.isEmpty()) {
			// keine einzige gueltige Artikel-ID
			final StringBuilder sb = new StringBuilder("Keine Artikel vorhanden mit den IDs: ");
			for (Lieferungsposition lp : lieferungspositionen) {
				final String artikelUriStr = lp.getArtikelUri().toString();
				final int startPos = artikelUriStr.lastIndexOf('/') + 1;
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

		int i = 0;
		final List<Lieferungsposition> neueLieferungspositionen = new ArrayList<>(lieferungspositionen.size());
		for (Lieferungsposition lp : lieferungspositionen) {

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
		lieferung.setLieferungspositionen(neueLieferungspositionen);

		lieferung = ls.createLieferung(lieferung, locale);

		final URI lieferungUri = uriHelperLieferung.getUriLieferung(lieferung, uriInfo);
		final Response response = Response.created(lieferungUri).build();
		LOGGER.debugf(lieferungUri.toString());
		
		return response;
	}
	
	@PUT
	@Consumes({ APPLICATION_JSON })
	@Produces
	public void updateLieferung(Lieferung lieferung, @Context UriInfo uriInfo, @Context HttpHeaders headers)
	{
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final Lieferung vorhLieferung = ls.findLieferungById(lieferung.getId(), 
								  LieferungService.FetchType.NUR_LIEFERUNG, locale);
		
		if (vorhLieferung == null) {
			final String msg = "Keine Lieferung gefunden mit der ID " + lieferung.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Kunde vorher: %s", vorhLieferung);
			
		// Daten der vorhandenen Lieferung überschreiben
		vorhLieferung.setValues(lieferung);
		LOGGER.debugf("Kunde nachher: %s", vorhLieferung);
				
		// Update durchfuehren		
		lieferung = ls.updateLieferung(vorhLieferung, locale);
		if (lieferung == null) {
			final String msg = "Keine Lieferung gefunden mit der ID " + vorhLieferung.getId();
			throw new NotFoundException(msg);
		}
	}
	
	@Path("{id:[1-9][0-9]*}")
	@DELETE
	@Produces
	public void deleteLieferung(@PathParam("id") Long id, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Lieferung lieferung = ls.findLieferungById(id, FetchType.NUR_LIEFERUNG, locale);
		ls.deleteLieferung(lieferung);
	}
}
