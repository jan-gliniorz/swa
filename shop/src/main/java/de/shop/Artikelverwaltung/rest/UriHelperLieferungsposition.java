package de.shop.Artikelverwaltung.rest;


import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;


@ApplicationScoped
@Log
public class UriHelperLieferungsposition {

	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Inject
	private ArtikelService as;
	
	public void updateReferenceLagerposition(Lieferungsposition lieferungsposition, HttpHeaders headers) {

		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		// Artikel Referenz setzen
		final String artikelUri = lieferungsposition.getArtikelUri().toString();
//		LOGGER.fine("Artikelreferenz setzen URI: " + artikelUri);
		final int artikelIdStartPos = artikelUri.lastIndexOf('/') + 1;
		final String artikelIdStr = artikelUri.substring(artikelIdStartPos);
		Long artikelId = null;
		try {
			artikelId = Long.valueOf(artikelIdStr);
		}
		catch (NumberFormatException e) {
			throw new NotFoundException("Kein Artikel gefunden mit der ID " + artikelIdStr);
		}
		
		final Artikel artikel = as.findArtikelByID(artikelId, ArtikelService.FetchType.NUR_Artikel, locale);
		if (artikel == null) {
			throw new NotFoundException("Kein Artikel gefunden mit der ID " + artikelId);
		}
//		LOGGER.fine("Artikel aus DB: " + artikel);
		
		lieferungsposition.setArtikel(artikel);
	}
	
	public void updateUriLieferungsposition(Lieferungsposition lieferungsposition, UriInfo uriInfo) {
		lieferungsposition.setArtikelUri(uriHelperArtikel.getUriArtikel(lieferungsposition.getArtikel(), uriInfo));
	}
	
	public URI getUriLieferungsposition(Lieferungsposition lieferungsposition, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LieferungspositionResource.class)
		                             .path(LieferungspositionResource.class, "findLieferungspositionById");
		final URI uri = ub.build(lieferungsposition.getId());
		return uri;
	}
	
	public URI getUriLieferungspositionenByLieferungId(Lieferung lieferung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LieferungspositionResource.class)
//		                             .path(LieferungspositionResource.class, "findLieferungspositionByLieferungId")
		                             .queryParam("lieferungid", lieferung.getId());
		final URI uri = ub.build(lieferung.getId());
		return uri;
	}
}
