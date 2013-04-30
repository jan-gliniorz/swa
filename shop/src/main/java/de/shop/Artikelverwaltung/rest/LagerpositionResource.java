
package de.shop.Artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LagerService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;
import de.shop.Util.RestLocaleHelper;
import de.shop.Util.Transactional;


@Path("/lagerpositionen")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Log
@Transactional
public class LagerpositionResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelService as;

	@Inject
	private LagerService ls;
	
	@Inject
	private UriHelperLagerposition uriLagerpos;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s {0} wird geloescht", this);
	}	
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Lagerposition findLagerpositionById(@PathParam("id") Long id, 
												@Context HttpHeaders header, 
												@Context UriInfo uriInfo) {
		final Locale locale =  RestLocaleHelper.getLocalFromHttpHeaders(header);
		
		final Lagerposition lagerposition = ls.findLagerpositionById(id, locale);
		if (lagerposition == null) {
			final String msg = "Keine Lagerposition gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		uriLagerpos.updateUriLagerposition(lagerposition, uriInfo);
	
		return lagerposition;
	}
	
//	@GET
//	@Path("ByLagerId/{lagerid:[1-9][0-9]*}")
//	public Lagerposition findLagerpositionByLagerId(@PathParam("lagerid") Long id,
//													@Context HttpHeaders headers,
//													@Context UriInfo uriInfo) {
//		
//		Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
//		
//		final Lagerposition lagerposition = ls.findLagerpositionByLagerId(id, locale);
//		if (lagerposition == null) {
//			String msg = "Lager nicht vorhanden mit der ID: " + id;
//			throw new NotFoundException(msg);
//		}
//			
//		uriLagerpos.updateUriLagerposition(lagerposition, uriInfo);
//		
//		return lagerposition;
//	}

	@GET
	@QueryParam("{artikelid:[1-9][0-9]*}")
	public List<Lagerposition> findLagerpositionenByArtikelId(@QueryParam("artikelid") Long artikelId, 
																@Context HttpHeaders header, 
																@Context UriInfo uriInfo) {
		final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(header);
		
		final Artikel artikel = as.findArtikelByID(artikelId, ArtikelService.FetchType.NUR_Artikel, locale);
		final List<Lagerposition> lagerpositionen = ls.findLagerpositionByArtikel(artikel, locale);
		if (lagerpositionen == null) {
			final String msg = "Keine Lagerpositionen zu Artikel mit der ID " + artikelId + "gefunden";
			throw new NotFoundException(msg);
		}
		
		for (Lagerposition l : lagerpositionen) {
			uriLagerpos.updateUriLagerposition(l, uriInfo);
		}

		return lagerpositionen;
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateLagerposition(Lagerposition lagerposition, 
										@Context UriInfo uriInfo, 
										@Context HttpHeaders headers) { 
	
		final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
		
		uriLagerpos.updateReferenceLagerposition(lagerposition, headers);
		
		Lagerposition lagerposOrig = ls.findLagerpositionById(lagerposition.getId(), locale);
		if (lagerposOrig == null) {
			final String msg = "keine Lagerposition gefunden mit der ID:" + lagerposition.getId();
			throw new NotFoundException(msg);
		}
		
		LOGGER.debugf("Artikel vorher: %s", lagerposOrig);
		lagerposOrig.setValues(lagerposition);
		LOGGER.debugf("Artikel nachher: %s", lagerposOrig);
		lagerposition = ls.updateLagerposition(lagerposOrig, locale);
		
		if (lagerposition == null) {
			final String msg = "keine Lagerposition gefunden mit der ID:" + lagerposOrig.getId();
			throw new NotFoundException(msg);
		}
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createLagerposition(Lagerposition lagerposition,
										@Context UriInfo uriInfo,
										@Context HttpHeaders headers) {
		// ein neuer Artikel kann noch nicht im Lager vorhanden sein
		final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
		
		uriLagerpos.updateReferenceLagerposition(lagerposition, headers);
		
		lagerposition = ls.createLagerposition(lagerposition, locale);

		final URI lagerpositionUri = uriLagerpos.getUriLagerposition(lagerposition, uriInfo);
		final Response response = Response.created(lagerpositionUri).build();
		
		return response;		
	}
	
	
	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteLagerposition(@PathParam("id") Long lagerpositionId, 
									@Context HttpHeaders headers) {
		final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
		final Lagerposition lagerposition = ls.findLagerpositionById(lagerpositionId, locale);
		if (lagerposition == null) {
			final String msg = "Keine Lagerposition gefunden mit der ID " + lagerpositionId;
			throw new NotFoundException(msg);
		}
		ls.deleteLagerposition(lagerposition);
	}	
}
