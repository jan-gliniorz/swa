package de.shop.Artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

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

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;
import de.shop.Util.Transactional;


@Path("/lieferungspositionen")
@Produces({ APPLICATION_JSON })
@Consumes
@Transactional
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
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}

	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Lieferungsposition findLieferungspositionById(
							  @PathParam("id") Long id, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
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
	public List<Lieferungsposition> findLieferungspositionByLieferungId(
		                        @QueryParam("lieferungid") Long id, 
		                        @Context UriInfo uriInfo, 
								@Context HttpHeaders headers) {
		
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
