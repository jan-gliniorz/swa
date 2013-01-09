package de.shop.Artikelverwaltung.rest;

import static java.util.logging.Level.FINER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.service.LagerService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@Path("/lager")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class LagerResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	LagerService ls;
	
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
	public Lager findLagerById(@PathParam("id") Long id, FetchType fetch, Locale locale, @Context UriInfo uriInfo) {
		final Lager lager = ls.findLagerById(id, locale);
		if (lager == null) {
			final String msg = "Keine Lager gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der Lagerposition gibt es keine Uris, -> es brauchen keine anpassungen vorgenommen werden
		return lager;
	}
}
