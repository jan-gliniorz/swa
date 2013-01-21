package de.shop.Auftragsverwaltung.rest;

import static java.util.logging.Level.FINER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
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

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.Auftragsverwaltung.domain.Rechnung;
import de.shop.Auftragsverwaltung.service.AuftragService;
import de.shop.Auftragsverwaltung.service.RechnungService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@Path("/rechnungen")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class RechnungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private AuftragService auftragService;
	
	@Inject
	private RechnungService rechnungService;
	
	@Inject
	private UriHelperAuftrag uriHelperAuftrag;
	
	@Inject
	private UriHelperRechnung uriHelperRechnung;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@GET
	@Wrapped(element = "rechnungen") 
	public Collection<Rechnung>findRechnungenAll(@Context UriInfo uriInfo) {
		Collection<Rechnung> rechnungen = rechnungService.findRechnungAll();
		for (Rechnung r : rechnungen) {
			uriHelperRechnung.updateUriRechnung(r, uriInfo);
		}
		
		return rechnungen;
	} 
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Rechnung findRechnungById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Rechnung rechnung = rechnungService.findRechnungById(id);
		if (rechnung == null) {
			final String msg = "Keine Rechnung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperRechnung.updateUriRechnung(rechnung, uriInfo);
		return rechnung;
	}
}
