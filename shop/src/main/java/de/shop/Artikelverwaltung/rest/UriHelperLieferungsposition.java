package de.shop.Artikelverwaltung.rest;


import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Util.Log;


@ApplicationScoped
@Log
public class UriHelperLieferungsposition {

	@Inject
	private UriHelperArtikel uriHelperArtikel;

	public void updateUriLieferungsposition(Lieferungsposition lieferungsposition, UriInfo uriInfo) {

		final Artikel artikel = lieferungsposition.getArtikel();
		
		if (artikel != null) {
			final URI artikelUri = uriHelperArtikel.getUriArtikel(lieferungsposition.getArtikel(), uriInfo);
			lieferungsposition.setArtikelUri(artikelUri);
		}
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
