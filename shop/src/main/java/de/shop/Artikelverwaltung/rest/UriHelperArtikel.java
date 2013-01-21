package de.shop.Artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Util.Log;

@ApplicationScoped
@Log
public class UriHelperArtikel {

	@Inject
	private UriHelperLagerposition uriHelperLagerpositionen;
	
	public void updateUriArtikel(Artikel artikel, UriInfo uriInfo) {	
		artikel.setLagerpositionenUri(uriHelperLagerpositionen.getUriLagerpositionenByArtikel(artikel, uriInfo));
	}

	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ArtikelResource.class)
		                             .path(ArtikelResource.class, "findArtikelById");
		final URI uri = ub.build(artikel.getId());
		return uri;
	}
}
