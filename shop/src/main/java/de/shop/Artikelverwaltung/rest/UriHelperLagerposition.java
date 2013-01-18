package de.shop.Artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Util.Log;

@ApplicationScoped
@Log
public class UriHelperLagerposition {
	
	@Inject
	UriHelperArtikel uriHelperArtikel;
	
	@Inject
	UriHelperLager uriHelperLager;
	
	public void updateUriLagerposition(Lagerposition lagerposition, UriInfo uriInfo) {
		lagerposition.setLagerUri(uriHelperLager.getUriLager(lagerposition.getLager(), uriInfo));
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
	
	public URI getUriLagerpositionenByLager(Lager lager, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LagerpositionResource.class)
		                             .path(LagerpositionResource.class, "findLagerpositionByLagerId");
		final URI uri = ub.build(lager.getId());
		return uri;
	}
}
