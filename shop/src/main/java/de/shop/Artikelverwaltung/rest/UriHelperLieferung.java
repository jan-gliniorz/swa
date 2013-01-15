package de.shop.Artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Util.Log;


@ApplicationScoped
@Log
public class UriHelperLieferung {

	@Inject
	private UriHelperLieferungsposition uriHelperLieferungsposition;
	
	public void updateUriLieferung(Lieferung lieferung, UriInfo uriInfo) {
//
//		final List<Lieferungsposition> lieferungspositionen = lieferung.getLieferungsposition();
//		
//		if (lieferungspositionen != null && !lieferungspositionen.isEmpty()) {

			lieferung.setLieferungspositionUri(
					  uriHelperLieferungsposition.getUriLieferungspositionenByLieferungId(lieferung, uriInfo));			
	}
	
	public URI getUriLieferung(Lieferung lieferung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LieferungResource.class)
		                             .path(LieferungResource.class, "findLieferungById");
		final URI uri = ub.build(lieferung.getId());
		return uri;
	}
}
