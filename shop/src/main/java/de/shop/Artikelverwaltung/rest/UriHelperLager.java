package de.shop.Artikelverwaltung.rest;


import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Util.Log;


@ApplicationScoped
@Log
public class UriHelperLager {
	public URI getUriLager(Lager lager, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LagerResource.class)
		                             .path(LagerResource.class, "findLagerById");
		final URI uri = ub.build(lager.getId());
		return uri;
	}
}
