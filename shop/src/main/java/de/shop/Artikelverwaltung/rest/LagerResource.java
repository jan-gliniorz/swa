//package de.shop.Artikelverwaltung.rest;
//
//import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
//
//import java.lang.invoke.MethodHandles;
//import java.net.URI;
//import java.util.Collection;
//import java.util.List;
//import java.util.Locale;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.enterprise.context.RequestScoped;
//import javax.inject.Inject;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.UriInfo;
//
//import org.jboss.logging.Logger;
//import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
//
//import de.shop.Artikelverwaltung.domain.Lager;
//import de.shop.Artikelverwaltung.service.LagerService;
//import de.shop.Util.Log;
//import de.shop.Util.NotFoundException;
//import de.shop.Util.RestLocaleHelper;
//
//
//@Path("/lager")
//@Produces(APPLICATION_JSON)
//@Consumes
//@RequestScoped
//@Log
//public class LagerResource {
//	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
//	
//	@Inject
//	private LagerService ls;
//	
//	@Inject
//	private UriHelperLager uriHelperLager;
//	
//	@PostConstruct
//	private void postConstruct() {
//		LOGGER.debugf("CDI-faehiges Bean %s {0} wurde erzeugt", this);
//	}
//	
//	@PreDestroy
//	private void preDestroy() {
//		LOGGER.debugf("CDI-faehiges Bean %s {0} wird geloescht", this);
//	}	
//	
//	@GET
//	@Path("{id:[1-9][0-9]*}")
//	public Lager findLagerById(@PathParam("id") Long id,
//							   @Context UriInfo uriInfo, 
//							   @Context HttpHeaders headers) {
//		final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
//		final Lager lager = ls.findLagerById(id, locale);
//		if (lager == null) {
//			final String msg = "Keine Lager gefunden mit der ID" + id;
//			throw new NotFoundException(msg);
//		}
//		
//		uriHelperLager.updateUriLager(lager, uriInfo);
//	
//		return lager;
//	}
//	
//	@GET
//	@Wrapped(element = "lager")
//	public Collection<Lager> findLagerAll(@Context UriInfo uriInfo) {
//		Collection<Lager> lager = ls.findLagerAll(LagerService.FetchType.NUR_Lager, LagerService.OrderType.ID);
//		for (Lager a : lager) {
//			uriHelperLager.updateUriLager(a, uriInfo);
//		}
//		return lager;
//		
//		
//	}
//	
//	
//	@PUT
//	@Consumes(APPLICATION_JSON)
//	@Produces
//	public void updateLager(Lager lager, 
//							@Context UriInfo uriInfo,
//							@Context HttpHeaders headers) {
//		
//		final List<Locale> locales = headers.getAcceptableLanguages();
//		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
//		
//		Lager lagerOrig = ls.findLagerById(lager.getId(), locale);
//		if (lagerOrig == null) {
//			final String msg = "Kein Lager gefunden mit der ID" + lager.getId();
//			throw new NotFoundException(msg);
//		}
//		
//		LOGGER.debugf("Lager vorher: %s", lagerOrig);
//		lagerOrig.setValues(lager);
//		LOGGER.debugf("Lager nachher: %s", lagerOrig);
//		lager = ls.updateLager(lagerOrig, locale);
//
//		if (lager == null) {
//			final String msg = "Kein Lager gefunden mit der ID " + lagerOrig.getId();
//			throw new NotFoundException(msg);
//		}
//	}
//	
//	@POST
//	@Consumes(APPLICATION_JSON)
//	@Produces
//	public Response createLager(Lager lager, 
//								@Context UriInfo uriInfo,
//								@Context HttpHeaders headers) {	
//		Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);		
//		ls.createLager(lager, locale);
//		final URI lagerUri = uriHelperLager.getUriLager(lager, uriInfo);
//		final Response response = Response.created(lagerUri).build();
//		LOGGER.debugf(lagerUri.toString());
//		
//		return response;
//	}
//	
//	@Path("{id:[0-9]+}")
//	@DELETE
//	@Produces
//	public void deleteLager(@PathParam("id") Long id, 
//							@Context HttpHeaders headers) {
//	final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
//	final Lager lager = ls.findLagerById(id, locale);
//	if (lager == null) {
//		String msg = "Kein Lager gefunden mit der ID:" + id;
//		throw new NotFoundException(msg);
//	}
//	
//	ls.deleteLager(lager);		
//	}	
//}
