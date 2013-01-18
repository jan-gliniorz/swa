package de.shop.Artikelverwaltung.rest;


import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Util.Log;


@ApplicationScoped
@Log
public class UriHelperLager {
	@Inject
	private UriHelperLagerposition uriHelperLagerposition;
	
	public URI getUriLager(Lager lager, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LagerResource.class)
		                             .path(LagerResource.class, "findLagerById");
		final URI uri = ub.build(lager.getId());
		return uri;
	}
	
	public void updateUriLager(Lager lager, UriInfo uriInfo) {
		lager.setLagerpositionenUri(uriHelperLagerposition.getUriLagerpositionenByLager(lager, uriInfo));
	}
	
}
