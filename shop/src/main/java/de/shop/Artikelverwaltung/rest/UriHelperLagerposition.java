package de.shop.Artikelverwaltung.rest;

import java.net.URI;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Artikel;
//import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.service.LagerService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;
import de.shop.Util.RestLocaleHelper;

@ApplicationScoped
@Log
public class UriHelperLagerposition {
	
	@Inject
	private LagerService lagerService;
	
	@Inject
	private ArtikelService artikelService;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
//	@Inject
//	private UriHelperLager uriHelperLager;
	
	public void updateReferenceLagerposition(Lagerposition lagerposition, HttpHeaders headers) {
		final Locale locale = RestLocaleHelper.getLocalFromHttpHeaders(headers);
		
		// Lager Referenz setzen
//		String lagerUri = lagerposition.getLagerUri().toString();
//		int lagerIdStartPost = lagerUri.lastIndexOf('/') + 1;
//		String lagerIdStr = lagerUri.substring(lagerIdStartPost);
//		Long lagerId = null;
//		try {
//			lagerId = Long.valueOf(lagerIdStr);
//		}
//		catch (NumberFormatException e) {
//			throw new NotFoundException("Kein Lager vorhanden mit der ID " + lagerIdStr, e);
//		}
		
//		Lager lager = lagerService.findLagerById(lagerId, locale);
//		if (lager == null) {
//			throw new NotFoundException("Kein Lager gefunden mit der ID " + lagerId);
//		}
		
//		lagerposition.setLager(lager);
		
		// Artikel Referenz setzen
		final String artikelUri = lagerposition.getArtikelUri().toString();
		final int artikelIdStartPos = artikelUri.lastIndexOf('/') + 1;
		final String artikelIdStr = artikelUri.substring(artikelIdStartPos);
		Long artikelId = null;
		try {
			artikelId = Long.valueOf(artikelIdStr);
		}
		catch (NumberFormatException e) {
			throw new NotFoundException("Kein Artikel gefunden mit der ID " + artikelIdStr);
		}
		
		final Artikel artikel = artikelService.findArtikelByID(artikelId, ArtikelService.FetchType.NUR_Artikel, locale);
		if (artikel == null) {
			throw new NotFoundException("Kein Artikel gefunden mit der ID " + artikelId);
		}
		
		lagerposition.setArtikel(artikel);
	}
	
	
	public void updateUriLagerposition(Lagerposition lagerposition, UriInfo uriInfo) {
//		lagerposition.setLagerUri(uriHelperLager.getUriLager(lagerposition.getLager(), uriInfo));
		lagerposition.setArtikelUri(uriHelperArtikel.getUriArtikel(lagerposition.getArtikel(), uriInfo));
	}

	public URI getUriLagerposition(Lagerposition lagerposition, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LagerpositionResource.class)
		                             .path(LagerpositionResource.class, "findLagerpositionById");
		final URI uri = ub.build(lagerposition.getId());
		return uri;
	}
	
	public URI getUriLagerpositionenByArtikel(Artikel artikel, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LagerpositionResource.class)
		                             .queryParam("artikelid", artikel.getId());
		final URI uri = ub.build(artikel.getId());
		return uri;
	}
	
//	public URI getUriLagerpositionenByLager(Lager lager, UriInfo uriInfo) {
//		final UriBuilder ub = uriInfo.getBaseUriBuilder()
//		                             .path(LagerpositionResource.class)
//		                             .path(LagerpositionResource.class, "findLagerpositionByLagerId");
//		final URI uri = ub.build(lager.getId());
//		return uri;
//	}
}
