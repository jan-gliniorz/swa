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
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;

import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.service.LagerService;
import de.shop.Artikelverwaltung.service.LieferungService;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Artikelverwaltung.rest.UriHelperLieferung;


import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@Path("/lagerpositionen")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class LagerpositionResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelService as;

	@Inject
	private LagerService ls;	
	
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
	public Lagerposition findLagerpositionById(@PathParam("id") Long id, FetchType fetch, Locale locale, @Context UriInfo uriInfo) {
		final Lagerposition lagerposition = ls.findLagerpositionById(id, locale);
		if (lagerposition == null) {
			final String msg = "Keine Lagerposition gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der Lagerposition gibt es keine Uris, -> es brauchen keine anpassungen vorgenommen werden
		return lagerposition;
	}
	
	@GET
	@Path("{artikelid:[1-9][0-9]*}")
	public List<Lagerposition> findLagerpositionenByArtikelId(@PathParam("artikelid") Long artikelId, FetchType fetch, Locale locale, @Context UriInfo uriInfo) {
		final Artikel artikel = as.findArtikelByID(artikelId, ArtikelService.FetchType.NUR_Artikel, locale);
		final List<Lagerposition> lagerpositionen = ls.findLagerpositionByArtikel(artikel, locale);
		if (lagerpositionen == null) {
			final String msg = "Keine Lagerpositionen zu Artikel mit der ID " + artikelId + "gefunden";
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der Lagerposition gibt es keine Uris, -> es brauchen keine anpassungen vorgenommen werden
		return lagerpositionen;
	}
}
