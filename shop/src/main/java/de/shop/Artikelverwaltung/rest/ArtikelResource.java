package de.shop.Artikelverwaltung.rest;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

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

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LagerService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@Path("/artikel")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelService as;

	@Inject
	private LagerService ls;

	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	
	@GET
	@Wrapped(element = "artikel") 
	public Collection<Artikel> findArtikelAll(@Context UriInfo uriInfo) {
		Collection<Artikel> artikel = 
		as.findArtikelAll(ArtikelService.FetchType.NUR_Artikel, ArtikelService.OrderType.ID);
		for (Artikel a : artikel) {
			uriHelperArtikel.updateUriArtikel(a, uriInfo);
		}
		
		return artikel;
	} 
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id, 
								   @Context UriInfo uriInfo, 
								   @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Artikel artikel = as.findArtikelByID(id, 
												   ArtikelService.FetchType.NUR_Artikel, 
												   locale); 
		
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperArtikel.updateUriArtikel(artikel, uriInfo);
		return artikel;
	}

	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createArtikel(Artikel artikel, 
								  @Context UriInfo uriInfo, 
								  @Context HttpHeaders headers) {

		// ein neuer Artikel kann noch nicht im Lager vorhanden sein
		artikel.setLagerpositionen(null);
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		artikel = as.createArtikel(artikel, locale);
		
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		final Response response = Response.created(artikelUri).build();
		LOGGER.finest(artikelUri.toString());
		
		return response;
	}
	
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateArtikel(Artikel artikel, @Context HttpHeaders headers) {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Artikel artikelOrig = as.findArtikelByID(artikel.getId(), ArtikelService.FetchType.NUR_Artikel, locale);
		if (artikelOrig == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		
		LOGGER.log(FINEST, "Artikel vorher: %s", artikelOrig);
		artikelOrig.setValues(artikel);
		LOGGER.log(FINEST, "Artikel nachher: %s", artikelOrig);
		artikel = as.updateArtikel(artikelOrig, locale);

		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikelOrig.getId();
			throw new NotFoundException(msg);
		}
	}
	
	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteArtikel(@PathParam("id") Long artikelId, 
							  @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Artikel artikel = as.findArtikelByID(artikelId, ArtikelService.FetchType.NUR_Artikel, locale);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikelId;
			throw new NotFoundException(msg);
		}
		as.deleteArtikel(artikel);
	}
}
