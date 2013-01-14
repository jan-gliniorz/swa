package de.shop.Artikelverwaltung.rest;

import static java.util.logging.Level.FINER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@Path("/lieferungspositionen")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class LieferungspositionResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	
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
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}

	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Lieferungsposition findLieferungspositionById(@PathParam("id") Long id, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final Lieferungsposition lieferungsposition = ls.findLieferungspositionById(id, locale);
		if (lieferungsposition == null) {
			final String msg = "Keine Lieferungsposition gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		uriHelperLieferungsposition.updateUriLieferungsposition(lieferungsposition, uriInfo);
		return lieferungsposition;
	}

	
	@GET
	@QueryParam("{lieferungid:[1-9][0-9]*}")
	public List<Lieferungsposition> findLieferungspositionByLieferungId(@QueryParam("lieferungid") Long id, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final List <Lieferungsposition> lieferungspositionen = ls.findLieferungspositionenByLieferungId(id, locale);
		
		if (lieferungspositionen == null) {
			final String msg = "Keine Lieferungspositionen gefunden fuer Lieferungs-ID " + id;
			throw new NotFoundException(msg);
		}
		
		for (Lieferungsposition lp : lieferungspositionen) {
			uriHelperLieferungsposition.updateUriLieferungsposition(lp, uriInfo);
		}
		
		return lieferungspositionen;
	}
}
